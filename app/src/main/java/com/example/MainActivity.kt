package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ai.ChatMessage
import com.example.ai.MyraAiService
import com.example.control.ActionResult
import com.example.control.MyraDeviceController
import com.example.data.MyraFaceMode
import com.example.data.MyraPreferences
import com.example.ui.components.EdgeGlowOverlay
import com.example.ui.components.MyraBottomBar
import com.example.ui.components.MyraHudBar
import com.example.ui.components.MyraNavDestination
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ConsoleScreen
import com.example.ui.screens.DeviceControlScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.voice.MyraVoiceManager
import com.example.voice.MyraVoiceState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var preferences: MyraPreferences
    private lateinit var deviceController: MyraDeviceController
    private lateinit var voiceManager: MyraVoiceManager
    private lateinit var aiService: MyraAiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferences = MyraPreferences(this)
        deviceController = MyraDeviceController(this)

        setContent {
            MyApplicationTheme {
                MyraUltraApp(
                    preferences = preferences,
                    deviceController = deviceController,
                    activity = this
                )
            }
        }
    }
}

@Composable
fun MyraUltraApp(
    preferences: MyraPreferences,
    deviceController: MyraDeviceController,
    activity: ComponentActivity
) {
    val coroutineScope = rememberCoroutineScope()
    var currentDestination by remember { mutableStateOf(MyraNavDestination.CONSOLE) }
    var inAboutScreen by remember { mutableStateOf(false) }
    var currentFace by remember { mutableStateOf(preferences.faceMode) }
    var isA11yActive by remember { mutableStateOf(deviceController.isAccessibilityActive()) }

    val messages = remember { mutableStateListOf<ChatMessage>() }
    var latestActionResult by remember { mutableStateOf<ActionResult?>(null) }
    var liveTranscript by remember { mutableStateOf("") }

    // Initialize AI Service
    val aiService = remember {
        MyraAiService(preferences, deviceController)
    }

    // Initialize Voice Manager
    var voiceManagerInstance: MyraVoiceManager? by remember { mutableStateOf(null) }
    val voiceManager = remember {
        MyraVoiceManager(activity) { recognizedSpeech ->
            liveTranscript = recognizedSpeech
            coroutineScope.launch {
                messages.add(ChatMessage(text = recognizedSpeech, isUser = true))
                val aiReply = aiService.sendMessage(recognizedSpeech)
                messages.add(aiReply)
                latestActionResult = aiReply.actionResult
                if (preferences.speechSynthesisEnabled && aiReply.text.isNotBlank()) {
                    voiceManagerInstance?.speak(aiReply.text)
                }
            }
        }.also {
            voiceManagerInstance = it
        }
    }

    val voiceState by voiceManager.voiceState.collectAsState()
    val audioLevel by voiceManager.audioRmsLevel.collectAsState()

    // Runtime Permission Launcher for Audio & Camera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val audioGranted = perms[Manifest.permission.RECORD_AUDIO] == true
        if (audioGranted) {
            voiceManager.startListening()
        } else {
            Toast.makeText(activity, "Microphone permission is required for voice input", Toast.LENGTH_SHORT).show()
        }
    }

    // Periodically check A11y status when returning to foreground
    DisposableEffect(Unit) {
        isA11yActive = deviceController.isAccessibilityActive()
        onDispose {
            voiceManager.destroy()
        }
    }

    LaunchedEffect(Unit) {
        // Initial greeting
        val initialGreeting = "MYRA ULTRA initialized. Systems online and ready. How may I assist you today?"
        messages.add(ChatMessage(text = initialGreeting, isUser = false))
        if (preferences.speechSynthesisEnabled) {
            voiceManager.speak(initialGreeting)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyraHudBar(
                    currentFace = currentFace,
                    isA11yActive = isA11yActive,
                    onToggleFace = {
                        val newFace = if (currentFace == MyraFaceMode.FACE_ONE) MyraFaceMode.FACE_TWO else MyraFaceMode.FACE_ONE
                        currentFace = newFace
                        preferences.faceMode = newFace
                    },
                    onA11yClick = {
                        deviceController.openAccessibilitySettings()
                        isA11yActive = deviceController.isAccessibilityActive()
                    }
                )
            },
            bottomBar = {
                if (!inAboutScreen) {
                    MyraBottomBar(
                        currentDestination = currentDestination,
                        onSelectDestination = { dest ->
                            currentDestination = dest
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (inAboutScreen) {
                    AboutScreen(onBack = { inAboutScreen = false })
                } else {
                    when (currentDestination) {
                        MyraNavDestination.CONSOLE -> {
                            ConsoleScreen(
                                currentFace = currentFace,
                                voiceState = voiceState,
                                audioLevel = audioLevel,
                                transcript = liveTranscript,
                                latestActionResult = latestActionResult,
                                onVoiceToggle = {
                                    if (voiceState == MyraVoiceState.LISTENING) {
                                        voiceManager.stopListening()
                                    } else {
                                        val hasRecordAudio = ContextCompat.checkSelfPermission(
                                            activity,
                                            Manifest.permission.RECORD_AUDIO
                                        ) == PackageManager.PERMISSION_GRANTED

                                        if (hasRecordAudio) {
                                            voiceManager.startListening()
                                        } else {
                                            val requested = mutableListOf(Manifest.permission.RECORD_AUDIO)
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                requested.add(Manifest.permission.POST_NOTIFICATIONS)
                                            }
                                            permissionLauncher.launch(requested.toTypedArray())
                                        }
                                    }
                                },
                                onSubmitText = { text ->
                                    coroutineScope.launch {
                                        messages.add(ChatMessage(text = text, isUser = true))
                                        val reply = aiService.sendMessage(text)
                                        messages.add(reply)
                                        latestActionResult = reply.actionResult
                                        if (preferences.speechSynthesisEnabled && reply.text.isNotBlank()) {
                                            voiceManager.speak(reply.text)
                                        }
                                    }
                                },
                                onQuickAction = { actionQuery ->
                                    val result = deviceController.executeAction(actionQuery)
                                    latestActionResult = result
                                    messages.add(ChatMessage(text = "Execute: $actionQuery", isUser = true))
                                    messages.add(ChatMessage(text = result.message, isUser = false, actionResult = result))
                                    if (preferences.speechSynthesisEnabled) {
                                        voiceManager.speak(result.message)
                                    }
                                },
                                onToggleFace = {
                                    val newFace = if (currentFace == MyraFaceMode.FACE_ONE) MyraFaceMode.FACE_TWO else MyraFaceMode.FACE_ONE
                                    currentFace = newFace
                                    preferences.faceMode = newFace
                                }
                            )
                        }

                        MyraNavDestination.CHAT -> {
                            ChatScreen(
                                messages = messages,
                                onSendMessage = { text ->
                                    coroutineScope.launch {
                                        messages.add(ChatMessage(text = text, isUser = true))
                                        val reply = aiService.sendMessage(text)
                                        messages.add(reply)
                                        latestActionResult = reply.actionResult
                                        if (preferences.speechSynthesisEnabled && reply.text.isNotBlank()) {
                                            voiceManager.speak(reply.text)
                                        }
                                    }
                                },
                                onSpeakMessage = { txt ->
                                    voiceManager.speak(txt)
                                },
                                onVoiceClick = {
                                    val hasRecordAudio = ContextCompat.checkSelfPermission(
                                        activity,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasRecordAudio) {
                                        voiceManager.startListening()
                                    } else {
                                        permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                                    }
                                }
                            )
                        }

                        MyraNavDestination.CONTROL -> {
                            DeviceControlScreen(
                                deviceController = deviceController,
                                isA11yActive = isA11yActive,
                                onOpenA11ySettings = {
                                    deviceController.openAccessibilitySettings()
                                },
                                onActionResult = { res ->
                                    latestActionResult = res
                                    isA11yActive = deviceController.isAccessibilityActive()
                                    messages.add(ChatMessage(text = "Device Action: ${res.command}", isUser = true))
                                    messages.add(ChatMessage(text = res.message, isUser = false, actionResult = res))
                                    if (preferences.speechSynthesisEnabled) {
                                        voiceManager.speak(res.message)
                                    }
                                }
                            )
                        }

                        MyraNavDestination.STUDIO -> {
                            StudioScreen(
                                onGenerateCode = { prompt, onDone ->
                                    coroutineScope.launch {
                                        val reply = aiService.sendMessage(prompt)
                                        onDone(reply.text)
                                    }
                                }
                            )
                        }

                        MyraNavDestination.SETTINGS -> {
                            SettingsScreen(
                                preferences = preferences,
                                isA11yActive = isA11yActive,
                                onOpenA11ySettings = {
                                    deviceController.openAccessibilitySettings()
                                    isA11yActive = deviceController.isAccessibilityActive()
                                },
                                onNavigateAbout = {
                                    inAboutScreen = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Ambient Edge Glow Overlay (recreating myra_edge_glow.py)
        EdgeGlowOverlay(
            voiceState = voiceState,
            enabled = preferences.edgeGlowEnabled
        )
    }
}

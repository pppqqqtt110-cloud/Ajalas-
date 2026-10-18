package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.control.ActionResult
import com.example.data.MyraFaceMode
import com.example.ui.faces.FaceOneReactor
import com.example.ui.faces.FaceTwoAvatar
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.voice.MyraVoiceState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConsoleScreen(
    currentFace: MyraFaceMode,
    voiceState: MyraVoiceState,
    audioLevel: Float,
    transcript: String,
    latestActionResult: ActionResult?,
    onVoiceToggle: () -> Unit,
    onSubmitText: (String) -> Unit,
    onQuickAction: (String) -> Unit,
    onToggleFace: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Telemetry Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberSurface)
                .border(1.dp, Color(0xFF1E284A), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NEURAL STATE",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
                Text(
                    text = voiceState.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = when (voiceState) {
                        MyraVoiceState.LISTENING -> NeonCyan
                        MyraVoiceState.THINKING -> CyberPurple
                        MyraVoiceState.SPEAKING -> NeonMagenta
                        MyraVoiceState.IDLE -> CyberGreen
                    }
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "VISUAL MATRIX",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
                Text(
                    text = if (currentFace == MyraFaceMode.FACE_ONE) "FACE 1 • REACTOR" else "FACE 2 • PERSONA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (currentFace == MyraFaceMode.FACE_ONE) NeonCyan else NeonMagenta
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "SYS FREQ",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
                Text(
                    text = "4.8 GHz",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = NeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Center Visual Face Component (Face One vs Face Two)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentFace == MyraFaceMode.FACE_ONE) {
                FaceOneReactor(
                    voiceState = voiceState,
                    audioLevel = audioLevel,
                    onTap = onVoiceToggle
                )
            } else {
                FaceTwoAvatar(
                    voiceState = voiceState,
                    audioLevel = audioLevel,
                    onTap = onVoiceToggle
                )
            }
        }

        // Live Speech / Subtitle Transcript
        AnimatedVisibility(
            visible = transcript.isNotEmpty() || voiceState != MyraVoiceState.IDLE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF131A2F))
                    .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = if (transcript.isNotEmpty()) "▶ $transcript" else "Listening for voice input...",
                    color = if (transcript.isNotEmpty()) TextPrimary else NeonCyan,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Latest Action Result Badge
        if (latestActionResult != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (latestActionResult.success) Color(0xFF002E1B) else Color(0xFF38101C))
                    .border(
                        1.dp,
                        if (latestActionResult.success) CyberGreen else NeonMagenta,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = "ACTION: ${latestActionResult.command.uppercase()} • ${if (latestActionResult.success) "SUCCESS" else "FAILED"}",
                        color = if (latestActionResult.success) CyberGreen else NeonMagenta,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = latestActionResult.message,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Action Chips
        Text(
            text = "QUICK COMMANDS & ACTIONS",
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "Go Home",
                "Press Back",
                "Open Recents",
                "Read Screen",
                "Flashlight",
                "Open YouTube",
                "Battery Level",
                "Toggle Face"
            ).forEach { action ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberSurfaceElevated)
                        .border(1.dp, Color(0xFF293659), RoundedCornerShape(16.dp))
                        .clickable {
                            if (action == "Toggle Face") onToggleFace() else onQuickAction(action)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("quick_${action.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = action,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Voice Trigger Orb & Text Command Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("console_text_input"),
                placeholder = {
                    Text(
                        text = "Command MYRA ULTRA...",
                        color = TextSecondary.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color(0xFF233052),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Mic Trigger Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (voiceState == MyraVoiceState.LISTENING) NeonMagenta
                        else Color(0xFF14243E)
                    )
                    .border(
                        1.5.dp,
                        if (voiceState == MyraVoiceState.LISTENING) NeonMagenta else NeonCyan,
                        CircleShape
                    )
                    .clickable(onClick = onVoiceToggle)
                    .testTag("voice_mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (voiceState == MyraVoiceState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (voiceState == MyraVoiceState.LISTENING) Color.White else NeonCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Send Button
            if (textInput.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        val cmd = textInput
                        textInput = ""
                        onSubmitText(cmd)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                        .testTag("console_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Command",
                        tint = Color(0xFF001C26),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

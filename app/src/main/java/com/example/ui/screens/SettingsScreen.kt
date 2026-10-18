package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MyraFaceMode
import com.example.data.MyraPersonalityMode
import com.example.data.MyraPreferences
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.QuantumAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    preferences: MyraPreferences,
    isA11yActive: Boolean,
    onOpenA11ySettings: () -> Unit,
    onNavigateAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var apiKeyText by remember { mutableStateOf(preferences.apiKey) }
    var apiKeyVisible by remember { mutableStateOf(false) }
    var faceMode by remember { mutableStateOf(preferences.faceMode) }
    var personalityMode by remember { mutableStateOf(preferences.personalityMode) }
    var edgeGlow by remember { mutableStateOf(preferences.edgeGlowEnabled) }
    var guardianSecurity by remember { mutableStateOf(preferences.guardianSecurityEnabled) }
    var pitch by remember { mutableFloatStateOf(preferences.voicePitch) }
    var speed by remember { mutableFloatStateOf(preferences.voiceSpeed) }
    var ttsEnabled by remember { mutableStateOf(preferences.speechSynthesisEnabled) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SYSTEM CONFIGURATION & NEURAL PARAMETERS",
            color = NeonCyan,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        // 1. Gemini AI API Key
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF212D4C), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GEMINI API CREDENTIALS",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Configured via AI Studio Secrets (BuildConfig) or override with your custom API key here:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiKeyText,
                    onValueChange = {
                        apiKeyText = it
                        preferences.apiKey = it
                    },
                    modifier = Modifier.fillMaxWidth().testTag("api_key_input"),
                    placeholder = { Text("AIzaSy...", color = TextSecondary.copy(alpha = 0.5f), fontSize = 11.sp) },
                    visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { apiKeyVisible = !apiKeyVisible }) {
                            Icon(
                                imageVector = if (apiKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle visibility",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF131A2F),
                        unfocusedContainerColor = Color(0xFF131A2F),
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF212D4C),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        // 2. Personality & Face Modes
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF212D4C), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "VISUAL INTERFACE & MODEL PERSONA",
                    color = NeonMagenta,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // Face Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Face Mode", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (faceMode == MyraFaceMode.FACE_ONE) "Face One (Neural Arc Reactor Core)" else "Face Two (Cyber Persona Avatar)",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    Row {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (faceMode == MyraFaceMode.FACE_ONE) NeonCyan else Color(0xFF1E284A))
                                .clickable {
                                    faceMode = MyraFaceMode.FACE_ONE
                                    preferences.faceMode = MyraFaceMode.FACE_ONE
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("FACE 1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (faceMode == MyraFaceMode.FACE_ONE) Color.Black else TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (faceMode == MyraFaceMode.FACE_TWO) NeonMagenta else Color(0xFF1E284A))
                                .clickable {
                                    faceMode = MyraFaceMode.FACE_TWO
                                    preferences.faceMode = MyraFaceMode.FACE_TWO
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("FACE 2", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (faceMode == MyraFaceMode.FACE_TWO) Color.White else TextPrimary)
                        }
                    }
                }

                // Personality
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("System Prompt Mode", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (personalityMode == MyraPersonalityMode.ULTRA_AUTONOMOUS) "Ultra Autonomous System Operator" else "Normal Everyday Assistant",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = personalityMode == MyraPersonalityMode.ULTRA_AUTONOMOUS,
                        onCheckedChange = { checked ->
                            val newMode = if (checked) MyraPersonalityMode.ULTRA_AUTONOMOUS else MyraPersonalityMode.NORMAL_ASSISTANT
                            personalityMode = newMode
                            preferences.personalityMode = newMode
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF004D5A))
                    )
                }

                // Ambient Edge Glow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Ambient Neon Edge Glow", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Signature edge lighting reacting to speech & listening", color = TextSecondary, fontSize = 10.sp)
                    }
                    Switch(
                        checked = edgeGlow,
                        onCheckedChange = {
                            edgeGlow = it
                            preferences.edgeGlowEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF004D5A))
                    )
                }

                // Guardian Security
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Guardian Security Lock", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Guardian biometric verification before sensitive device changes", color = TextSecondary, fontSize = 10.sp)
                    }
                    Switch(
                        checked = guardianSecurity,
                        onCheckedChange = {
                            guardianSecurity = it
                            preferences.guardianSecurityEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyberPurple, checkedTrackColor = Color(0xFF450DB8))
                    )
                }
            }
        }

        // 3. Speech Synthesis & Voice Audio Controls
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF212D4C), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = QuantumAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VOICE SYNTHESIS (TTS)",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Switch(
                        checked = ttsEnabled,
                        onCheckedChange = {
                            ttsEnabled = it
                            preferences.speechSynthesisEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = QuantumAmber, checkedTrackColor = Color(0xFF664800))
                    )
                }

                if (ttsEnabled) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Voice Pitch (${String.format("%.2f", pitch)}x)", fontSize = 11.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    Slider(
                        value = pitch,
                        onValueChange = {
                            pitch = it
                            preferences.voicePitch = it
                        },
                        valueRange = 0.6f..1.6f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Speech Rate (${String.format("%.2f", speed)}x)", fontSize = 11.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    Slider(
                        value = speed,
                        onValueChange = {
                            speed = it
                            preferences.voiceSpeed = it
                        },
                        valueRange = 0.6f..1.6f,
                        colors = SliderDefaults.colors(thumbColor = NeonMagenta, activeTrackColor = NeonMagenta)
                    )
                }
            }
        }

        // 4. Accessibility Setup Flow Link
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF212D4C), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessibilityNew, contentDescription = null, tint = if (isA11yActive) CyberGreen else QuantumAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ANDROID DEVICE CONTROL BRIDGE",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Controls apps, home/back gestures, screen inspection, and notifications. Status: ${if (isA11yActive) "Active & Bound" else "Inactive"}.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenA11ySettings,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2C4A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OPEN ACCESSIBILITY SETTINGS", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // 5. About & Credits Button
        Button(
            onClick = onNavigateAbout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF151D36)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberPurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .testTag("about_team_button")
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = CyberPurple, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("ABOUT MYRA ULTRA & CREATORS", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

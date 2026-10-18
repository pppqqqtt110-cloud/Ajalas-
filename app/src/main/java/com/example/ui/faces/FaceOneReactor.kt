package com.example.ui.faces

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.QuantumAmber
import com.example.ui.theme.TextSecondary
import com.example.voice.MyraVoiceState
import kotlin.math.cos
import kotlin.math.sin

/**
 * FACE ONE: Cybernetic Neural Arc Reactor Core.
 * Represents MYRA ULTRA's primary cybernetic visual identity with multi-layer rotating rings,
 * audio-reactive waveforms, and pulse energy states.
 */
@Composable
fun FaceOneReactor(
    voiceState: MyraVoiceState,
    audioLevel: Float,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FaceOneRings")

    // Primary clockwise rotation
    val rotation1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Ring1Rot"
    )

    // Secondary counter-clockwise rotation
    val rotation2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Ring2Rot"
    )

    // Inner core fast rotation
    val rotation3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Ring3Rot"
    )

    // Core breathing pulsation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CorePulse"
    )

    // Wave ring expansion
    val waveExpand by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveExpand"
    )

    val coreColor = when (voiceState) {
        MyraVoiceState.LISTENING -> NeonCyan
        MyraVoiceState.THINKING -> CyberPurple
        MyraVoiceState.SPEAKING -> NeonMagenta
        MyraVoiceState.IDLE -> NeonCyan
    }

    val stateText = when (voiceState) {
        MyraVoiceState.LISTENING -> "NEURAL LINK: LISTENING"
        MyraVoiceState.THINKING -> "QUANTUM CORE: COMPUTING"
        MyraVoiceState.SPEAKING -> "AUDIO EMITTER: ACTIVE"
        MyraVoiceState.IDLE -> "CORE STATUS: ONLINE"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTap
                )
                .testTag("face_one_reactor"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = size.minDimension / 2f * 0.85f

                // Audio reactive boost
                val dynamicBoost = if (voiceState == MyraVoiceState.SPEAKING || voiceState == MyraVoiceState.LISTENING) {
                    audioLevel.coerceIn(0f, 1f) * 20f
                } else 0f

                // Outer Glowing Ambient Aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            coreColor.copy(alpha = 0.35f),
                            coreColor.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius * 1.15f
                    ),
                    radius = baseRadius * 1.15f,
                    center = center
                )

                // Expanding sonar ripple when listening / speaking
                if (voiceState == MyraVoiceState.LISTENING || voiceState == MyraVoiceState.SPEAKING) {
                    val rippleRadius = baseRadius * (0.5f + waveExpand * 0.55f)
                    drawCircle(
                        color = coreColor.copy(alpha = (1f - waveExpand) * 0.5f),
                        radius = rippleRadius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Layer 1: Outermost HUD Scale Ring (Segmented)
                rotate(rotation1, pivot = center) {
                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.45f),
                        radius = baseRadius * 0.95f + dynamicBoost,
                        center = center,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f, 4f, 8f), 0f)
                        )
                    )

                    // 4 Quadrant Accent Marks
                    val markLen = 8.dp.toPx()
                    for (i in 0 until 4) {
                        val angle = (i * 90.0).let { Math.toRadians(it) }
                        val rOut = baseRadius * 0.98f
                        val rIn = rOut - markLen
                        drawLine(
                            color = NeonCyan,
                            start = Offset(center.x + (rIn * cos(angle)).toFloat(), center.y + (rIn * sin(angle)).toFloat()),
                            end = Offset(center.x + (rOut * cos(angle)).toFloat(), center.y + (rOut * sin(angle)).toFloat()),
                            strokeWidth = 2.5.dp.toPx()
                        )
                    }
                }

                // Layer 2: Middle Segmented Cyber Ring
                rotate(rotation2, pivot = center) {
                    drawCircle(
                        color = NeonMagenta.copy(alpha = 0.65f),
                        radius = baseRadius * 0.78f,
                        center = center,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 15f), 0f)
                        )
                    )

                    // Rotating Node Dots
                    for (i in 0 until 6) {
                        val angle = (i * 60.0).let { Math.toRadians(it) }
                        val nodeDist = baseRadius * 0.78f
                        val pos = Offset(center.x + (nodeDist * cos(angle)).toFloat(), center.y + (nodeDist * sin(angle)).toFloat())
                        drawCircle(
                            color = if (i % 2 == 0) NeonCyan else NeonMagenta,
                            radius = 3.dp.toPx(),
                            center = pos
                        )
                    }
                }

                // Layer 3: Inner Harmonic Violet Track
                rotate(rotation3, pivot = center) {
                    drawCircle(
                        color = CyberPurple.copy(alpha = 0.8f),
                        radius = baseRadius * 0.60f,
                        center = center,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(45f, 25f, 10f, 25f), 0f)
                        )
                    )
                }

                // Layer 4: Central Arc Core
                val coreRadius = baseRadius * 0.42f * pulseScale + dynamicBoost * 0.5f

                // Deep Dark Core Base
                drawCircle(
                    color = Color(0xFF0F1528),
                    radius = coreRadius,
                    center = center
                )

                // Glowing Inner Plasma
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            coreColor.copy(alpha = 0.95f),
                            coreColor.copy(alpha = 0.60f),
                            Color(0xFF050812)
                        ),
                        center = center,
                        radius = coreRadius
                    ),
                    radius = coreRadius * 0.92f,
                    center = center
                )

                // Central Focus Eye / Pupil
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx() * (1f + (dynamicBoost / 30f)),
                    center = center
                )

                // High-tech Crosshairs in the center
                val crossHair = coreRadius * 0.5f
                drawLine(
                    color = coreColor.copy(alpha = 0.7f),
                    start = Offset(center.x - crossHair, center.y),
                    end = Offset(center.x + crossHair, center.y),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = coreColor.copy(alpha = 0.7f),
                    start = Offset(center.x, center.y - crossHair),
                    end = Offset(center.x, center.y + crossHair),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cyber HUD Core Status Badge
        Text(
            text = stateText,
            color = coreColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "TAP CORE FOR VOICE COMMAND",
            color = TextSecondary.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    }
}

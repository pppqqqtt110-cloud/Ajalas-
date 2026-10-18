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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextSecondary
import com.example.voice.MyraVoiceState
import kotlin.math.sin

/**
 * FACE TWO: Cyber Persona Avatar ("Myra Girl" Visual AI Companion).
 * Recreates the secondary visual face with animated expressions, glowing cybernetic headset,
 * audio-reactive mouth synchronization, and holographic aura.
 */
@Composable
fun FaceTwoAvatar(
    voiceState: MyraVoiceState,
    audioLevel: Float,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "FaceTwoAnim")

    // Breathing float animation
    val breathY by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathY"
    )

    // Holographic halo rotation
    val haloPulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloPulse"
    )

    // Eye blink cycle (0 = open, 1 = closed)
    val blinkProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Blink"
    )

    // Mouth talking oscillation when speaking
    val talkOscillation by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TalkMouth"
    )

    val eyeOpen = if (blinkProgress in 0.93f..0.97f) 0.1f else 1f

    val accentColor = when (voiceState) {
        MyraVoiceState.LISTENING -> NeonCyan
        MyraVoiceState.THINKING -> CyberPurple
        MyraVoiceState.SPEAKING -> NeonMagenta
        MyraVoiceState.IDLE -> NeonCyan
    }

    val stateText = when (voiceState) {
        MyraVoiceState.LISTENING -> "PERSONA: ATTENTIVE"
        MyraVoiceState.THINKING -> "PERSONA: REFLECTING"
        MyraVoiceState.SPEAKING -> "PERSONA: SPEAKING"
        MyraVoiceState.IDLE -> "MYRA PERSONA: ACTIVE"
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
                .testTag("face_two_avatar"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f + breathY
                val scale = size.minDimension / 260f

                // 1. Digital Matrix Hologram Halo Ring
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.25f),
                            CyberPurple.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = 120f * scale * haloPulse
                    ),
                    radius = 120f * scale * haloPulse,
                    center = Offset(cx, cy)
                )

                drawCircle(
                    color = accentColor.copy(alpha = 0.5f),
                    radius = 110f * scale * haloPulse,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.8f * scale)
                )

                // 2. Cybernetic Ears / Headset Nodes (Left & Right)
                val headsetGlow = if (voiceState == MyraVoiceState.SPEAKING) audioLevel * 10f else 0f

                // Left Cyber Headset Node
                drawRoundRect(
                    color = accentColor,
                    topLeft = Offset(cx - 72f * scale, cy - 20f * scale),
                    size = Size(14f * scale, 38f * scale + headsetGlow),
                    cornerRadius = CornerRadius(6f * scale, 6f * scale)
                )
                // Left LED light
                drawCircle(
                    color = Color.White,
                    radius = 3f * scale,
                    center = Offset(cx - 65f * scale, cy - 2f * scale)
                )

                // Right Cyber Headset Node
                drawRoundRect(
                    color = accentColor,
                    topLeft = Offset(cx + 58f * scale, cy - 20f * scale),
                    size = Size(14f * scale, 38f * scale + headsetGlow),
                    cornerRadius = CornerRadius(6f * scale, 6f * scale)
                )
                // Right LED light
                drawCircle(
                    color = Color.White,
                    radius = 3f * scale,
                    center = Offset(cx + 65f * scale, cy - 2f * scale)
                )

                // 3. Hair (Back Layers - Midnight Cyber Blue/Black)
                val backHairPath = Path().apply {
                    moveTo(cx - 62f * scale, cy - 40f * scale)
                    cubicTo(cx - 75f * scale, cy + 30f * scale, cx - 60f * scale, cy + 85f * scale, cx - 45f * scale, cy + 95f * scale)
                    lineTo(cx + 45f * scale, cy + 95f * scale)
                    cubicTo(cx + 60f * scale, cy + 85f * scale, cx + 75f * scale, cy + 30f * scale, cx + 62f * scale, cy - 40f * scale)
                    close()
                }
                drawPath(
                    path = backHairPath,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF1E2640), Color(0xFF101626))
                    )
                )

                // 4. Face Contour (Anime soft V-shape chin)
                val facePath = Path().apply {
                    moveTo(cx - 45f * scale, cy - 25f * scale)
                    cubicTo(cx - 45f * scale, cy + 20f * scale, cx - 28f * scale, cy + 55f * scale, cx, cy + 62f * scale)
                    cubicTo(cx + 28f * scale, cy + 55f * scale, cx + 45f * scale, cy + 20f * scale, cx + 45f * scale, cy - 25f * scale)
                    close()
                }
                drawPath(
                    path = facePath,
                    color = Color(0xFFF7EBE1)
                )

                // Soft blush
                drawCircle(
                    color = Color(0x33FF4081),
                    radius = 10f * scale,
                    center = Offset(cx - 26f * scale, cy + 18f * scale)
                )
                drawCircle(
                    color = Color(0x33FF4081),
                    radius = 10f * scale,
                    center = Offset(cx + 26f * scale, cy + 18f * scale)
                )

                // 5. Stylized Anime Cyber Eyes
                val eyeWidth = 18f * scale
                val eyeHeight = 16f * scale * eyeOpen

                // Left Eye
                if (eyeOpen > 0.3f) {
                    // Sclera / Eye BG
                    drawOval(
                        color = Color.White,
                        topLeft = Offset(cx - 33f * scale, cy - 2f * scale),
                        size = Size(eyeWidth, eyeHeight)
                    )
                    // Iris (Deep Cyan & Violet)
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White, accentColor, CyberPurple),
                            center = Offset(cx - 24f * scale, cy + 4f * scale),
                            radius = 9f * scale
                        ),
                        radius = 8f * scale * eyeOpen,
                        center = Offset(cx - 24f * scale, cy + 5f * scale)
                    )
                    // Catchlight highlight
                    drawCircle(
                        color = Color.White,
                        radius = 2.5f * scale,
                        center = Offset(cx - 26f * scale, cy + 3f * scale)
                    )
                    // Eyelash upper line
                    drawLine(
                        color = Color(0xFF1A1A2E),
                        start = Offset(cx - 35f * scale, cy - 3f * scale),
                        end = Offset(cx - 15f * scale, cy - 1f * scale),
                        strokeWidth = 3f * scale,
                        cap = StrokeCap.Round
                    )
                } else {
                    // Closed Eye line
                    drawLine(
                        color = Color(0xFF1A1A2E),
                        start = Offset(cx - 34f * scale, cy + 4f * scale),
                        end = Offset(cx - 14f * scale, cy + 4f * scale),
                        strokeWidth = 2.5f * scale,
                        cap = StrokeCap.Round
                    )
                }

                // Right Eye
                if (eyeOpen > 0.3f) {
                    drawOval(
                        color = Color.White,
                        topLeft = Offset(cx + 15f * scale, cy - 2f * scale),
                        size = Size(eyeWidth, eyeHeight)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White, accentColor, CyberPurple),
                            center = Offset(cx + 24f * scale, cy + 4f * scale),
                            radius = 9f * scale
                        ),
                        radius = 8f * scale * eyeOpen,
                        center = Offset(cx + 24f * scale, cy + 5f * scale)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.5f * scale,
                        center = Offset(cx + 22f * scale, cy + 3f * scale)
                    )
                    drawLine(
                        color = Color(0xFF1A1A2E),
                        start = Offset(cx + 15f * scale, cy - 1f * scale),
                        end = Offset(cx + 35f * scale, cy - 3f * scale),
                        strokeWidth = 3f * scale,
                        cap = StrokeCap.Round
                    )
                } else {
                    drawLine(
                        color = Color(0xFF1A1A2E),
                        start = Offset(cx + 14f * scale, cy + 4f * scale),
                        end = Offset(cx + 34f * scale, cy + 4f * scale),
                        strokeWidth = 2.5f * scale,
                        cap = StrokeCap.Round
                    )
                }

                // Cute Eyebrows
                drawLine(
                    color = Color(0xFF2C3E50),
                    start = Offset(cx - 32f * scale, cy - 12f * scale),
                    end = Offset(cx - 15f * scale, cy - 10f * scale),
                    strokeWidth = 2.2f * scale,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color(0xFF2C3E50),
                    start = Offset(cx + 15f * scale, cy - 10f * scale),
                    end = Offset(cx + 32f * scale, cy - 12f * scale),
                    strokeWidth = 2.2f * scale,
                    cap = StrokeCap.Round
                )

                // Tiny Cute Nose
                drawCircle(
                    color = Color(0x66FF7043),
                    radius = 1.8f * scale,
                    center = Offset(cx, cy + 22f * scale)
                )

                // 6. Reactive Mouth
                if (voiceState == MyraVoiceState.SPEAKING) {
                    val mouthH = (8f + talkOscillation * 14f + audioLevel * 10f) * scale
                    drawOval(
                        color = Color(0xFFC2185B),
                        topLeft = Offset(cx - 7f * scale, cy + 35f * scale),
                        size = Size(14f * scale, mouthH)
                    )
                } else if (voiceState == MyraVoiceState.LISTENING) {
                    // Small "o" curious mouth
                    drawCircle(
                        color = Color(0xFFC2185B),
                        radius = 3.5f * scale,
                        center = Offset(cx, cy + 39f * scale)
                    )
                } else {
                    // Gentle smile curve
                    val smilePath = Path().apply {
                        moveTo(cx - 7f * scale, cy + 37f * scale)
                        quadraticBezierTo(cx, cy + 42f * scale, cx + 7f * scale, cy + 37f * scale)
                    }
                    drawPath(
                        path = smilePath,
                        color = Color(0xFFC2185B),
                        style = Stroke(width = 2.2f * scale, cap = StrokeCap.Round)
                    )
                }

                // 7. Front Bangs / Cyber Hair Strands with Neon Cyan Gradient Streaks
                val bangsPath = Path().apply {
                    moveTo(cx - 52f * scale, cy - 35f * scale)
                    cubicTo(cx - 40f * scale, cy - 8f * scale, cx - 25f * scale, cy + 5f * scale, cx - 18f * scale, cy - 2f * scale)
                    lineTo(cx - 10f * scale, cy - 15f * scale)
                    cubicTo(cx, cy + 8f * scale, cx + 15f * scale, cy - 5f * scale, cx + 22f * scale, cy - 1f * scale)
                    lineTo(cx + 30f * scale, cy - 18f * scale)
                    cubicTo(cx + 42f * scale, cy + 2f * scale, cx + 50f * scale, cy - 10f * scale, cx + 52f * scale, cy - 35f * scale)
                    cubicTo(cx + 35f * scale, cy - 58f * scale, cx - 35f * scale, cy - 58f * scale, cx - 52f * scale, cy - 35f * scale)
                    close()
                }
                drawPath(
                    path = bangsPath,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF2B3A67), Color(0xFF1E284A))
                    )
                )

                // Neon Cyber Hair Strand Highlight
                drawLine(
                    color = NeonCyan.copy(alpha = 0.85f),
                    start = Offset(cx - 36f * scale, cy - 30f * scale),
                    end = Offset(cx - 24f * scale, cy + 2f * scale),
                    strokeWidth = 2.5f * scale,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = NeonMagenta.copy(alpha = 0.85f),
                    start = Offset(cx + 26f * scale, cy - 30f * scale),
                    end = Offset(cx + 38f * scale, cy - 2f * scale),
                    strokeWidth = 2.5f * scale,
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stateText,
            color = accentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "TAP AVATAR FOR VOICE INTERACTION",
            color = TextSecondary.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    }
}

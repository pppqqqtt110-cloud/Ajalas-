package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.voice.MyraVoiceState

/**
 * Recreates MYRA's signature Edge Glow effect (myra_edge_glow.py):
 * Ambient animated neon perimeter border around the Android screen that reacts to voice state.
 */
@Composable
fun EdgeGlowOverlay(
    voiceState: MyraVoiceState,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    if (!enabled) return

    val transition = rememberInfiniteTransition(label = "EdgeGlowTransition")
    val alphaAnim by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    val glowColor = when (voiceState) {
        MyraVoiceState.LISTENING -> NeonCyan
        MyraVoiceState.THINKING -> CyberPurple
        MyraVoiceState.SPEAKING -> NeonMagenta
        MyraVoiceState.IDLE -> NeonCyan
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeW = 4f
        val glowDepth = 24f

        // Top edge glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(glowColor.copy(alpha = alphaAnim * 0.7f), Color.Transparent),
                startY = 0f,
                endY = glowDepth
            ),
            topLeft = Offset.Zero,
            size = Size(size.width, glowDepth)
        )

        // Bottom edge glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, glowColor.copy(alpha = alphaAnim * 0.7f)),
                startY = size.height - glowDepth,
                endY = size.height
            ),
            topLeft = Offset(0f, size.height - glowDepth),
            size = Size(size.width, glowDepth)
        )

        // Left edge glow
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(glowColor.copy(alpha = alphaAnim * 0.5f), Color.Transparent),
                startX = 0f,
                endX = glowDepth
            ),
            topLeft = Offset.Zero,
            size = Size(glowDepth, size.height)
        )

        // Right edge glow
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, glowColor.copy(alpha = alphaAnim * 0.5f)),
                startX = size.width - glowDepth,
                endX = size.width
            ),
            topLeft = Offset(size.width - glowDepth, 0f),
            size = Size(glowDepth, size.height)
        )
    }
}

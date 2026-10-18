package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.MyraFaceMode
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.QuantumAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MyraHudBar(
    currentFace: MyraFaceMode,
    isA11yActive: Boolean,
    onToggleFace: () -> Unit,
    onA11yClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F1528), CyberSurface)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(NeonCyan.copy(alpha = 0.4f), CyberPurple.copy(alpha = 0.2f), NeonMagenta.copy(alpha = 0.4f))
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Insignia
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("hud_brand")
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141C34))
                        .border(1.5.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (currentFace == MyraFaceMode.FACE_ONE) Icons.Default.GraphicEq else Icons.Default.Face,
                        contentDescription = "MYRA Emblem",
                        tint = if (currentFace == MyraFaceMode.FACE_ONE) NeonCyan else NeonMagenta,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "MYRA ULTRA",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "AUTONOMOUS CORE v4.1",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }

            // Quick Status Controls
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Face Switcher Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF19223D))
                        .border(
                            1.dp,
                            if (currentFace == MyraFaceMode.FACE_ONE) NeonCyan else NeonMagenta,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = onToggleFace)
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("toggle_face_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (currentFace == MyraFaceMode.FACE_ONE) Icons.Default.Memory else Icons.Default.Face,
                            contentDescription = "Switch Face",
                            tint = if (currentFace == MyraFaceMode.FACE_ONE) NeonCyan else NeonMagenta,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentFace == MyraFaceMode.FACE_ONE) "FACE 1" else "FACE 2",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Accessibility Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isA11yActive) Color(0xFF003820) else Color(0xFF382900)
                        )
                        .border(
                            1.dp,
                            if (isA11yActive) CyberGreen else QuantumAmber,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = onA11yClick)
                        .padding(horizontal = 7.dp, vertical = 5.dp)
                        .testTag("a11y_status_chip"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isA11yActive) CyberGreen else QuantumAmber)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isA11yActive) "A11Y ON" else "A11Y OFF",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isA11yActive) CyberGreen else QuantumAmber
                        )
                    }
                }
            }
        }
    }
}

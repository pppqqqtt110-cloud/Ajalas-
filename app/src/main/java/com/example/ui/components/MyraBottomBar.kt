package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextSecondary

enum class MyraNavDestination(
    val title: String,
    val icon: ImageVector,
    val tag: String
) {
    CONSOLE("HUD", Icons.Default.Radar, "nav_console"),
    CHAT("AI CHAT", Icons.Default.Chat, "nav_chat"),
    CONTROL("CONTROL", Icons.Default.PhoneAndroid, "nav_control"),
    STUDIO("STUDIO", Icons.Default.Code, "nav_studio"),
    SETTINGS("SYSTEM", Icons.Default.Settings, "nav_settings")
}

@Composable
fun MyraBottomBar(
    currentDestination: MyraNavDestination,
    onSelectDestination: (MyraNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = Color(0xFF1E284A))
            .testTag("myra_bottom_bar"),
        containerColor = CyberSurface,
        tonalElevation = 8.dp
    ) {
        MyraNavDestination.entries.forEach { dest ->
            val selected = currentDestination == dest
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectDestination(dest) },
                icon = {
                    Icon(
                        imageVector = dest.icon,
                        contentDescription = dest.title,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = dest.title,
                        fontSize = 9.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonCyan,
                    selectedTextColor = NeonCyan,
                    unselectedIconColor = TextSecondary.copy(alpha = 0.6f),
                    unselectedTextColor = TextSecondary.copy(alpha = 0.6f),
                    indicatorColor = Color(0xFF14243D)
                ),
                modifier = Modifier.testTag(dest.tag)
            )
        }
    }
}

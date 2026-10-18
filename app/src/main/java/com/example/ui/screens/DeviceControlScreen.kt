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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pageview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.control.ActionResult
import com.example.control.MyraDeviceController
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
fun DeviceControlScreen(
    deviceController: MyraDeviceController,
    isA11yActive: Boolean,
    onOpenA11ySettings: () -> Unit,
    onActionResult: (ActionResult) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var targetClickText by remember { mutableStateOf("") }
    var screenInspectResult by remember { mutableStateOf<ActionResult?>(null) }
    val installedApps = remember { deviceController.getInstalledAppList() }

    val filteredApps = remember(searchQuery, installedApps) {
        if (searchQuery.isBlank()) installedApps.take(20)
        else installedApps.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(14.dp)
            .testTag("device_control_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Accessibility Service Status & Activation Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isA11yActive) Color(0xFF002917) else Color(0xFF2C1E0A)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isA11yActive) CyberGreen else QuantumAmber,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Accessibility,
                                contentDescription = "A11y",
                                tint = if (isA11yActive) CyberGreen else QuantumAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACCESSIBILITY SERVICE",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = if (isA11yActive) CyberGreen else QuantumAmber
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isA11yActive) CyberGreen else QuantumAmber)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isA11yActive) "ENABLED" else "DISABLED",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isA11yActive)
                            "MYRA ULTRA is authorized for verified Android control: navigation, node reading, gestures, and app interaction."
                        else
                            "Device automation requires enabling 'MYRA ULTRA Device Assistant' in Android Accessibility settings.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    if (!isA11yActive) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onOpenA11ySettings,
                            colors = ButtonDefaults.buttonColors(containerColor = QuantumAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("open_a11y_settings_btn")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ENABLE IN ACCESSIBILITY SETTINGS",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Android System Navigation Action Pad
        item {
            Column {
                Text(
                    text = "SYSTEM NAVIGATION PAD",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionPadButton(
                        icon = Icons.Default.Home,
                        label = "HOME",
                        color = NeonCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.goHome()) }
                    )
                    ActionPadButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        label = "BACK",
                        color = NeonCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.goBack()) }
                    )
                    ActionPadButton(
                        icon = Icons.Default.ViewCarousel,
                        label = "RECENTS",
                        color = NeonCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.openRecents()) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionPadButton(
                        icon = Icons.Default.Notifications,
                        label = "NOTIFS",
                        color = CyberPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.openNotifications()) }
                    )
                    ActionPadButton(
                        icon = Icons.Default.Settings,
                        label = "QUICK SET",
                        color = CyberPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.openQuickSettings()) }
                    )
                    ActionPadButton(
                        icon = Icons.Default.Lock,
                        label = "LOCK",
                        color = NeonMagenta,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.lockScreen()) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionPadButton(
                        icon = Icons.Default.ArrowDownward,
                        label = "SCROLL DOWN",
                        color = Color(0xFF2979FF),
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.scroll(true)) }
                    )
                    ActionPadButton(
                        icon = Icons.Default.ArrowUpward,
                        label = "SCROLL UP",
                        color = Color(0xFF2979FF),
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.scroll(false)) }
                    )
                }
            }
        }

        // 3. Hardware Toggles
        item {
            Column {
                Text(
                    text = "HARDWARE TELEMETRY & CONTROLS",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionPadButton(
                        icon = Icons.Default.FlashlightOn,
                        label = "TORCH",
                        color = QuantumAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.toggleFlashlight()) }
                    )
                    ActionPadButton(
                        icon = Icons.Default.VolumeUp,
                        label = "VOL +",
                        color = CyberGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.changeVolume(true)) }
                    )
                    ActionPadButton(
                        icon = Icons.Default.VolumeDown,
                        label = "VOL -",
                        color = CyberGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.changeVolume(false)) }
                    )
                    ActionPadButton(
                        icon = Icons.Default.BatteryChargingFull,
                        label = "BATTERY",
                        color = NeonCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onActionResult(deviceController.getBatteryStatus()) }
                    )
                }
            }
        }

        // 4. Screen Reading & Accessibility Node Inspector
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF253358), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SCREEN OCR & NODE INSPECTION",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Reads visible text and interactive UI elements from current active window for AI comprehension.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val result = deviceController.readScreen()
                                screenInspectResult = result
                                onActionResult(result)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF193252)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Pageview, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("INSPECT SCREEN", fontSize = 11.sp, color = NeonCyan, fontFamily = FontFamily.Monospace)
                        }
                    }

                    // Click node by text field
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = targetClickText,
                            onValueChange = { targetClickText = it },
                            placeholder = { Text("Element text to click...", color = TextSecondary.copy(alpha = 0.5f), fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF131A2F),
                                unfocusedContainerColor = Color(0xFF131A2F),
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF253358),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (targetClickText.isNotBlank()) {
                                    val res = deviceController.clickElement(targetClickText)
                                    onActionResult(res)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TAP", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Display inspected nodes if any
                    if (screenInspectResult?.details?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0C101D))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = screenInspectResult!!.details,
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Installed Applications Launcher
        item {
            Column {
                Text(
                    text = "INSTALLED APPS LAUNCHER",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search installed apps...", color = TextSecondary.copy(alpha = 0.5f), fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("app_search_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF253358),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        items(filteredApps, key = { it.packageName }) { app ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurface)
                    .border(1.dp, Color(0xFF1E284A), RoundedCornerShape(8.dp))
                    .clickable {
                        val res = deviceController.launchApp(app.name)
                        onActionResult(res)
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.name,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = app.packageName,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Launch",
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionPadButton(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurfaceElevated)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TextPrimary
            )
        }
    }
}

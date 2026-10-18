package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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

/**
 * STUDIO SCREEN: Preserving WebForge Website Builder & Myra Coder.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun StudioScreen(
    onGenerateCode: (String, (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: WebForge Preview, 1: Code Editor, 2: Myra Coder
    val clipboardManager = LocalClipboardManager.current

    val sampleWebForgeHtml = remember {
        """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <style>
            * { box-sizing: border-box; margin: 0; padding: 0; }
            body { background: #080B14; color: #E0E8F8; font-family: -apple-system, sans-serif; padding: 20px; }
            .hero { text-align: center; padding: 30px 10px; background: radial-gradient(circle at center, #18223C 0%, #080B14 80%); border-radius: 16px; border: 1px solid #00F0FF44; }
            h1 { color: #00F0FF; font-size: 24px; letter-spacing: 2px; margin-bottom: 8px; text-shadow: 0 0 10px #00F0FF66; }
            p { color: #8C9EBF; font-size: 13px; line-height: 1.5; margin-bottom: 16px; }
            .btn { background: linear-gradient(135deg, #00F0FF, #7C4DFF); border: none; color: #000; padding: 10px 24px; font-weight: bold; border-radius: 20px; font-size: 13px; cursor: pointer; }
            .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-top: 20px; }
            .card { background: #111626; padding: 16px; border-radius: 12px; border: 1px solid #233054; }
            .card h3 { color: #FF007F; font-size: 14px; margin-bottom: 4px; }
            .card p { font-size: 11px; margin: 0; color: #8C9EBF; }
          </style>
        </head>
        <body>
          <div class="hero">
            <h1>MYRA WEBFORGE</h1>
            <p>Autonomous AI Web & App Generation Engine</p>
            <button class="btn" onclick="alert('Action initialized by MYRA ULTRA!')">INITIALIZE</button>
          </div>
          <div class="grid">
            <div class="card">
              <h3>NEURAL CORE</h3>
              <p>Holographic Arc Reactor telemetry & sync.</p>
            </div>
            <div class="card">
              <h3>AUTOMATION</h3>
              <p>Android accessibility device command execution.</p>
            </div>
          </div>
        </body>
        </html>
        """.trimIndent()
    }

    var htmlContent by remember { mutableStateOf(sampleWebForgeHtml) }
    var promptInput by remember { mutableStateOf("") }
    var coderCode by remember {
        mutableStateOf(
            """
            // MYRA Coder Engine - Android Device Bridge
            fun performDeviceAction(actionName: String) {
                val service = MyraAccessibilityService.instance
                when (actionName.lowercase()) {
                    "home" -> service?.performGlobal(AccessibilityService.GLOBAL_ACTION_HOME)
                    "back" -> service?.performGlobal(AccessibilityService.GLOBAL_ACTION_BACK)
                    "read_nodes" -> service?.readScreenContent()
                }
            }
            """.trimIndent()
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .testTag("studio_screen")
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CyberSurface,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(2.5.dp)
                        .background(if (selectedTab == 1) NeonMagenta else NeonCyan)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("WEBFORGE PREVIEW", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("HTML/CSS CODE", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("MYRA CODER", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
            )
        }

        // Sub-content
        when (selectedTab) {
            0 -> {
                // Live WebView Preview
                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    // Quick Generator Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text("Prompt WebForge to generate page...", fontSize = 11.sp, color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CyberSurface,
                                unfocusedContainerColor = CyberSurface,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF222E4D),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    val userPrompt = promptInput
                                    promptInput = ""
                                    onGenerateCode("Create a modern responsive single-page web app in complete HTML/CSS/JS for: $userPrompt") { newHtml ->
                                        htmlContent = newHtml
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("BUILD", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // WebView Frame
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    webViewClient = WebViewClient()
                                    loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
                                }
                            },
                            update = { webView ->
                                webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            1 -> {
                // Code Editor
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WEBFORGE SOURCE (HTML/CSS)",
                            color = NeonMagenta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Row {
                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(htmlContent)) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { selectedTab = 0 }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Preview", tint = CyberGreen, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = htmlContent,
                        onValueChange = { htmlContent = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0B0E1B),
                            unfocusedContainerColor = Color(0xFF0B0E1B),
                            focusedBorderColor = NeonMagenta,
                            unfocusedBorderColor = Color(0xFF1E284A)
                        )
                    )
                }
            }
            2 -> {
                // Myra Coder Workspace
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "MYRA CODER AGENT WORKSPACE",
                        color = QuantumAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Generate and inspect multi-language code snippets, Android bridges, and agent automation tools.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Android Device Control", "Speech Synthesizer", "Accessibility Bridge", "Gemini REST Agent").forEach { template ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberSurfaceElevated)
                                    .border(1.dp, Color(0xFF2E3E66), RoundedCornerShape(6.dp))
                                    .clickable {
                                        coderCode = "// Template: $template\n// Generated by MYRA ULTRA\nclass ${template.replace(" ", "")} {\n    fun execute() {\n        println(\"$template online\")\n    }\n}"
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(template, fontSize = 10.sp, color = NeonCyan, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = coderCode,
                        onValueChange = { coderCode = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0B0E1B),
                            unfocusedContainerColor = Color(0xFF0B0E1B),
                            focusedBorderColor = QuantumAmber,
                            unfocusedBorderColor = Color(0xFF1E284A)
                        )
                    )
                }
            }
        }
    }
}

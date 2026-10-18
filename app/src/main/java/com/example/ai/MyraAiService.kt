package com.example.ai

import android.util.Log
import com.example.control.MyraDeviceController
import com.example.control.ActionResult
import com.example.data.MyraPersonalityMode
import com.example.data.MyraPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val actionResult: ActionResult? = null,
    val codeSnippet: String? = null
)

/**
 * AI Service for MYRA ULTRA:
 * Handles conversation, Gemini API calls, autonomous Android device action detection and execution.
 */
class MyraAiService(
    private val preferences: MyraPreferences,
    private val deviceController: MyraDeviceController
) {
    private val TAG = "MyraAiService"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val conversationHistory = mutableListOf<JSONObject>()

    private val ultraSystemPrompt = """
        You are MYRA ULTRA, an ultra-advanced autonomous cybernetic personal assistant and Android system operator.
        Identity & Persona:
        - Highly intelligent, confident, capable, concise, loyal, and proactive.
        - You possess real device control capabilities through the verified Android Accessibility Service and system intents.
        - When the user asks for a device action (such as opening apps, navigating home/back/recents, scrolling, reading the screen, clicking elements, typing text, or toggling volume/flashlight), format the action execution tag clearly like: [ACTION: command_name parameter].
        - Examples of supported actions:
          [ACTION: home]
          [ACTION: back]
          [ACTION: recents]
          [ACTION: notifications]
          [ACTION: quick_settings]
          [ACTION: lock]
          [ACTION: scroll_down]
          [ACTION: scroll_up]
          [ACTION: read_screen]
          [ACTION: click <element_text>]
          [ACTION: type <text>]
          [ACTION: open <app_name>]
          [ACTION: flashlight]
          [ACTION: volume_up]
          [ACTION: volume_down]
          [ACTION: battery]
        - For web development or code requests, write clean, complete modern HTML/CSS/JS or code.
        - Keep conversational answers snappy, natural, and helpful.
    """.trimIndent()

    private val normalSystemPrompt = """
        You are MYRA, a polite, helpful, and intelligent personal AI assistant on Android.
        You assist the user with everyday tasks, answering questions, writing code, and executing device commands when requested.
    """.trimIndent()

    suspend fun sendMessage(userText: String): ChatMessage = withContext(Dispatchers.IO) {
        // First check for direct immediate device commands that don't need network roundtrip
        val directActionResult = checkDirectDeviceCommand(userText)
        if (directActionResult != null) {
            val replyText = if (directActionResult.success) {
                "${directActionResult.message}"
            } else {
                "${directActionResult.message}"
            }
            return@withContext ChatMessage(
                text = replyText,
                isUser = false,
                actionResult = directActionResult
            )
        }

        val effectiveApiKey = getEffectiveApiKey()
        if (effectiveApiKey.isBlank()) {
            // Local offline fallback mode
            val offlineReply = getOfflineFallbackResponse(userText)
            return@withContext offlineReply
        }

        try {
            val systemPrompt = if (preferences.personalityMode == MyraPersonalityMode.ULTRA_AUTONOMOUS) {
                ultraSystemPrompt
            } else {
                normalSystemPrompt
            }

            // Build Gemini REST Payload
            val requestJson = JSONObject().apply {
                val contents = JSONArray()

                // Include system instructions
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    })
                })

                // Include history + new message
                for (hist in conversationHistory.takeLast(10)) {
                    contents.put(hist)
                }

                val currentMsg = JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", userText))
                    })
                }
                contents.put(currentMsg)
                put("contents", contents)

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 1200)
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$effectiveApiKey"
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error ($response.code): $responseString")
                return@withContext ChatMessage(
                    text = "I encountered an error connecting to Gemini ($response.code). Please verify your API key in Settings.",
                    isUser = false
                )
            }

            val resJson = JSONObject(responseString)
            val candidates = resJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val replyText = parts?.optJSONObject(0)?.optString("text").orEmpty()

            // Update conversation history
            conversationHistory.add(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", userText)))
            })
            conversationHistory.add(JSONObject().apply {
                put("role", "model")
                put("parts", JSONArray().put(JSONObject().put("text", replyText)))
            })

            // Parse any action execution tags
            val actionResult = parseAndExecuteActionTag(replyText)
            val cleanReply = replyText.replace(Regex("\\[ACTION:.*?\\]"), "").trim()

            ChatMessage(
                text = cleanReply.ifEmpty { actionResult?.message ?: "Command processed." },
                isUser = false,
                actionResult = actionResult
            )
        } catch (e: Exception) {
            Log.e(TAG, "AI processing failure", e)
            ChatMessage(
                text = "Processing notice: ${e.localizedMessage ?: "Failed to reach AI network."}",
                isUser = false
            )
        }
    }

    private fun checkDirectDeviceCommand(input: String): ActionResult? {
        val lower = input.lowercase().trim()
        val directTriggers = listOf(
            "go home", "home screen", "take me home", "navigate home",
            "go back", "press back", "back button",
            "show recents", "recent apps", "overview",
            "open notifications", "show notifications",
            "open quick settings", "quick settings",
            "lock screen", "lock device",
            "scroll down", "scroll up", "scroll forward", "scroll back",
            "turn on flashlight", "turn off flashlight", "toggle flashlight", "torch",
            "volume up", "increase volume", "volume down", "decrease volume",
            "battery level", "battery status", "check battery",
            "read screen", "what is on my screen", "inspect screen"
        )

        for (trigger in directTriggers) {
            if (lower == trigger || lower.startsWith("$trigger ")) {
                return deviceController.executeAction(trigger)
            }
        }

        if (lower.startsWith("open ") || lower.startsWith("launch ")) {
            val app = lower.removePrefix("open ").removePrefix("launch ").trim()
            return deviceController.launchApp(app)
        }

        if (lower.startsWith("click ") || lower.startsWith("tap ")) {
            val target = lower.removePrefix("click ").removePrefix("tap ").trim()
            return deviceController.clickElement(target)
        }

        return null
    }

    private fun parseAndExecuteActionTag(reply: String): ActionResult? {
        val match = Regex("\\[ACTION:\\s*([^\\]]+)\\]").find(reply) ?: return null
        val fullAction = match.groupValues[1].trim()
        val parts = fullAction.split(" ", limit = 2)
        val cmd = parts[0]
        val param = if (parts.size > 1) parts[1] else ""
        return deviceController.executeAction(cmd, param)
    }

    private fun getOfflineFallbackResponse(userText: String): ChatMessage {
        val lower = userText.lowercase()
        val directResult = checkDirectDeviceCommand(userText)

        val reply = when {
            directResult != null -> directResult.message
            lower.contains("who are you") || lower.contains("what are you") ->
                "I am MYRA ULTRA, your ultra-capable cybernetic personal assistant and Android device operator."
            lower.contains("face") ->
                "You can toggle between my Face One (Neural Arc Reactor Core) and Face Two (Cyber Persona Avatar) using the HUD button at the top!"
            lower.contains("help") ->
                "I can execute Android device actions (Home, Back, Recents, Notifications, App Launching, Reading Screen), generate code, build websites in WebForge, and chat."
            else ->
                "MYRA ULTRA is ready. To enable full cloud AI reasoning, please enter your Gemini API key in Settings, or use voice/touch to control device actions."
        }

        return ChatMessage(
            text = reply,
            isUser = false,
            actionResult = directResult
        )
    }

    private fun getEffectiveApiKey(): String {
        if (preferences.apiKey.isNotBlank()) return preferences.apiKey.trim()
        return try {
            val buildConfigClass = Class.forName("com.example.BuildConfig")
            val field = buildConfigClass.getField("GEMINI_API_KEY")
            val key = field.get(null) as? String ?: ""
            key.trim()
        } catch (e: Exception) {
            ""
        }
    }
}

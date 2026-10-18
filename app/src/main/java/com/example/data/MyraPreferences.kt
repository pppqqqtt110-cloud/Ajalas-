package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class MyraFaceMode {
    FACE_ONE, // Cybernetic Neural Arc Reactor Core
    FACE_TWO  // Cyberpunk Persona Girl Avatar
}

enum class MyraPersonalityMode {
    ULTRA_AUTONOMOUS,
    NORMAL_ASSISTANT
}

class MyraPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("myra_ultra_prefs", Context.MODE_PRIVATE)

    var apiKey: String
        get() = prefs.getString("custom_api_key", "") ?: ""
        set(value) = prefs.edit().putString("custom_api_key", value).apply()

    var faceMode: MyraFaceMode
        get() {
            val name = prefs.getString("face_mode", MyraFaceMode.FACE_ONE.name)
            return try {
                MyraFaceMode.valueOf(name ?: MyraFaceMode.FACE_ONE.name)
            } catch (e: Exception) {
                MyraFaceMode.FACE_ONE
            }
        }
        set(value) = prefs.edit().putString("face_mode", value.name).apply()

    var personalityMode: MyraPersonalityMode
        get() {
            val name = prefs.getString("personality_mode", MyraPersonalityMode.ULTRA_AUTONOMOUS.name)
            return try {
                MyraPersonalityMode.valueOf(name ?: MyraPersonalityMode.ULTRA_AUTONOMOUS.name)
            } catch (e: Exception) {
                MyraPersonalityMode.ULTRA_AUTONOMOUS
            }
        }
        set(value) = prefs.edit().putString("personality_mode", value.name).apply()

    var edgeGlowEnabled: Boolean
        get() = prefs.getBoolean("edge_glow_enabled", true)
        set(value) = prefs.edit().putBoolean("edge_glow_enabled", value).apply()

    var guardianSecurityEnabled: Boolean
        get() = prefs.getBoolean("guardian_enabled", false)
        set(value) = prefs.edit().putBoolean("guardian_enabled", value).apply()

    var voicePitch: Float
        get() = prefs.getFloat("voice_pitch", 1.05f)
        set(value) = prefs.edit().putFloat("voice_pitch", value).apply()

    var voiceSpeed: Float
        get() = prefs.getFloat("voice_speed", 1.0f)
        set(value) = prefs.edit().putFloat("voice_speed", value).apply()

    var speechSynthesisEnabled: Boolean
        get() = prefs.getBoolean("tts_enabled", true)
        set(value) = prefs.edit().putBoolean("tts_enabled", value).apply()
}

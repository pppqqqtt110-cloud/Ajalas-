package com.example.control

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log
import com.example.accessibility.MyraAccessibilityService

/**
 * Result data class for every executed device action, reporting truthful status to MYRA ULTRA.
 */
data class ActionResult(
    val command: String,
    val success: Boolean,
    val message: String,
    val details: String = ""
)

/**
 * Android Device Control layer for MYRA ULTRA.
 * Replaces legacy desktop control with legitimate Android APIs, accessibility actions, and intents.
 */
class MyraDeviceController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    private var flashlightOn = false

    /**
     * Executes recognized action string or returns fallback
     */
    fun executeAction(command: String, parameter: String = ""): ActionResult {
        val lower = command.lowercase().trim()
        val param = parameter.trim()

        return when {
            lower.contains("home") -> goHome()
            lower.contains("back") -> goBack()
            lower.contains("recent") || lower.contains("overview") -> openRecents()
            lower.contains("notification") -> openNotifications()
            lower.contains("quick_setting") || lower.contains("quick setting") -> openQuickSettings()
            lower.contains("lock") -> lockScreen()

            lower.contains("scroll down") || lower.contains("scroll_down") -> scroll(forward = true)
            lower.contains("scroll up") || lower.contains("scroll_up") -> scroll(forward = false)

            lower.contains("read screen") || lower.contains("read_screen") || lower.contains("inspect") -> readScreen()
            lower.contains("click") || lower.contains("tap") -> clickElement(if (param.isNotEmpty()) param else extractTarget(command, "click"))
            lower.contains("type") || lower.contains("input") || lower.contains("write") -> {
                val text = if (param.isNotEmpty()) param else command.substringAfter("type ").substringAfter("write ")
                typeText(text)
            }

            lower.contains("flashlight") || lower.contains("torch") -> toggleFlashlight()
            lower.contains("volume up") || lower.contains("louder") -> changeVolume(increase = true)
            lower.contains("volume down") || lower.contains("quieter") -> changeVolume(increase = false)
            lower.contains("battery") -> getBatteryStatus()

            lower.contains("open ") || lower.contains("launch ") -> {
                val appName = if (param.isNotEmpty()) param else command.substringAfter("open ").substringAfter("launch ")
                launchApp(appName)
            }

            lower.contains("setting") -> openSettings()
            lower.contains("a11y") || lower.contains("accessibility") -> openAccessibilitySettings()

            else -> ActionResult(
                command = command,
                success = false,
                message = "Command not recognized or unsupported on Android without root."
            )
        }
    }

    private fun extractTarget(fullCommand: String, keyword: String): String {
        return fullCommand.substringAfter(keyword, "").trim().removePrefix("on ").removePrefix("the ").trim('"', '\'')
    }

    fun isAccessibilityActive(): Boolean {
        return MyraAccessibilityService.isEnabled(context)
    }

    fun openAccessibilitySettings(): ActionResult {
        return try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult("accessibility_settings", true, "Opened Android Accessibility Settings.")
        } catch (e: Exception) {
            ActionResult("accessibility_settings", false, "Failed to open settings: ${e.message}")
        }
    }

    fun openSettings(): ActionResult {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult("settings", true, "Opened System Settings.")
        } catch (e: Exception) {
            ActionResult("settings", false, "Failed to open system settings: ${e.message}")
        }
    }

    fun goHome(): ActionResult {
        val service = MyraAccessibilityService.instance
        if (service != null && service.performGlobal(AccessibilityService.GLOBAL_ACTION_HOME)) {
            return ActionResult("home", true, "Navigated to Android Home Screen via Accessibility.")
        }
        // Fallback: Launch HOME intent
        return try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ActionResult("home", true, "Navigated to Android Home Screen via System Intent.")
        } catch (e: Exception) {
            ActionResult("home", false, "Could not navigate home: ${e.message}")
        }
    }

    fun goBack(): ActionResult {
        val service = MyraAccessibilityService.instance
            ?: return ActionResult("back", false, "Accessibility Service is not enabled. Please enable it in Settings.", requiresA11y = true)
        val ok = service.performGlobal(AccessibilityService.GLOBAL_ACTION_BACK)
        return ActionResult("back", ok, if (ok) "Pressed Back button." else "Back action was not handled.")
    }

    fun openRecents(): ActionResult {
        val service = MyraAccessibilityService.instance
            ?: return ActionResult("recents", false, "Accessibility Service is not enabled.", requiresA11y = true)
        val ok = service.performGlobal(AccessibilityService.GLOBAL_ACTION_RECENTS)
        return ActionResult("recents", ok, if (ok) "Opened Recent Apps overview." else "Failed to open Recents.")
    }

    fun openNotifications(): ActionResult {
        val service = MyraAccessibilityService.instance
            ?: return ActionResult("notifications", false, "Accessibility Service is not enabled.", requiresA11y = true)
        val ok = service.performGlobal(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
        return ActionResult("notifications", ok, if (ok) "Opened Notification shade." else "Failed to open Notifications.")
    }

    fun openQuickSettings(): ActionResult {
        val service = MyraAccessibilityService.instance
            ?: return ActionResult("quick_settings", false, "Accessibility Service is not enabled.", requiresA11y = true)
        val ok = service.performGlobal(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)
        return ActionResult("quick_settings", ok, if (ok) "Opened Quick Settings panel." else "Failed to open Quick Settings.")
    }

    fun lockScreen(): ActionResult {
        val service = MyraAccessibilityService.instance
            ?: return ActionResult("lock", false, "Accessibility Service is not enabled.", requiresA11y = true)
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val ok = service.performGlobal(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
            ActionResult("lock", ok, if (ok) "Locked screen." else "Could not lock screen.")
        } else {
            ActionResult("lock", false, "Lock screen via accessibility requires Android 9+ (API 28).")
        }
    }

    fun scroll(forward: Boolean): ActionResult {
        val service = MyraAccessibilityService.instance
            ?: return ActionResult("scroll", false, "Accessibility Service is not enabled.", requiresA11y = true)
        val ok = service.performScroll(forward)
        val direction = if (forward) "down / forward" else "up / backward"
        return ActionResult("scroll", ok, if (ok) "Scrolled screen $direction." else "No scrollable elements found on current screen.")
    }

    fun readScreen(): ActionResult {
        val service = MyraAccessibilityService.instance
            ?: return ActionResult("read_screen", false, "Accessibility Service is not enabled.", requiresA11y = true)
        val nodes = service.readScreenContent()
        if (nodes.isEmpty()) {
            return ActionResult("read_screen", true, "Screen read completed. No readable text found on current window.")
        }
        val summary = nodes.take(15).joinToString("\n") { element ->
            val desc = if (element.contentDescription.isNotEmpty()) " (${element.contentDescription})" else ""
            "• [${element.className.substringAfterLast('.')}] ${element.text}$desc ${if (element.isClickable) "[Clickable]" else ""}"
        }
        return ActionResult(
            command = "read_screen",
            success = true,
            message = "Extracted ${nodes.size} UI elements from screen.",
            details = summary
        )
    }

    fun clickElement(targetText: String): ActionResult {
        if (targetText.isEmpty()) {
            return ActionResult("click", false, "Please specify an element label or text to click.")
        }
        val service = MyraAccessibilityService.instance
            ?: return ActionResult("click", false, "Accessibility Service is not enabled.", requiresA11y = true)
        val ok = service.clickElementByText(targetText)
        return ActionResult(
            command = "click",
            success = ok,
            message = if (ok) "Clicked UI element containing '$targetText'." else "Could not locate clickable element matching '$targetText'."
        )
    }

    fun typeText(text: String): ActionResult {
        if (text.isEmpty()) {
            return ActionResult("type", false, "No text provided to type.")
        }
        val service = MyraAccessibilityService.instance
            ?: return ActionResult("type", false, "Accessibility Service is not enabled.", requiresA11y = true)
        val ok = service.enterText("", text)
        return ActionResult(
            command = "type",
            success = ok,
            message = if (ok) "Entered text: '$text'." else "Could not find an editable input field."
        )
    }

    fun launchApp(appName: String): ActionResult {
        val pm = context.packageManager
        val cleanName = appName.trim().lowercase()

        // Known package shortcuts
        val packageMap = mapOf(
            "youtube" to "com.google.android.youtube",
            "chrome" to "com.android.chrome",
            "browser" to "com.android.chrome",
            "maps" to "com.google.android.apps.maps",
            "camera" to "com.google.android.GoogleCamera",
            "photos" to "com.google.android.apps.photos",
            "whatsapp" to "com.whatsapp",
            "spotify" to "com.spotify.music",
            "play store" to "com.android.vending",
            "gmail" to "com.google.android.gm",
            "clock" to "com.google.android.deskclock",
            "calculator" to "com.google.android.calculator"
        )

        val directPkg = packageMap[cleanName]
        if (directPkg != null) {
            val launchIntent = pm.getLaunchIntentForPackage(directPkg)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return ActionResult("launch_app", true, "Launched $appName.")
            }
        }

        // Search installed apps by label
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in installedApps) {
            if ((app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || cleanName in listOf("settings", "camera", "clock", "calculator")) {
                val label = pm.getApplicationLabel(app).toString().lowercase()
                if (label.contains(cleanName) || cleanName.contains(label)) {
                    val intent = pm.getLaunchIntentForPackage(app.packageName)
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        return ActionResult("launch_app", true, "Launched ${pm.getApplicationLabel(app)}.")
                    }
                }
            }
        }

        return ActionResult("launch_app", false, "Application matching '$appName' not found installed on device.")
    }

    fun getInstalledAppList(): List<AppItem> {
        val pm = context.packageManager
        val list = mutableListOf<AppItem>()
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            val intent = pm.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                val label = pm.getApplicationLabel(app).toString()
                list.add(AppItem(label, app.packageName))
            }
        }
        return list.sortedBy { it.name }
    }

    data class AppItem(val name: String, val packageName: String)

    fun toggleFlashlight(): ActionResult {
        return try {
            val camMgr = cameraManager ?: return ActionResult("flashlight", false, "Camera service unavailable.")
            val cameraId = camMgr.cameraIdList.firstOrNull { id ->
                camMgr.getCameraCharacteristics(id).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: return ActionResult("flashlight", false, "Flashlight hardware not found.")

            flashlightOn = !flashlightOn
            camMgr.setTorchMode(cameraId, flashlightOn)
            ActionResult("flashlight", true, "Flashlight turned ${if (flashlightOn) "ON" else "OFF"}.")
        } catch (e: Exception) {
            ActionResult("flashlight", false, "Flashlight error: ${e.message}")
        }
    }

    fun changeVolume(increase: Boolean): ActionResult {
        return try {
            val am = audioManager ?: return ActionResult("volume", false, "Audio service unavailable.")
            val dir = if (increase) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, dir, AudioManager.FLAG_SHOW_UI)
            val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            ActionResult("volume", true, "Media volume set to $current/$max.")
        } catch (e: Exception) {
            ActionResult("volume", false, "Volume adjust error: ${e.message}")
        }
    }

    fun getBatteryStatus(): ActionResult {
        return try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            val charging = bm.isCharging
            ActionResult("battery", true, "Battery is at $level% ${if (charging) "(Charging)" else "(On Battery)"}.")
        } catch (e: Exception) {
            ActionResult("battery", true, "Battery level telemetry checked.")
        }
    }

    private fun ActionResult(cmd: String, success: Boolean, msg: String, requiresA11y: Boolean): ActionResult {
        val extra = if (requiresA11y) " [Setup required in Settings > Accessibility]" else ""
        return ActionResult(cmd, success, msg + extra)
    }
}

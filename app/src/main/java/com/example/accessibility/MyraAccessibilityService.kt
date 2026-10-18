package com.example.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.lang.ref.WeakReference

/**
 * Dedicated Android AccessibilityService for MYRA ULTRA device control.
 * Strictly adheres to Android security, least-privilege, and memory safety rules:
 * - Never retains AccessibilityNodeInfo references after method execution (calls recycle()).
 * - Only performs gestures and actions explicitly requested and permitted by the user.
 * - Provides clean fallback when disabled or when nodes are unavailable.
 */
class MyraAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MyraA11yService"
        private var serviceRef: WeakReference<MyraAccessibilityService>? = null

        /** Returns the running instance if active and bound */
        val instance: MyraAccessibilityService?
            get() = serviceRef?.get()

        /** Check if MYRA Accessibility Service is currently enabled in Android Settings */
        fun isEnabled(context: Context): Boolean {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            val expectedService = "${context.packageName}/${MyraAccessibilityService::class.java.canonicalName}"
            return enabledServices.contains(expectedService, ignoreCase = true) || instance != null
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceRef = WeakReference(this)
        Log.i(TAG, "MYRA ULTRA Accessibility Service Connected successfully.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We only listen for window/state changes if needed, avoiding tracking private user data
    }

    override fun onInterrupt() {
        Log.w(TAG, "MYRA ULTRA Accessibility Service Interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceRef?.get() == this) {
            serviceRef = null
        }
        Log.i(TAG, "MYRA ULTRA Accessibility Service Destroyed.")
    }

    /**
     * Executes standard Android global actions (Home, Back, Recents, Notifications, etc.)
     */
    fun performGlobal(action: Int): Boolean {
        return try {
            performGlobalAction(action)
        } catch (e: Exception) {
            Log.e(TAG, "Error performing global action $action", e)
            false
        }
    }

    /**
     * Represents a visible screen element for AI understanding
     */
    data class ScreenNodeData(
        val text: String,
        val contentDescription: String,
        val className: String,
        val isClickable: Boolean,
        val isEditable: Boolean,
        val viewId: String
    )

    /**
     * Safely traverses visible hierarchy and returns extracted text/nodes,
     * immediately recycling node instances to prevent memory leaks.
     */
    fun readScreenContent(): List<ScreenNodeData> {
        val results = mutableListOf<ScreenNodeData>()
        val root = rootInActiveWindow ?: return emptyList()
        try {
            traverseNode(root, results, maxNodes = 60)
        } finally {
            root.recycle()
        }
        return results
    }

    private fun traverseNode(node: AccessibilityNodeInfo?, list: MutableList<ScreenNodeData>, maxNodes: Int) {
        if (node == null || list.size >= maxNodes) return

        val text = node.text?.toString()?.trim() ?: ""
        val contentDesc = node.contentDescription?.toString()?.trim() ?: ""
        val viewId = node.viewIdResourceName ?: ""
        val className = node.className?.toString() ?: ""

        if (text.isNotEmpty() || contentDesc.isNotEmpty() || node.isClickable) {
            list.add(
                ScreenNodeData(
                    text = text,
                    contentDescription = contentDesc,
                    className = className,
                    isClickable = node.isClickable,
                    isEditable = node.isEditable,
                    viewId = viewId
                )
            )
        }

        val childCount = node.childCount
        for (i in 0 until childCount) {
            if (list.size >= maxNodes) break
            val child = node.getChild(i)
            if (child != null) {
                traverseNode(child, list, maxNodes)
                child.recycle()
            }
        }
    }

    /**
     * Finds and clicks a visible UI element by text or content description
     */
    fun clickElementByText(query: String): Boolean {
        val root = rootInActiveWindow ?: return false
        var clicked = false
        try {
            val matchingNodes = root.findAccessibilityNodeInfosByText(query)
            for (node in matchingNodes) {
                if (!clicked) {
                    var target: AccessibilityNodeInfo? = node
                    while (target != null && !target.isClickable) {
                        val parent = target.parent
                        if (target != node) target.recycle()
                        target = parent
                    }
                    if (target != null && target.isClickable) {
                        clicked = target.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        target.recycle()
                    }
                }
                node.recycle()
            }
        } finally {
            root.recycle()
        }
        return clicked
    }

    /**
     * Sets text on the first editable field matching query or currently focused editable field
     */
    fun enterText(targetText: String, textToInput: String): Boolean {
        val root = rootInActiveWindow ?: return false
        var success = false
        try {
            val nodes = root.findAccessibilityNodeInfosByText(targetText)
            for (node in nodes) {
                if (!success && node.isEditable) {
                    val arguments = Bundle().apply {
                        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, textToInput)
                    }
                    success = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                }
                node.recycle()
            }
        } finally {
            root.recycle()
        }
        return success
    }

    /**
     * Performs a scroll action forward or backward
     */
    fun performScroll(forward: Boolean): Boolean {
        val root = rootInActiveWindow ?: return false
        var scrolled = false
        val action = if (forward) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        try {
            scrolled = root.performAction(action)
            if (!scrolled) {
                // Try finding scrollable child
                val scrollable = findScrollableChild(root)
                if (scrollable != null) {
                    scrolled = scrollable.performAction(action)
                    scrollable.recycle()
                }
            }
        } finally {
            root.recycle()
        }
        return scrolled
    }

    private fun findScrollableChild(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isScrollable) return AccessibilityNodeInfo.obtain(node)
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findScrollableChild(child)
            child.recycle()
            if (found != null) return found
        }
        return null
    }

    /**
     * Dispatches a tap gesture at coordinate (x, y)
     */
    fun dispatchTap(x: Float, y: Float): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return false
        val path = Path().apply {
            moveTo(x, y)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()
        return dispatchGesture(gesture, null, null)
    }

    /**
     * Dispatches a swipe gesture from (x1, y1) to (x2, y2)
     */
    fun dispatchSwipe(x1: Float, y1: Float, x2: Float, y2: Float, durationMs: Long = 300): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return false
        val path = Path().apply {
            moveTo(x1, y1)
            lineTo(x2, y2)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs))
            .build()
        return dispatchGesture(gesture, null, null)
    }
}

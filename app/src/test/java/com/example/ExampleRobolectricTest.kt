package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.control.MyraDeviceController
import com.example.data.MyraFaceMode
import com.example.data.MyraPersonalityMode
import com.example.data.MyraPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `verify app name matches MYRA ULTRA`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MYRA ULTRA", appName)
  }

  @Test
  fun `verify MyraPreferences configuration`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = MyraPreferences(context)
    prefs.faceMode = MyraFaceMode.FACE_TWO
    assertEquals(MyraFaceMode.FACE_TWO, prefs.faceMode)

    prefs.personalityMode = MyraPersonalityMode.ULTRA_AUTONOMOUS
    assertEquals(MyraPersonalityMode.ULTRA_AUTONOMOUS, prefs.personalityMode)

    prefs.edgeGlowEnabled = true
    assertTrue(prefs.edgeGlowEnabled)
  }

  @Test
  fun `verify MyraDeviceController executes battery action`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val controller = MyraDeviceController(context)
    val result = controller.executeAction("check battery")
    assertNotNull(result)
    assertTrue(result.success)
  }
}

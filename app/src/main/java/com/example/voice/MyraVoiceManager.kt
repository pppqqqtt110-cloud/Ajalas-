package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class MyraVoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

/**
 * Manages Speech-To-Text (Voice input) and Text-To-Speech (Voice synthesis),
 * with reactive state and audio level flows to power Face One and Face Two animation loops.
 */
class MyraVoiceManager(
    private val context: Context,
    private val onSpeechRecognized: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private val TAG = "MyraVoiceManager"

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isTtsReady = false

    private val _voiceState = MutableStateFlow(MyraVoiceState.IDLE)
    val voiceState: StateFlow<MyraVoiceState> = _voiceState.asStateFlow()

    private val _audioRmsLevel = MutableStateFlow(0f)
    val audioRmsLevel: StateFlow<Float> = _audioRmsLevel.asStateFlow()

    private val _lastSpokenText = MutableStateFlow("")
    val lastSpokenText: StateFlow<String> = _lastSpokenText.asStateFlow()

    var pitch: Float = 1.05f
        set(value) {
            field = value
            tts?.setPitch(value)
        }

    var speechRate: Float = 1.0f
        set(value) {
            field = value
            tts?.setSpeechRate(value)
        }

    init {
        tts = TextToSpeech(context.applicationContext, this)
        initSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setPitch(pitch)
            tts?.setSpeechRate(speechRate)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _voiceState.value = MyraVoiceState.SPEAKING
                }

                override fun onDone(utteranceId: String?) {
                    _voiceState.value = MyraVoiceState.IDLE
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _voiceState.value = MyraVoiceState.IDLE
                }
            })
            isTtsReady = true
            Log.i(TAG, "TTS initialized successfully.")
        } else {
            Log.e(TAG, "TTS initialization failed.")
        }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _voiceState.value = MyraVoiceState.LISTENING
                    }

                    override fun onBeginningOfSpeech() {
                        _voiceState.value = MyraVoiceState.LISTENING
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        _audioRmsLevel.value = (rmsdB.coerceIn(0f, 10f) / 10f)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _voiceState.value = MyraVoiceState.THINKING
                    }

                    override fun onError(error: Int) {
                        _voiceState.value = MyraVoiceState.IDLE
                        _audioRmsLevel.value = 0f
                        Log.w(TAG, "SpeechRecognizer error: $error")
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        _voiceState.value = MyraVoiceState.IDLE
                        _audioRmsLevel.value = 0f
                        if (text.isNotBlank()) {
                            _lastSpokenText.value = text
                            onSpeechRecognized(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _lastSpokenText.value = it
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    fun startListening() {
        stopSpeaking()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        try {
            _voiceState.value = MyraVoiceState.LISTENING
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start speech recognizer: ${e.message}")
            _voiceState.value = MyraVoiceState.IDLE
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(TAG, "Stop listening error: ${e.message}")
        }
    }

    fun speak(text: String) {
        if (!isTtsReady) return
        stopListening()
        _voiceState.value = MyraVoiceState.SPEAKING
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MYRA_${System.currentTimeMillis()}")
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "MYRA_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        tts?.stop()
        if (_voiceState.value == MyraVoiceState.SPEAKING) {
            _voiceState.value = MyraVoiceState.IDLE
        }
    }

    fun setVoiceState(state: MyraVoiceState) {
        _voiceState.value = state
    }

    fun destroy() {
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}

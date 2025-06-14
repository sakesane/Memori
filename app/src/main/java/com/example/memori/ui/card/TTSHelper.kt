package com.example.memori.ui.card

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TTSHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                isReady = result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE
                // 如果英文不可用，可以尝试 Locale.CHINA 或 Locale.getDefault()
                if (!isReady) {
                    val zhResult = tts?.setLanguage(Locale.CHINA)
                    isReady = zhResult == TextToSpeech.LANG_AVAILABLE || zhResult == TextToSpeech.LANG_COUNTRY_AVAILABLE
                }
            }
        }
    }

    fun speak(text: String) {
        if (isReady) {
            android.util.Log.d("TTSHelper", "Speak: $text")
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            android.util.Log.d("TTSHelper", "TTS not ready")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
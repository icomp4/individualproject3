package com.individualproject3.math

import android.content.Context
import android.media.SoundPool
import android.media.AudioAttributes
import com.individualproject3.R

/**
 * Manages the sound effects for the math game
 */
class MathSoundManager(context: Context) {
    private val soundPool: SoundPool
    private var correctSound: Int = 0
    private var incorrectSound: Int = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        correctSound = soundPool.load(context, R.raw.correct_sound, 1)
        incorrectSound = soundPool.load(context, R.raw.incorrect_sound, 1)
    }

    fun playCorrectSound() {
        soundPool.play(correctSound, 1f, 1f, 1, 0, 1f)
    }

    fun playIncorrectSound() {
        soundPool.play(incorrectSound, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
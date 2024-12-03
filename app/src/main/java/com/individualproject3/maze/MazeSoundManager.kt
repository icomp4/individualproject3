package com.individualproject3.maze


import android.content.Context
import android.media.SoundPool
import android.media.AudioAttributes
import com.individualproject3.R

/**
 * Manages the sound effects for the maze game]
 */
class MazeSoundManager(context: Context) {
    private val soundPool: SoundPool
    private var moveSound: Int = 0
    private var invalidMoveSound: Int = 0
    private var completionSound: Int = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        moveSound = soundPool.load(context, R.raw.move_sound, 1)
        invalidMoveSound = soundPool.load(context, R.raw.invalid_move_sound, 1)
        completionSound = soundPool.load(context, R.raw.completion_sound, 1)
    }

    fun playMoveSound() {
        soundPool.play(moveSound, 1f, 1f, 1, 0, 1f)
    }

    fun playInvalidMoveSound() {
        soundPool.play(invalidMoveSound, 1f, 1f, 1, 0, 1f)
    }

    fun playCompletionSound() {
        soundPool.play(completionSound, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
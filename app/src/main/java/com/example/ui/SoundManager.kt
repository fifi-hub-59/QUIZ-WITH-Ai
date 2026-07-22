package com.example.ui

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.*

object SoundManager {
    private var isSoundEnabled = true
    private var isMusicEnabled = true

    private var musicJob: Job? = null
    private val musicScope = CoroutineScope(Dispatchers.Default)
    private var currentScreenMusic: String? = null
    private var tickCount = 0

    // Single streaming AudioTrack for background music and synth tones
    private var musicAudioTrack: AudioTrack? = null
    private const val SAMPLE_RATE = 22050

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufSize, SAMPLE_RATE * 2)
            
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
                
            val audioFormat = AudioFormat.Builder()
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(SAMPLE_RATE)
                .build()

            musicAudioTrack = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            musicAudioTrack?.play()
        } catch (t: Throwable) {
            Log.e("SoundManager", "Failed to initialize streaming AudioTrack", t)
        }
    }

    private fun writeToTrack(buffer: ShortArray) {
        try {
            if (musicAudioTrack == null || musicAudioTrack?.state == AudioTrack.STATE_UNINITIALIZED) {
                initAudioTrack()
            }
            if (musicAudioTrack?.playState != AudioTrack.PLAYSTATE_PLAYING) {
                musicAudioTrack?.play()
            }
            musicAudioTrack?.write(buffer, 0, buffer.size)
        } catch (t: Throwable) {
            Log.e("SoundManager", "Error writing to streaming AudioTrack", t)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        val changed = isSoundEnabled != enabled
        isSoundEnabled = enabled
        if (changed) {
            if (!enabled) {
                stopAllSounds()
            } else {
                // Restart music for the current screen if enabled
                val screen = currentScreenMusic
                if (screen != null && isMusicEnabled) {
                    currentScreenMusic = null
                    startMusicForScreen(screen)
                }
            }
        }
    }

    fun setMusicEnabled(enabled: Boolean) {
        val changed = isMusicEnabled != enabled
        isMusicEnabled = enabled
        if (changed) {
            if (!enabled) {
                stopMusic()
            } else {
                // Restart music for the current screen if enabled
                val screen = currentScreenMusic
                if (screen != null && isSoundEnabled) {
                    currentScreenMusic = null
                    startMusicForScreen(screen)
                }
            }
        }
    }

    /**
     * Generates and plays a beautiful pure sine wave with a smooth volume envelope
     * through the single continuous streaming AudioTrack.
     */
    fun playSynthTone(frequency: Double, durationMs: Int, volume: Double = 0.5) {
        if (!isSoundEnabled) return
        try {
            val numSamples = (durationMs / 1000.0 * SAMPLE_RATE).toInt()
            val buffer = ShortArray(numSamples)
            val maxVol = (32767 * volume).coerceIn(0.0, 32767.0)

            for (i in 0 until numSamples) {
                // Apply a smooth decay envelope to the tail 20% of the note
                val envelope = if (i > numSamples * 0.8) {
                    (numSamples - i).toDouble() / (numSamples * 0.2)
                } else if (i < numSamples * 0.05) {
                    // Quick attack to avoid popping at the start
                    i.toDouble() / (numSamples * 0.05)
                } else {
                    1.0
                }
                val angle = 2.0 * Math.PI * i * frequency / SAMPLE_RATE
                buffer[i] = (Math.sin(angle) * maxVol * envelope).toInt().toShort()
            }

            writeToTrack(buffer)
        } catch (t: Throwable) {
            Log.e("SoundManager", "Error playing synth tone", t)
        }
    }

    fun startMusicForScreen(screen: String) {
        if (currentScreenMusic == screen) return
        currentScreenMusic = screen
        musicJob?.cancel()

        if (screen == "game" || !isMusicEnabled || !isSoundEnabled) {
            // Gameplay screen has its own interactive ticking audio via ToneGenerator
            return
        }

        musicJob = musicScope.launch {
            while (isActive && isMusicEnabled && isSoundEnabled && currentScreenMusic == screen) {
                try {
                    when (screen) {
                        "login" -> {
                            // Mysterious space-synth melody (A4, C5, E5, G5)
                            val notes = listOf(440.0, 523.25, 659.25, 783.99)
                            for (freq in notes) {
                                if (!isActive || currentScreenMusic != screen) break
                                playSynthTone(freq, 400, volume = 0.4)
                                delay(1200)
                            }
                        }
                        "home", "category_selection", "tutorial", "leaderboard" -> {
                            // Relaxing lounge chord progression (C4, E4, G4, A4, C5)
                            val notes = listOf(261.63, 329.63, 392.00, 440.00, 523.25)
                            for (freq in notes) {
                                if (!isActive || currentScreenMusic != screen) break
                                playSynthTone(freq, 500, volume = 0.35)
                                delay(1000)
                            }
                        }
                        "store", "settings" -> {
                            // Cosmic crystal shopping bells (G5, C5, E5, A5)
                            val notes = listOf(783.99, 523.25, 659.25, 880.00)
                            for (freq in notes) {
                                if (!isActive || currentScreenMusic != screen) break
                                playSynthTone(freq, 300, volume = 0.3)
                                delay(800)
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    break
                } catch (t: Throwable) {
                    Log.e("SoundManager", "Error in music synthesizer loop", t)
                    delay(2000)
                }
            }
        }
    }

    fun stopMusic() {
        musicJob?.cancel()
        musicJob = null
        currentScreenMusic = null
        try {
            musicAudioTrack?.pause()
            musicAudioTrack?.flush()
        } catch (t: Throwable) {
            Log.e("SoundManager", "Error stopping music track", t)
        }
    }

    fun stopAllSounds() {
        stopMusic()
    }

    fun playCorrect() {
        if (!isSoundEnabled) return
        // Beautiful double chime arpeggio: C5 -> E5
        playSynthTone(frequency = 523.25, durationMs = 80, volume = 0.4)
        playSynthTone(frequency = 659.25, durationMs = 150, volume = 0.4)
    }

    fun playWrong() {
        if (!isSoundEnabled) return
        // Low buzzing descending tone
        playSynthTone(frequency = 220.0, durationMs = 200, volume = 0.5)
    }

    fun playCelebration() {
        if (!isSoundEnabled) return
        // Energetic ascending arpeggio: E5 -> G5 -> C6
        playSynthTone(frequency = 659.25, durationMs = 60, volume = 0.4)
        playSynthTone(frequency = 783.99, durationMs = 60, volume = 0.4)
        playSynthTone(frequency = 1046.50, durationMs = 150, volume = 0.5)
    }

    fun playGameOver() {
        if (!isSoundEnabled) return
        // Melancholic descending progression: F4 -> Eb4 -> C4
        playSynthTone(frequency = 349.23, durationMs = 150, volume = 0.4)
        playSynthTone(frequency = 311.13, durationMs = 150, volume = 0.4)
        playSynthTone(frequency = 261.63, durationMs = 250, volume = 0.5)
    }

    fun playClick() {
        if (!isSoundEnabled) return
        // Soft, futuristic high-pitched click tone
        playSynthTone(frequency = 1000.0, durationMs = 30, volume = 0.25)
    }

    fun playTimerTick(isUrgent: Boolean) {
        if (!isSoundEnabled) return
        tickCount++
        if (isUrgent) {
            playSynthTone(frequency = 1200.0, durationMs = 50, volume = 0.3)
        } else {
            playSynthTone(frequency = 800.0, durationMs = 30, volume = 0.15)
        }
    }
}

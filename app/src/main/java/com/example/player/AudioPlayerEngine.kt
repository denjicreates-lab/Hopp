package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.model.Track
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RepeatMode { OFF, ALL, ONE }

enum class EqualizerPreset(val title: String) {
    NORMAL("Normal / Flat"),
    BASS_BOOST("Bass Boost"),
    TREBLE_BOOST("Crystal Treble"),
    VOCAL("Acoustic / Vocal"),
    ELECTRONIC("Club Electronic")
}

private enum class PlayerState {
    IDLE,
    PREPARING,
    PREPARED,
    PLAYING,
    PAUSED,
    ERROR
}

class AudioPlayerEngine(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var playerState = PlayerState.IDLE
    private var playWhenReady = false
    private val mainScope = CoroutineScope(Dispatchers.Main + Job())

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(180000L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _equalizerPreset = MutableStateFlow(EqualizerPreset.NORMAL)
    val equalizerPreset: StateFlow<EqualizerPreset> = _equalizerPreset.asStateFlow()

    private val _sleepTimerMinutesLeft = MutableStateFlow<Int?>(null)
    val sleepTimerMinutesLeft: StateFlow<Int?> = _sleepTimerMinutesLeft.asStateFlow()

    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null

    var onTrackCompleted: (() -> Unit)? = null

    init {
        startProgressTracking()
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = mainScope.launch {
            while (isActive) {
                delay(250)
                val player = mediaPlayer
                if (player != null && playerState == PlayerState.PLAYING) {
                    try {
                        if (player.isPlaying) {
                            _currentPositionMs.value = player.currentPosition.toLong()
                            if (player.duration > 0) {
                                _durationMs.value = player.duration.toLong()
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("AudioPlayerEngine", "Error checking player position: ${e.message}")
                    }
                } else if (_isPlaying.value && playerState != PlayerState.PREPARING) {
                    // Virtual progress tracking for streaming fallback / sample tracks
                    val newPos = _currentPositionMs.value + 250L
                    if (newPos >= _durationMs.value) {
                        if (_repeatMode.value == RepeatMode.ONE) {
                            _currentPositionMs.value = 0L
                        } else {
                            _isPlaying.value = false
                            _currentPositionMs.value = 0L
                            onTrackCompleted?.invoke()
                        }
                    } else {
                        _currentPositionMs.value = newPos
                    }
                }
            }
        }
    }

    fun playTrack(track: Track, autoPlay: Boolean = true) {
        _currentTrack.value = track
        _currentPositionMs.value = 0L
        _durationMs.value = track.durationMs
        playWhenReady = autoPlay
        _isPlaying.value = autoPlay
        _isBuffering.value = autoPlay && track.streamUrl.isNotBlank()

        safeReleaseMediaPlayer()

        if (track.streamUrl.isBlank()) {
            playerState = PlayerState.IDLE
            _isBuffering.value = false
            return
        }

        try {
            val player = MediaPlayer()
            mediaPlayer = player
            playerState = PlayerState.PREPARING

            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            player.setDataSource(context, Uri.parse(track.streamUrl))

            player.setOnPreparedListener { mp ->
                // Ensure this prepared callback is for the current active player instance
                if (mediaPlayer !== mp) return@setOnPreparedListener
                playerState = PlayerState.PREPARED
                _isBuffering.value = false
                if (mp.duration > 0) {
                    _durationMs.value = mp.duration.toLong()
                }

                if (playWhenReady) {
                    try {
                        mp.start()
                        playerState = PlayerState.PLAYING
                        _isPlaying.value = true
                    } catch (e: Exception) {
                        Log.w("AudioPlayerEngine", "start() failed after prepared: ${e.message}")
                        playerState = PlayerState.ERROR
                        safeReleaseMediaPlayer()
                        _isPlaying.value = true
                    }
                } else {
                    playerState = PlayerState.PAUSED
                    _isPlaying.value = false
                }
            }

            player.setOnCompletionListener { mp ->
                if (mediaPlayer !== mp) return@setOnCompletionListener
                if (_repeatMode.value == RepeatMode.ONE) {
                    try {
                        mp.seekTo(0)
                        mp.start()
                        playerState = PlayerState.PLAYING
                        _isPlaying.value = true
                    } catch (e: Exception) {
                        _isPlaying.value = false
                    }
                } else {
                    _isPlaying.value = false
                    playerState = PlayerState.PAUSED
                    onTrackCompleted?.invoke()
                }
            }

            player.setOnErrorListener { mp, what, extra ->
                Log.w("AudioPlayerEngine", "MediaPlayer error $what / $extra, falling back to simulated playback")
                if (mediaPlayer === mp) {
                    playerState = PlayerState.ERROR
                    _isBuffering.value = false
                    safeReleaseMediaPlayer()
                    // Maintain user's desired playing state in fallback simulation
                    _isPlaying.value = playWhenReady
                }
                true
            }

            player.prepareAsync()
        } catch (e: Exception) {
            Log.e("AudioPlayerEngine", "Failed to initialize MediaPlayer: ${e.message}")
            safeReleaseMediaPlayer()
            playerState = PlayerState.ERROR
            _isBuffering.value = false
            _isPlaying.value = autoPlay
        }
    }

    fun togglePlayPause() {
        if (playerState == PlayerState.PREPARING) {
            // Player is asynchronously preparing; toggle user intent without calling native start/pause
            playWhenReady = !playWhenReady
            _isPlaying.value = playWhenReady
            return
        }

        val player = mediaPlayer
        if (player != null && (playerState == PlayerState.PREPARED || playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED)) {
            try {
                if (player.isPlaying) {
                    player.pause()
                    playerState = PlayerState.PAUSED
                    playWhenReady = false
                    _isPlaying.value = false
                } else {
                    player.start()
                    playerState = PlayerState.PLAYING
                    playWhenReady = true
                    _isPlaying.value = true
                }
            } catch (e: Exception) {
                Log.w("AudioPlayerEngine", "Error during togglePlayPause: ${e.message}")
                safeReleaseMediaPlayer()
                playerState = PlayerState.ERROR
                playWhenReady = !playWhenReady
                _isPlaying.value = playWhenReady
            }
        } else {
            // Fallback / simulated player mode
            playWhenReady = !playWhenReady
            _isPlaying.value = playWhenReady
        }
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0L, _durationMs.value.coerceAtLeast(1000L))
        _currentPositionMs.value = clamped
        val player = mediaPlayer
        if (player != null && (playerState == PlayerState.PREPARED || playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED)) {
            try {
                player.seekTo(clamped.toInt())
            } catch (e: Exception) {
                Log.w("AudioPlayerEngine", "Error in seekTo: ${e.message}")
            }
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun cycleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun setEqualizerPreset(preset: EqualizerPreset) {
        _equalizerPreset.value = preset
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        _sleepTimerMinutesLeft.value = minutes

        if (minutes != null && minutes > 0) {
            sleepTimerJob = mainScope.launch {
                var remaining = minutes
                while (remaining > 0 && isActive) {
                    delay(60000L)
                    remaining--
                    _sleepTimerMinutesLeft.value = remaining
                }
                // Time up! Pause playback
                if (_isPlaying.value) {
                    togglePlayPause()
                }
                _sleepTimerMinutesLeft.value = null
            }
        }
    }

    private fun safeReleaseMediaPlayer() {
        val player = mediaPlayer ?: return
        mediaPlayer = null
        playerState = PlayerState.IDLE
        try {
            player.setOnPreparedListener(null)
            player.setOnCompletionListener(null)
            player.setOnErrorListener(null)
            if (player.isPlaying) {
                player.stop()
            }
            player.reset()
            player.release()
        } catch (e: Exception) {
            Log.w("AudioPlayerEngine", "Error releasing MediaPlayer: ${e.message}")
        }
    }

    fun release() {
        progressJob?.cancel()
        sleepTimerJob?.cancel()
        safeReleaseMediaPlayer()
    }
}

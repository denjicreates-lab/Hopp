package com.example.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiService
import com.example.data.MusicRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MusicRepository(application)
    val playerEngine = AudioPlayerEngine(application)
    private val geminiService = GeminiService()

    // Tab Navigation
    private val _activeTab = MutableStateFlow("home")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    // Full screen & overlay states
    private val _isFullScreenNowPlayingOpen = MutableStateFlow(false)
    val isFullScreenNowPlayingOpen: StateFlow<Boolean> = _isFullScreenNowPlayingOpen.asStateFlow()

    private val _isQueueSheetOpen = MutableStateFlow(false)
    val isQueueSheetOpen: StateFlow<Boolean> = _isQueueSheetOpen.asStateFlow()

    private val _isLyricsOpen = MutableStateFlow(false)
    val isLyricsOpen: StateFlow<Boolean> = _isLyricsOpen.asStateFlow()

    private val _isSleepTimerDialogOpen = MutableStateFlow(false)
    val isSleepTimerDialogOpen: StateFlow<Boolean> = _isSleepTimerDialogOpen.asStateFlow()

    // Toast & notification
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Tracks, Playlists, Providers
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    private val _recentlyPlayed = MutableStateFlow<List<Track>>(emptyList())
    val recentlyPlayed: StateFlow<List<Track>> = _recentlyPlayed.asStateFlow()

    private val _connections = MutableStateFlow<List<ProviderConnection>>(emptyList())
    val connections: StateFlow<List<ProviderConnection>> = _connections.asStateFlow()

    // Filter & Search
    private val _selectedProviderFilter = MutableStateFlow<MusicProviderType?>(null)
    val selectedProviderFilter: StateFlow<MusicProviderType?> = _selectedProviderFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Theme & Navigation Customization
    private val _themeConfig = MutableStateFlow(repository.getSavedTheme())
    val themeConfig: StateFlow<ThemeConfig> = _themeConfig.asStateFlow()

    private val _navConfig = MutableStateFlow(repository.getSavedNavigation())
    val navConfig: StateFlow<NavigationConfig> = _navConfig.asStateFlow()

    private val _currentLanguage = MutableStateFlow(repository.getSavedLanguage())
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // AI Features
    private val _moodSuggestions = MutableStateFlow<List<MoodSongSuggestion>>(emptyList())
    val moodSuggestions: StateFlow<List<MoodSongSuggestion>> = _moodSuggestions.asStateFlow()

    private val _isCuratingMood = MutableStateFlow(false)
    val isCuratingMood: StateFlow<Boolean> = _isCuratingMood.asStateFlow()

    private val _songRecognitionResult = MutableStateFlow<SongRecognitionResult?>(null)
    val songRecognitionResult: StateFlow<SongRecognitionResult?> = _songRecognitionResult.asStateFlow()

    private val _isRecognizingSong = MutableStateFlow(false)
    val isRecognizingSong: StateFlow<Boolean> = _isRecognizingSong.asStateFlow()

    private val _recognitionTimerSec = MutableStateFlow(0)
    val recognitionTimerSec: StateFlow<Int> = _recognitionTimerSec.asStateFlow()

    // Local library tracks imported by user
    private val _localTracks = MutableStateFlow<List<Track>>(emptyList())
    val localTracks: StateFlow<List<Track>> = _localTracks.asStateFlow()

    init {
        loadInitialData()
        playerEngine.onTrackCompleted = {
            playNextTrack()
        }
    }

    private fun loadInitialData() {
        val initialPlaylists = repository.getInitialPlaylists()
        _playlists.value = initialPlaylists
        _connections.value = repository.getInitialConnections()
        val allTracks = repository.defaultTracks
        _queue.value = allTracks
        _recentlyPlayed.value = allTracks.take(4)

        // Set initial track if available
        if (playerEngine.currentTrack.value == null && allTracks.isNotEmpty()) {
            // Prepared with first track in paused state so it does not auto-blast audio
            playerEngine.playTrack(allTracks.first(), autoPlay = false)
        }
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    fun setFullScreenNowPlayingOpen(open: Boolean) {
        _isFullScreenNowPlayingOpen.value = open
    }

    fun setQueueSheetOpen(open: Boolean) {
        _isQueueSheetOpen.value = open
    }

    fun setLyricsOpen(open: Boolean) {
        _isLyricsOpen.value = open
    }

    fun setSleepTimerDialogOpen(open: Boolean) {
        _isSleepTimerDialogOpen.value = open
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun playTrack(track: Track) {
        playerEngine.playTrack(track)
        // Update recently played
        val list = _recentlyPlayed.value.toMutableList()
        list.removeAll { it.id == track.id }
        list.add(0, track)
        _recentlyPlayed.value = list.take(10)
    }

    fun playPlaylist(playlist: Playlist) {
        if (playlist.tracks.isNotEmpty()) {
            _queue.value = playlist.tracks
            playTrack(playlist.tracks.first())
            showToast("Playing: ${playlist.title}")
        }
    }

    fun playNextTrack() {
        val current = playerEngine.currentTrack.value ?: return
        val currentQueue = _queue.value
        if (currentQueue.isEmpty()) return

        val currentIndex = currentQueue.indexOfFirst { it.id == current.id }
        if (currentIndex in 0 until currentQueue.size - 1) {
            playTrack(currentQueue[currentIndex + 1])
        } else if (currentQueue.isNotEmpty()) {
            playTrack(currentQueue.first())
        }
    }

    fun playPreviousTrack() {
        val current = playerEngine.currentTrack.value ?: return
        val currentQueue = _queue.value
        if (currentQueue.isEmpty()) return

        val currentIndex = currentQueue.indexOfFirst { it.id == current.id }
        if (currentIndex > 0) {
            playTrack(currentQueue[currentIndex - 1])
        } else if (currentQueue.isNotEmpty()) {
            playTrack(currentQueue.last())
        }
    }

    fun setProviderFilter(provider: MusicProviderType?) {
        _selectedProviderFilter.value = provider
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleProviderConnection(provider: MusicProviderType) {
        val updated = _connections.value.map { conn ->
            if (conn.provider == provider) {
                val newStatus = !conn.isConnected
                repository.setConnection(provider, newStatus)
                conn.copy(isConnected = newStatus)
            } else conn
        }
        _connections.value = updated
        val changed = updated.firstOrNull { it.provider == provider }
        val statusText = if (changed?.isConnected == true) "Connected" else "Disconnected"
        showToast("${provider.displayName}: $statusText")
    }

    // Theme Customization
    fun updateTheme(newConfig: ThemeConfig) {
        _themeConfig.value = newConfig
        repository.saveTheme(newConfig)
        showToast("Theme updated: ${newConfig.preset.title}")
    }

    fun updateNavigation(newConfig: NavigationConfig) {
        _navConfig.value = newConfig
        repository.saveNavigation(newConfig)
        showToast("Navigation style: ${newConfig.navType.title}")
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
        repository.saveLanguage(lang)
        showToast("Language changed to ${lang.label}")
    }

    fun exportThemeJson(): String {
        return repository.exportThemeToJson(_themeConfig.value)
    }

    fun importThemeJson(json: String): Boolean {
        val imported = repository.importThemeFromJson(json)
        return if (imported != null) {
            updateTheme(imported)
            true
        } else {
            showToast("Invalid theme JSON")
            false
        }
    }

    fun resetThemeToDefault() {
        val defaultConfig = ThemeConfig()
        updateTheme(defaultConfig)
        showToast("Theme reset to Midnight default")
    }

    // AI Mood Curation
    fun curateMood(moodText: String) {
        viewModelScope.launch {
            _isCuratingMood.value = true
            val prefLang = when (_currentLanguage.value) {
                AppLanguage.HINDI -> "Hindi"
                AppLanguage.HINGLISH -> "Hindi, Punjabi, and Indian Indie"
                AppLanguage.ENGLISH -> "Global & English"
            }
            val result = geminiService.curateMoodSongs(moodText, prefLang)
            _isCuratingMood.value = false
            result.onSuccess { list ->
                _moodSuggestions.value = list
                showToast("15 mood songs curated!")
            }.onFailure {
                showToast("Could not curate mood songs. Try again.")
            }
        }
    }

    fun playMoodSuggestionsAsQueue() {
        val suggestions = _moodSuggestions.value
        if (suggestions.isEmpty()) return

        val newTracks = suggestions.mapIndexed { idx, s ->
            Track(
                id = "mood_t_$idx",
                title = s.title,
                artist = s.artist,
                album = "Hopp AI Mood Mix",
                durationMs = 190000L,
                coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=500&auto=format&fit=crop&q=80",
                provider = MusicProviderType.SPOTIFY,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                lyrics = "[00:10.00] ${s.reason}"
            )
        }
        _queue.value = newTracks
        playTrack(newTracks.first())
        showToast("Playing AI Mood Queue (${newTracks.size} tracks)")
    }

    // Song Recognition
    fun startSongRecognition() {
        viewModelScope.launch {
            _isRecognizingSong.value = true
            _songRecognitionResult.value = null
            _recognitionTimerSec.value = 0

            // Simulate 6 seconds of acoustic listening with live countdown
            for (i in 1..6) {
                kotlinx.coroutines.delay(1000)
                _recognitionTimerSec.value = i
            }

            val result = geminiService.identifySong(null, 6)
            _isRecognizingSong.value = false
            result.onSuccess {
                _songRecognitionResult.value = it
            }.onFailure {
                showToast("Could not identify song")
            }
        }
    }

    // Create custom Hopp Playlist
    fun createHoppPlaylist(title: String, description: String) {
        val newPlaylist = Playlist(
            id = "p_custom_${System.currentTimeMillis()}",
            title = title.ifBlank { "My Hopp Mix" },
            description = description.ifBlank { "Personal mixed cross-platform playlist" },
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.SPOTIFY,
            trackCount = repository.defaultTracks.take(4).size,
            tracks = repository.defaultTracks.take(4),
            isHoppCustom = true,
            isUserCreated = true
        )
        _playlists.value = listOf(newPlaylist) + _playlists.value
        showToast("Playlist created: ${newPlaylist.title}")
    }

    // Import playlist from URL or text
    fun importPlaylistFromText(name: String, rawContent: String, provider: MusicProviderType) {
        val lines = rawContent.split("\n").filter { it.isNotBlank() }
        val importedTracks = lines.mapIndexed { index, line ->
            val parts = line.split("-", "by", ":")
            val title = parts.getOrNull(0)?.trim() ?: line.trim()
            val artist = parts.getOrNull(1)?.trim() ?: "Imported Artist"
            Track(
                id = "imported_${System.currentTimeMillis()}_$index",
                title = title,
                artist = artist,
                album = "Imported Tracklist",
                durationMs = 180000L,
                coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&auto=format&fit=crop&q=80",
                provider = provider,
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
            )
        }

        val newPlaylist = Playlist(
            id = "p_import_${System.currentTimeMillis()}",
            title = name.ifBlank { "Imported ${provider.displayName} Playlist" },
            description = "Imported from external link/tracklist (${importedTracks.size} tracks)",
            coverUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500&auto=format&fit=crop&q=80",
            provider = provider,
            trackCount = importedTracks.size,
            tracks = importedTracks,
            isUserCreated = true
        )

        _playlists.value = listOf(newPlaylist) + _playlists.value
        showToast("Imported ${importedTracks.size} tracks successfully!")
    }

    // Add local tracks from device file picker
    fun addLocalTrack(title: String, artist: String, uriString: String) {
        val localTrack = Track(
            id = "local_${System.currentTimeMillis()}",
            title = title,
            artist = artist.ifBlank { "Device Audio" },
            album = "Local Storage",
            durationMs = 200000L,
            coverUrl = "",
            provider = MusicProviderType.LOCAL,
            streamUrl = uriString,
            isLossless = true
        )
        _localTracks.value = listOf(localTrack) + _localTracks.value
        _queue.value = listOf(localTrack) + _queue.value
        showToast("Added local track: $title")
    }

    override fun onCleared() {
        super.onCleared()
        playerEngine.release()
    }
}

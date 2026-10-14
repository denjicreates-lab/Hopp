package com.example

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.*
import com.example.player.PlayerViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HoppApp()
            }
        }
    }
}

@Composable
fun HoppApp(
    viewModel: PlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("hopp_app_state", Context.MODE_PRIVATE) }

    var showSplash by remember { mutableStateOf(true) }
    var showOnboarding by remember {
        mutableStateOf(sharedPrefs.getBoolean("first_launch", true))
    }

    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val isFullScreenNowPlayingOpen by viewModel.isFullScreenNowPlayingOpen.collectAsStateWithLifecycle()
    val isQueueSheetOpen by viewModel.isQueueSheetOpen.collectAsStateWithLifecycle()
    val isSleepTimerDialogOpen by viewModel.isSleepTimerDialogOpen.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val currentTrack by viewModel.playerEngine.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.playerEngine.isPlaying.collectAsStateWithLifecycle()
    val currentPositionMs by viewModel.playerEngine.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.playerEngine.durationMs.collectAsStateWithLifecycle()
    val repeatMode by viewModel.playerEngine.repeatMode.collectAsStateWithLifecycle()
    val isShuffle by viewModel.playerEngine.isShuffle.collectAsStateWithLifecycle()
    val sleepTimerMinutesLeft by viewModel.playerEngine.sleepTimerMinutesLeft.collectAsStateWithLifecycle()
    val equalizerPreset by viewModel.playerEngine.equalizerPreset.collectAsStateWithLifecycle()

    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val queue by viewModel.queue.collectAsStateWithLifecycle()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsStateWithLifecycle()
    val connections by viewModel.connections.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedProviderFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
    val navConfig by viewModel.navConfig.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()

    val moodSuggestions by viewModel.moodSuggestions.collectAsStateWithLifecycle()
    val isCuratingMood by viewModel.isCuratingMood.collectAsStateWithLifecycle()
    val songRecognitionResult by viewModel.songRecognitionResult.collectAsStateWithLifecycle()
    val isRecognizingSong by viewModel.isRecognizingSong.collectAsStateWithLifecycle()
    val recognitionTimerSec by viewModel.recognitionTimerSec.collectAsStateWithLifecycle()
    val localTracks by viewModel.localTracks.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    // Predictive Back Handling
    BackHandler(enabled = isFullScreenNowPlayingOpen || isQueueSheetOpen || activeTab != "home") {
        when {
            isQueueSheetOpen -> viewModel.setQueueSheetOpen(false)
            isFullScreenNowPlayingOpen -> viewModel.setFullScreenNowPlayingOpen(false)
            activeTab != "home" -> viewModel.setActiveTab("home")
        }
    }

    val accentColor = Color(themeConfig.accentColorHex)
    val secondaryColor = Color(themeConfig.secondaryColorHex)

    val backgroundBrush = when (themeConfig.backgroundStyle) {
        BackgroundStyle.SOLID -> Brush.verticalGradient(listOf(Color(0xFF0B0B10), Color(0xFF0B0B10)))
        BackgroundStyle.GRADIENT -> Brush.verticalGradient(listOf(Color(0xFF140D24), Color(0xFF0D0B16), Color(0xFF08060E)))
        BackgroundStyle.BLURRED_ART -> Brush.radialGradient(listOf(accentColor.copy(alpha = 0.25f), Color(0xFF0B0B10)))
        BackgroundStyle.ANIMATED_MESH -> Brush.sweepGradient(listOf(accentColor.copy(alpha = 0.2f), secondaryColor.copy(alpha = 0.2f), Color(0xFF0B0B10)))
    }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = { showSplash = false }
        )
    } else if (showOnboarding) {
        OnboardingScreen(
            currentLanguage = currentLanguage,
            themeConfig = themeConfig,
            onThemeSelect = { preset ->
                val newCfg = themeConfig.copy(preset = preset)
                viewModel.updateTheme(newCfg)
            },
            onCompleteOnboarding = {
                sharedPrefs.edit().putBoolean("first_launch", false).apply()
                showOnboarding = false
            }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                HoppTopBar(
                    currentLanguage = currentLanguage,
                    onLanguageClick = {
                        val next = when (currentLanguage) {
                            AppLanguage.HINGLISH -> AppLanguage.HINDI
                            AppLanguage.HINDI -> AppLanguage.ENGLISH
                            AppLanguage.ENGLISH -> AppLanguage.HINGLISH
                        }
                        viewModel.setLanguage(next)
                    },
                    onQuickListenClick = {
                        viewModel.setActiveTab("discover")
                        viewModel.startSongRecognition()
                    },
                    onAccountsClick = { viewModel.setActiveTab("accounts") },
                    onSettingsClick = { viewModel.setActiveTab("settings") },
                    accentColor = accentColor,
                    secondaryColor = secondaryColor
                )
            },
            bottomBar = {
                Column {
                    // Mini player bar
                    if (currentTrack != null) {
                        MiniPlayerBar(
                            track = currentTrack,
                            isPlaying = isPlaying,
                            progressMs = currentPositionMs,
                            durationMs = durationMs,
                            accentColor = accentColor,
                            cornerRadiusDp = themeConfig.cornerRadiusDp,
                            onExpandClick = { viewModel.setFullScreenNowPlayingOpen(true) },
                            onPlayPauseClick = { viewModel.playerEngine.togglePlayPause() },
                            onNextClick = { viewModel.playNextTrack() }
                        )
                    }

                    // Navigation bar (if bottom dock or bottom bar)
                    if (navConfig.navType == NavigationType.BOTTOM_BAR || navConfig.navType == NavigationType.FLOATING_DOCK) {
                        HoppNavigationBar(
                            activeTab = activeTab,
                            onTabSelected = { viewModel.setActiveTab(it) },
                            navConfig = navConfig,
                            currentLanguage = currentLanguage,
                            accentColor = accentColor
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundBrush)
                    .padding(innerPadding)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Sidebar layout if navigation type is SIDEBAR
                    if (navConfig.navType == NavigationType.SIDEBAR) {
                        HoppNavigationBar(
                            activeTab = activeTab,
                            onTabSelected = { viewModel.setActiveTab(it) },
                            navConfig = navConfig,
                            currentLanguage = currentLanguage,
                            accentColor = accentColor
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        // Top tabs if navigation type is TOP_TABS
                        if (navConfig.navType == NavigationType.TOP_TABS) {
                            HoppNavigationBar(
                                activeTab = activeTab,
                                onTabSelected = { viewModel.setActiveTab(it) },
                                navConfig = navConfig,
                                currentLanguage = currentLanguage,
                                accentColor = accentColor
                            )
                        }

                        // Main Screen View Switching
                        Box(modifier = Modifier.weight(1f)) {
                            when (activeTab) {
                                "home" -> HomeScreen(
                                    currentLanguage = currentLanguage,
                                    currentTrack = currentTrack,
                                    isPlaying = isPlaying,
                                    recentlyPlayed = recentlyPlayed,
                                    playlists = playlists,
                                    themeConfig = themeConfig,
                                    onTrackClick = { viewModel.playTrack(it) },
                                    onPlaylistClick = { viewModel.playPlaylist(it) },
                                    onMoodChipClick = { chip ->
                                        viewModel.setActiveTab("discover")
                                        viewModel.curateMood(chip)
                                    },
                                    onQuickListenClick = {
                                        viewModel.setActiveTab("discover")
                                        viewModel.startSongRecognition()
                                    }
                                )
                                "library" -> LibraryScreen(
                                    playlists = playlists,
                                    selectedFilter = selectedFilter,
                                    themeConfig = themeConfig,
                                    currentLanguage = currentLanguage,
                                    onFilterChange = { viewModel.setProviderFilter(it) },
                                    onPlaylistClick = { viewModel.playPlaylist(it) },
                                    onCreateHoppPlaylist = { t, d -> viewModel.createHoppPlaylist(t, d) },
                                    onImportPlaylist = { name, content, prov ->
                                        viewModel.importPlaylistFromText(name, content, prov)
                                    }
                                )
                                "search" -> SearchScreen(
                                    searchQuery = searchQuery,
                                    allTracks = queue,
                                    themeConfig = themeConfig,
                                    currentLanguage = currentLanguage,
                                    onQueryChange = { viewModel.setSearchQuery(it) },
                                    onTrackClick = { viewModel.playTrack(it) }
                                )
                                "discover" -> DiscoverAiScreen(
                                    currentLanguage = currentLanguage,
                                    moodSuggestions = moodSuggestions,
                                    isCuratingMood = isCuratingMood,
                                    songRecognitionResult = songRecognitionResult,
                                    isRecognizingSong = isRecognizingSong,
                                    recognitionTimerSec = recognitionTimerSec,
                                    themeConfig = themeConfig,
                                    onCurateMood = { viewModel.curateMood(it) },
                                    onPlayAllMoodSongs = { viewModel.playMoodSuggestionsAsQueue() },
                                    onStartSongRecognition = { viewModel.startSongRecognition() }
                                )
                                "open_music" -> OpenMusicScreen(
                                    openTracks = queue.filter { it.provider == MusicProviderType.OPEN_MUSIC },
                                    localTracks = localTracks,
                                    themeConfig = themeConfig,
                                    currentLanguage = currentLanguage,
                                    onTrackClick = { viewModel.playTrack(it) },
                                    onAddLocalTrack = { t, a, u -> viewModel.addLocalTrack(t, a, u) },
                                    onShowToast = { viewModel.showToast(it) }
                                )
                                "customize" -> CustomizeScreen(
                                    themeConfig = themeConfig,
                                    navConfig = navConfig,
                                    currentLanguage = currentLanguage,
                                    onThemeChange = { viewModel.updateTheme(it) },
                                    onNavChange = { viewModel.updateNavigation(it) },
                                    onResetTheme = { viewModel.resetThemeToDefault() },
                                    onExportJson = { viewModel.exportThemeJson() },
                                    onImportJson = { viewModel.importThemeJson(it) },
                                    onShowToast = { viewModel.showToast(it) }
                                )
                                "accounts" -> AccountsScreen(
                                    connections = connections,
                                    themeConfig = themeConfig,
                                    currentLanguage = currentLanguage,
                                    onToggleConnection = { viewModel.toggleProviderConnection(it) },
                                    onShowToast = { viewModel.showToast(it) }
                                )
                                "settings" -> SettingsScreen(
                                    currentLanguage = currentLanguage,
                                    themeConfig = themeConfig,
                                    sleepTimerMinutesLeft = sleepTimerMinutesLeft,
                                    equalizerPreset = equalizerPreset,
                                    onLanguageChange = { viewModel.setLanguage(it) },
                                    onEqualizerChange = { viewModel.playerEngine.setEqualizerPreset(it) },
                                    onSleepTimerChange = { viewModel.playerEngine.setSleepTimer(it) },
                                    onShowToast = { viewModel.showToast(it) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Animated Full Screen Now Playing Overlay
        AnimatedVisibility(
            visible = isFullScreenNowPlayingOpen,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(320)) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(280)) + fadeOut()
        ) {
            FullScreenNowPlaying(
                track = currentTrack,
                isPlaying = isPlaying,
                progressMs = currentPositionMs,
                durationMs = durationMs,
                repeatMode = repeatMode,
                isShuffle = isShuffle,
                themeConfig = themeConfig,
                sleepTimerMinutesLeft = sleepTimerMinutesLeft,
                onCloseClick = { viewModel.setFullScreenNowPlayingOpen(false) },
                onPlayPauseClick = { viewModel.playerEngine.togglePlayPause() },
                onSeek = { viewModel.playerEngine.seekTo(it) },
                onNextClick = { viewModel.playNextTrack() },
                onPrevClick = { viewModel.playPreviousTrack() },
                onToggleShuffle = { viewModel.playerEngine.toggleShuffle() },
                onCycleRepeat = { viewModel.playerEngine.cycleRepeatMode() },
                onOpenQueue = { viewModel.setQueueSheetOpen(true) },
                onOpenSleepTimer = { viewModel.playerEngine.setSleepTimer(if (sleepTimerMinutesLeft == null) 30 else null) }
            )
        }

        // Queue Bottom Sheet
        if (isQueueSheetOpen) {
            QueueSheet(
                queue = queue,
                currentTrack = currentTrack,
                isPlaying = isPlaying,
                accentColor = accentColor,
                onTrackSelect = {
                    viewModel.playTrack(it)
                    viewModel.setQueueSheetOpen(false)
                },
                onDismiss = { viewModel.setQueueSheetOpen(false) }
            )
        }
    }
}

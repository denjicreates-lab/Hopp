package com.example.model

enum class MusicProviderType(
    val displayName: String,
    val badgeColorHex: Long,
    val description: String
) {
    SPOTIFY("Spotify", 0xFF1DB954, "OAuth PKCE & Web Playback SDK"),
    APPLE_MUSIC("Apple Music", 0xFFFA243C, "MusicKit JS & Developer Token"),
    YOUTUBE("YouTube", 0xFFFF0000, "Official Embed Player & Data API"),
    JIOSAAVN("JioSaavn", 0xFF2BC5B4, "Playlist Link / Tracklist Import"),
    LOCAL("Local", 0xFF7C5CFF, "Device Storage Audio (MP3/FLAC/WAV)"),
    OPEN_MUSIC("Open Music", 0xFF2DE2C4, "Audius, Jamendo & Internet Archive")
}

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationMs: Long = 180000L,
    val coverUrl: String = "",
    val provider: MusicProviderType = MusicProviderType.LOCAL,
    val streamUrl: String = "",
    val isLossless: Boolean = false,
    val licenseInfo: String? = null,
    val lyrics: String? = null,
    val downloadFormats: List<String> = listOf("MP3 320kbps", "FLAC Lossless", "WAV")
)

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val provider: MusicProviderType,
    val trackCount: Int,
    val tracks: List<Track> = emptyList(),
    val isHoppCustom: Boolean = false,
    val isUserCreated: Boolean = false
)

data class ProviderConnection(
    val provider: MusicProviderType,
    val isConnected: Boolean,
    val accountName: String? = null,
    val accountEmail: String? = null,
    val requiresPremium: Boolean = false,
    val apiConfigured: Boolean = false,
    val lastSyncTime: String = "Just now"
)

enum class ThemePreset(val title: String) {
    MIDNIGHT("Midnight"),
    AURORA("Aurora"),
    SUNSET("Sunset"),
    MONO("Mono"),
    NEON("Neon"),
    LIGHT("Light")
}

enum class BackgroundStyle(val title: String) {
    SOLID("Solid Surface"),
    GRADIENT("Deep Gradient"),
    BLURRED_ART("Blurred Album Art"),
    ANIMATED_MESH("Neon Ambient Glow")
}

enum class PlayerLayout(val title: String) {
    CLASSIC("Classic Art"),
    LYRICS("Lyrics Focus"),
    MINIMAL("Sleek Minimal")
}

enum class AlbumArtShape(val title: String) {
    ROUNDED_SQUARE("Rounded Square"),
    CIRCLE("Modern Circle"),
    VINYL("Spinning Vinyl")
}

enum class NavigationType(val title: String) {
    BOTTOM_BAR("Bottom Tab Bar"),
    FLOATING_DOCK("Floating Dock"),
    SIDEBAR("Left Sidebar"),
    TOP_TABS("Top Tabs")
}

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "English (Default)"),
    HINGLISH("hinglish", "Hinglish"),
    HINDI("hi", "हिंदी (Hindi)")
}

data class ThemeConfig(
    val preset: ThemePreset = ThemePreset.MIDNIGHT,
    val accentColorHex: Long = 0xFFFF4D6D,
    val secondaryColorHex: Long = 0xFF7C5CFF,
    val tertiaryColorHex: Long = 0xFF2DE2C4,
    val backgroundStyle: BackgroundStyle = BackgroundStyle.GRADIENT,
    val cornerRadiusDp: Int = 20,
    val glassBlurIntensity: Float = 0.85f,
    val playerLayout: PlayerLayout = PlayerLayout.CLASSIC,
    val albumArtShape: AlbumArtShape = AlbumArtShape.ROUNDED_SQUARE
)

data class NavItemConfig(
    val id: String,
    val labelHinglish: String,
    val labelHindi: String,
    val labelEnglish: String,
    val isVisible: Boolean = true
)

data class NavigationConfig(
    val navType: NavigationType = NavigationType.BOTTOM_BAR,
    val isIconOnly: Boolean = false,
    val items: List<NavItemConfig> = listOf(
        NavItemConfig("home", "Aapke Liye", "आपके लिए", "For You"),
        NavItemConfig("library", "Library", "लाइब्रेरी", "Library"),
        NavItemConfig("search", "Khojo", "खोजें", "Search"),
        NavItemConfig("discover", "AI Mood", "एआई मूड", "AI Discover"),
        NavItemConfig("open_music", "Open Music", "ओपन म्यूजिक", "Open Music"),
        NavItemConfig("customize", "Customize", "कस्टमाइज़", "Customize")
    )
)

data class MoodSongSuggestion(
    val title: String,
    val artist: String,
    val language: String,
    val reason: String
)

data class SongRecognitionResult(
    val title: String = "",
    val artist: String = "",
    val confidence: Float = 0f,
    val notes: String = "",
    val isMatchFound: Boolean = false
)

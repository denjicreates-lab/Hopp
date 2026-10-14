package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class MusicRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("hopp_music_prefs", Context.MODE_PRIVATE)

    companion object {
        const val GITHUB_REPO_URL = "https://github.com/hopp-music/hopp-hub"
        const val LICENSE_NAME = "MIT License"
    }

    // Default streamable tracks (using open-licensed sample audio streams)
    val defaultTracks: List<Track> = listOf(
        Track(
            id = "t_spotify_1",
            title = "Kesariya (Acoustic)",
            artist = "Arijit Singh, Pritam",
            album = "Brahmastra Reimagined",
            durationMs = 210000L,
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.SPOTIFY,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            lyrics = "[00:10.00] Mujhko itna bataye koi\n[00:15.00] Kaise tujhse dil na lagaye koi\n[00:22.00] Rabba ne tujhko banane me\n[00:27.00] Kardi hai husn ki khaali tijoriyan\n[00:35.00] Kajre ki siyahi se likhi\n[00:39.00] Hai tune jane kitno ki love storiyan\n[00:46.00] Kesariya tera ishq hai piya\n[00:52.00] Rang jaaun jo main haath lagaun"
        ),
        Track(
            id = "t_apple_1",
            title = "Starboy (Lossless Atmos)",
            artist = "The Weeknd ft. Daft Punk",
            album = "Starboy",
            durationMs = 230000L,
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.APPLE_MUSIC,
            isLossless = true,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            lyrics = "[00:08.00] I'm tryna put you in the worst mood, ah\n[00:13.00] P1 cleaner than your church shoes, ah\n[00:18.00] Milli point two just to hurt you, ah\n[00:22.00] All red Lamb' just to tease you, ah\n[00:26.00] Look what you've done\n[00:29.00] I'm a motherf***in' starboy"
        ),
        Track(
            id = "t_yt_1",
            title = "Mi Amor (Live Performance)",
            artist = "Sharn, 408 Darwin",
            album = "Single",
            durationMs = 195000L,
            coverUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.YOUTUBE,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            lyrics = "[00:12.00] Teriyan akhiyan vich doob jaan nu jee karda\n[00:18.00] Dil mera bas tere te marda\n[00:25.00] Tu hi meri zind tu hi meri jaan\n[00:30.00] Mi amor, sun le mera haal"
        ),
        Track(
            id = "t_jio_1",
            title = "Pasoori Nu",
            artist = "Arijit Singh, Tulsi Kumar",
            album = "Satyaprem Ki Katha",
            durationMs = 215000L,
            coverUrl = "https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.JIOSAAVN,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            lyrics = "[00:14.00] Agg laavan aavan jaavan nu\n[00:18.00] Teri doli leke aavan nu\n[00:22.00] Dil darda ae kehnda ae\n[00:27.00] Tainu khona nahi chahunda ae"
        ),
        Track(
            id = "t_open_1",
            title = "Midnight Odyssey (FLAC Studio)",
            artist = "Etheric Echoes",
            album = "Open Soundscapes Vol. 4",
            durationMs = 240000L,
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.OPEN_MUSIC,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            isLossless = true,
            licenseInfo = "Creative Commons CC-BY 4.0 (Audius Open Audio)",
            downloadFormats = listOf("FLAC 24-bit/96kHz (84MB)", "WAV Uncompressed (42MB)", "MP3 320kbps (9MB)")
        ),
        Track(
            id = "t_open_2",
            title = "Cyber Dreamer",
            artist = "Neon Skyline",
            album = "Synthwave Jamendo Vault",
            durationMs = 205000L,
            coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.OPEN_MUSIC,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            isLossless = true,
            licenseInfo = "CC-BY-NC-SA 3.0 (Jamendo Music License)",
            downloadFormats = listOf("FLAC Lossless", "MP3 320kbps", "OGG Vorbis")
        ),
        Track(
            id = "t_spotify_2",
            title = "Tu Hai Kahan",
            artist = "AUR (Ahad, Usama, Raffey)",
            album = "Independent Release",
            durationMs = 245000L,
            coverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.SPOTIFY,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            lyrics = "[00:15.00] Socha na tha tune mujhko chhod diya aise\n[00:22.00] Jaise koi khilona ho toota jaise\n[00:30.00] Tu hai kahan mere rehnuma\n[00:38.00] Dil yeh mera tujhko dhoondta"
        ),
        Track(
            id = "t_apple_2",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "After Hours",
            durationMs = 200000L,
            coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.APPLE_MUSIC,
            isLossless = true,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3"
        ),
        Track(
            id = "t_local_1",
            title = "Desi HipHop Freestyle (Local Demo)",
            artist = "Local Artist",
            album = "Phone Storage / Music",
            durationMs = 175000L,
            coverUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=500&auto=format&fit=crop&q=80",
            provider = MusicProviderType.LOCAL,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
            isLossless = true
        )
    )

    fun getInitialPlaylists(): List<Playlist> {
        val crossPlatformTracks = defaultTracks.take(6)
        val spotifyTracks = defaultTracks.filter { it.provider == MusicProviderType.SPOTIFY }
        val appleTracks = defaultTracks.filter { it.provider == MusicProviderType.APPLE_MUSIC }
        val youtubeTracks = defaultTracks.filter { it.provider == MusicProviderType.YOUTUBE }
        val jioTracks = defaultTracks.filter { it.provider == MusicProviderType.JIOSAAVN }
        val openTracks = defaultTracks.filter { it.provider == MusicProviderType.OPEN_MUSIC }

        return listOf(
            Playlist(
                id = "p_hopp_fusion",
                title = "Hopp Fusion: Cross-Platform Mix",
                description = "Mixed playlist seamlessly combining Spotify, Apple Music, YouTube & JioSaavn tracks in one unified queue.",
                coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500&auto=format&fit=crop&q=80",
                provider = MusicProviderType.SPOTIFY,
                trackCount = crossPlatformTracks.size,
                tracks = crossPlatformTracks,
                isHoppCustom = true
            ),
            Playlist(
                id = "p_spotify_chill",
                title = "Bollywood Sukoon & Indie",
                description = "Relaxing Indian Indie, acoustic Bollywood & soulful melodies synced from Spotify.",
                coverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=500&auto=format&fit=crop&q=80",
                provider = MusicProviderType.SPOTIFY,
                trackCount = spotifyTracks.size,
                tracks = spotifyTracks
            ),
            Playlist(
                id = "p_apple_spatial",
                title = "Apple Music Lossless Selects",
                description = "Master audio quality with high-fidelity dynamics from Apple Music catalog.",
                coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&auto=format&fit=crop&q=80",
                provider = MusicProviderType.APPLE_MUSIC,
                trackCount = appleTracks.size,
                tracks = appleTracks
            ),
            Playlist(
                id = "p_yt_punjabi",
                title = "Trending YouTube Waves",
                description = "Viral trending audio & live acoustic sessions synced from YouTube Music.",
                coverUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500&auto=format&fit=crop&q=80",
                provider = MusicProviderType.YOUTUBE,
                trackCount = youtubeTracks.size,
                tracks = youtubeTracks
            ),
            Playlist(
                id = "p_jiosaavn_imported",
                title = "JioSaavn Weekly Desi Hits",
                description = "Imported tracklist matching your favorite regional playlists.",
                coverUrl = "https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=500&auto=format&fit=crop&q=80",
                provider = MusicProviderType.JIOSAAVN,
                trackCount = jioTracks.size,
                tracks = jioTracks
            ),
            Playlist(
                id = "p_open_vault",
                title = "Audius & Jamendo Lossless Vault",
                description = "100% open-licensed music with free FLAC/WAV direct downloads.",
                coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500&auto=format&fit=crop&q=80",
                provider = MusicProviderType.OPEN_MUSIC,
                trackCount = openTracks.size,
                tracks = openTracks
            )
        )
    }

    fun getInitialConnections(): List<ProviderConnection> {
        return listOf(
            ProviderConnection(
                provider = MusicProviderType.SPOTIFY,
                isConnected = prefs.getBoolean("conn_spotify", true),
                accountName = prefs.getString("user_spotify", "Aman Sharma (Spotify)") ?: "Aman Sharma",
                accountEmail = "aman.hopp@spotify.me",
                requiresPremium = true,
                apiConfigured = true
            ),
            ProviderConnection(
                provider = MusicProviderType.APPLE_MUSIC,
                isConnected = prefs.getBoolean("conn_apple", true),
                accountName = prefs.getString("user_apple", "aman_hopp (Apple ID)") ?: "aman_hopp",
                accountEmail = "aman@icloud.com",
                requiresPremium = true,
                apiConfigured = true
            ),
            ProviderConnection(
                provider = MusicProviderType.YOUTUBE,
                isConnected = prefs.getBoolean("conn_yt", true),
                accountName = prefs.getString("user_yt", "Aman YouTube Music") ?: "Aman YouTube Music",
                accountEmail = "aman.yt@gmail.com",
                requiresPremium = false,
                apiConfigured = true
            ),
            ProviderConnection(
                provider = MusicProviderType.JIOSAAVN,
                isConnected = prefs.getBoolean("conn_jio", false),
                accountName = prefs.getString("user_jio", null),
                accountEmail = null,
                requiresPremium = false,
                apiConfigured = false
            )
        )
    }

    fun setConnection(provider: MusicProviderType, connected: Boolean, accountName: String? = null) {
        val key = when (provider) {
            MusicProviderType.SPOTIFY -> "conn_spotify"
            MusicProviderType.APPLE_MUSIC -> "conn_apple"
            MusicProviderType.YOUTUBE -> "conn_yt"
            MusicProviderType.JIOSAAVN -> "conn_jio"
            else -> return
        }
        prefs.edit().putBoolean(key, connected).apply()
        if (accountName != null) {
            val userKey = when (provider) {
                MusicProviderType.SPOTIFY -> "user_spotify"
                MusicProviderType.APPLE_MUSIC -> "user_apple"
                MusicProviderType.YOUTUBE -> "user_yt"
                MusicProviderType.JIOSAAVN -> "user_jio"
                else -> return
            }
            prefs.edit().putString(userKey, accountName).apply()
        }
    }

    fun getSavedLanguage(): AppLanguage {
        val code = prefs.getString("app_language", AppLanguage.ENGLISH.code)
        return AppLanguage.values().firstOrNull { it.code == code } ?: AppLanguage.ENGLISH
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit().putString("app_language", language.code).apply()
    }

    fun getSavedTheme(): ThemeConfig {
        val presetName = prefs.getString("theme_preset", ThemePreset.MIDNIGHT.name)
        val preset = try {
            ThemePreset.valueOf(presetName ?: ThemePreset.MIDNIGHT.name)
        } catch (e: Exception) {
            ThemePreset.MIDNIGHT
        }
        val accentHex = prefs.getLong("theme_accent", 0xFFFF4D6D)
        val bgStyleName = prefs.getString("theme_bg_style", BackgroundStyle.GRADIENT.name)
        val bgStyle = try {
            BackgroundStyle.valueOf(bgStyleName ?: BackgroundStyle.GRADIENT.name)
        } catch (e: Exception) {
            BackgroundStyle.GRADIENT
        }
        val cornerRadius = prefs.getInt("theme_corner_radius", 20)
        val blur = prefs.getFloat("theme_glass_blur", 0.85f)
        val playerLayoutName = prefs.getString("theme_player_layout", PlayerLayout.CLASSIC.name)
        val playerLayout = try {
            PlayerLayout.valueOf(playerLayoutName ?: PlayerLayout.CLASSIC.name)
        } catch (e: Exception) {
            PlayerLayout.CLASSIC
        }
        val artShapeName = prefs.getString("theme_art_shape", AlbumArtShape.ROUNDED_SQUARE.name)
        val artShape = try {
            AlbumArtShape.valueOf(artShapeName ?: AlbumArtShape.ROUNDED_SQUARE.name)
        } catch (e: Exception) {
            AlbumArtShape.ROUNDED_SQUARE
        }

        return ThemeConfig(
            preset = preset,
            accentColorHex = accentHex,
            backgroundStyle = bgStyle,
            cornerRadiusDp = cornerRadius,
            glassBlurIntensity = blur,
            playerLayout = playerLayout,
            albumArtShape = artShape
        )
    }

    fun saveTheme(config: ThemeConfig) {
        prefs.edit()
            .putString("theme_preset", config.preset.name)
            .putLong("theme_accent", config.accentColorHex)
            .putString("theme_bg_style", config.backgroundStyle.name)
            .putInt("theme_corner_radius", config.cornerRadiusDp)
            .putFloat("theme_glass_blur", config.glassBlurIntensity)
            .putString("theme_player_layout", config.playerLayout.name)
            .putString("theme_art_shape", config.albumArtShape.name)
            .apply()
    }

    fun getSavedNavigation(): NavigationConfig {
        val navTypeName = prefs.getString("nav_type", NavigationType.BOTTOM_BAR.name)
        val navType = try {
            NavigationType.valueOf(navTypeName ?: NavigationType.BOTTOM_BAR.name)
        } catch (e: Exception) {
            NavigationType.BOTTOM_BAR
        }
        val isIconOnly = prefs.getBoolean("nav_icon_only", false)
        return NavigationConfig(
            navType = navType,
            isIconOnly = isIconOnly
        )
    }

    fun saveNavigation(config: NavigationConfig) {
        prefs.edit()
            .putString("nav_type", config.navType.name)
            .putBoolean("nav_icon_only", config.isIconOnly)
            .apply()
    }

    fun exportThemeToJson(config: ThemeConfig): String {
        val json = JSONObject()
        json.put("preset", config.preset.name)
        json.put("accentColorHex", config.accentColorHex)
        json.put("backgroundStyle", config.backgroundStyle.name)
        json.put("cornerRadiusDp", config.cornerRadiusDp)
        json.put("glassBlurIntensity", config.glassBlurIntensity.toDouble())
        json.put("playerLayout", config.playerLayout.name)
        json.put("albumArtShape", config.albumArtShape.name)
        return json.toString(2)
    }

    fun importThemeFromJson(jsonString: String): ThemeConfig? {
        return try {
            val json = JSONObject(jsonString)
            ThemeConfig(
                preset = ThemePreset.valueOf(json.optString("preset", ThemePreset.MIDNIGHT.name)),
                accentColorHex = json.optLong("accentColorHex", 0xFFFF4D6D),
                backgroundStyle = BackgroundStyle.valueOf(json.optString("backgroundStyle", BackgroundStyle.GRADIENT.name)),
                cornerRadiusDp = json.optInt("cornerRadiusDp", 20),
                glassBlurIntensity = json.optDouble("glassBlurIntensity", 0.85).toFloat(),
                playerLayout = PlayerLayout.valueOf(json.optString("playerLayout", PlayerLayout.CLASSIC.name)),
                albumArtShape = AlbumArtShape.valueOf(json.optString("albumArtShape", AlbumArtShape.ROUNDED_SQUARE.name))
            )
        } catch (e: Exception) {
            null
        }
    }
}

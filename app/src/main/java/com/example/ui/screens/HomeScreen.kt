package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.HoppingEqualizer
import com.example.ui.components.PlatformBadge
import java.util.Calendar

@Composable
fun HomeScreen(
    currentLanguage: AppLanguage,
    currentTrack: Track?,
    isPlaying: Boolean,
    recentlyPlayed: List<Track>,
    playlists: List<Playlist>,
    themeConfig: ThemeConfig,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onMoodChipClick: (String) -> Unit,
    onQuickListenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(themeConfig.accentColorHex)
    val secondaryColor = Color(themeConfig.secondaryColorHex)
    val cornerRadius = themeConfig.cornerRadiusDp.dp

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> when (currentLanguage) {
            AppLanguage.HINDI -> "शुभ प्रभात"
            AppLanguage.HINGLISH -> "Subah Bakhair"
            AppLanguage.ENGLISH -> "Good Morning"
        }
        hour < 17 -> when (currentLanguage) {
            AppLanguage.HINDI -> "नमस्ते"
            AppLanguage.HINGLISH -> "Namaste"
            AppLanguage.ENGLISH -> "Good Afternoon"
        }
        hour < 21 -> when (currentLanguage) {
            AppLanguage.HINDI -> "शुभ संध्या"
            AppLanguage.HINGLISH -> "Shubh Sandhya"
            AppLanguage.ENGLISH -> "Good Evening"
        }
        else -> when (currentLanguage) {
            AppLanguage.HINDI -> "शुभ रात्रि"
            AppLanguage.HINGLISH -> "Late Night Sukoon"
            AppLanguage.ENGLISH -> "Late Night Vibes"
        }
    }

    val moodChips = when (currentLanguage) {
        AppLanguage.ENGLISH -> listOf(
            "Rainy day soothing 🌧️",
            "Late night drive 🚗",
            "High-energy workout 💪",
            "Heartfelt acoustic 💔",
            "Coffee & nostalgic 90s ☕",
            "Party & electronic 🎉",
            "Deep focus & coding 💻"
        )
        AppLanguage.HINDI -> listOf(
            "बारिश और सुकून 🌧️",
            "देर रात की ड्राइव 🚗",
            "जिम और हाई एनर्जी 💪",
            "दिल को छूने वाले गाने 💔",
            "चाय और 90 के दशक ☕",
            "पार्टी और जश्न 🎉",
            "गहरा ध्यान व कोडिंग 💻"
        )
        AppLanguage.HINGLISH -> listOf(
            "Barish ho rahi hai 🌧️",
            "Late night long drive 🚗",
            "Gym heavy workout 💪",
            "Dil toota & sukoon 💔",
            "Chai aur purane 90s ☕",
            "Party & high energy 🎉",
            "Deep focus coding 💻"
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Greeting Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.HINDI -> "आपके सभी पसंदीदा गाने एक जगह"
                            AppLanguage.HINGLISH -> "Aapki marzi ka all-in-one player"
                            AppLanguage.ENGLISH -> "Your unified music sanctuary"
                        },
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }

                // Quick Pehchano Mic Button
                Button(
                    onClick = onQuickListenClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("home_quick_listen_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Hearing,
                        contentDescription = "Identify Song",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.HINDI -> "पहचानो"
                            AppLanguage.HINGLISH -> "Pehchano"
                            AppLanguage.ENGLISH -> "Listen"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
        }

        // Continue Listening Card (Hero)
        if (currentTrack != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(cornerRadius))
                        .clickable { onTrackClick(currentTrack) }
                        .testTag("continue_listening_card"),
                    shape = RoundedCornerShape(cornerRadius),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E2C)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF221636),
                                        Color(0xFF161522)
                                    )
                                )
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(cornerRadius / 2))
                                .background(Color(0xFF2B2040)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = currentTrack.coverUrl,
                                contentDescription = currentTrack.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "CONTINUE LISTENING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = accentColor
                                )
                                PlatformBadge(provider = currentTrack.provider, showLabel = false)
                            }
                            Text(
                                text = currentTrack.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentTrack.artist,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Mood Chips Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.HINDI -> "मूड चुनें (एआई सजेशन्स)"
                        AppLanguage.HINGLISH -> "Mood batao — AI se gaane"
                        AppLanguage.ENGLISH -> "Pick a Mood (Gemini AI)"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(moodChips) { chipText ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            modifier = Modifier
                                .clickable { onMoodChipClick(chipText) }
                                .testTag("mood_chip_$chipText")
                        ) {
                            Text(
                                text = chipText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Recently Played Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.HINDI -> "हाल ही में बजाया गया"
                        AppLanguage.HINGLISH -> "Haal hi me bajaya gaya"
                        AppLanguage.ENGLISH -> "Recently Played"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentlyPlayed) { track ->
                        Column(
                            modifier = Modifier
                                .width(120.dp)
                                .clickable { onTrackClick(track) },
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(cornerRadius / 2))
                                    .background(Color(0xFF222230))
                            ) {
                                AsyncImage(
                                    model = track.coverUrl,
                                    contentDescription = track.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                ) {
                                    PlatformBadge(provider = track.provider, showLabel = false)
                                }
                            }
                            Text(
                                text = track.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = track.artist,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.65f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Unified Playlists Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.HINDI -> "सारे प्लेटफॉर्म्स की प्लेलिस्ट्स"
                            AppLanguage.HINGLISH -> "Unified Playlists"
                            AppLanguage.ENGLISH -> "Connected Playlists"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                playlists.forEach { playlist ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(cornerRadius))
                            .clickable { onPlaylistClick(playlist) }
                            .testTag("playlist_card_${playlist.id}"),
                        shape = RoundedCornerShape(cornerRadius),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(cornerRadius / 2))
                                    .background(Color(0xFF20202E))
                            ) {
                                AsyncImage(
                                    model = playlist.coverUrl,
                                    contentDescription = playlist.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = playlist.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (playlist.isHoppCustom) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(accentColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text("HOPP MIX", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = accentColor)
                                        }
                                    }
                                }

                                Text(
                                    text = playlist.description,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.65f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    PlatformBadge(provider = playlist.provider, showLabel = true)
                                    Text(
                                        text = "• ${playlist.trackCount} tracks",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            IconButton(onClick = { onPlaylistClick(playlist) }) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Play Playlist",
                                    tint = accentColor,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

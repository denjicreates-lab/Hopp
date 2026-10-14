package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.player.RepeatMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenNowPlaying(
    track: Track?,
    isPlaying: Boolean,
    progressMs: Long,
    durationMs: Long,
    repeatMode: RepeatMode,
    isShuffle: Boolean,
    themeConfig: ThemeConfig,
    sleepTimerMinutesLeft: Int?,
    onCloseClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (track == null) return

    val accentColor = Color(themeConfig.accentColorHex)
    val secondaryColor = Color(themeConfig.secondaryColorHex)

    // Vinyl spinning rotation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val vinylRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    val currentRotation = if (isPlaying && themeConfig.albumArtShape == AlbumArtShape.VINYL) vinylRotation else 0f

    var showLyricsView by remember { mutableStateOf(themeConfig.playerLayout == PlayerLayout.LYRICS) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1B142A),
                        Color(0xFF0F0B18),
                        Color(0xFF07050A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("full_screen_now_playing")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Dismiss button, title, sleep timer & queue icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.testTag("now_playing_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NOW PLAYING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = track.album.ifEmpty { "Hopp Music Hub" },
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenSleepTimer,
                        modifier = Modifier.testTag("sleep_timer_button")
                    ) {
                        BadgedBox(badge = {
                            if (sleepTimerMinutesLeft != null) {
                                Badge(containerColor = accentColor) {
                                    Text("${sleepTimerMinutesLeft}m")
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Sleep Timer",
                                tint = if (sleepTimerMinutesLeft != null) accentColor else Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenQueue,
                        modifier = Modifier.testTag("now_playing_queue_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = "Queue",
                            tint = Color.White
                        )
                    }
                }
            }

            // Middle Content: Artwork / Vinyl / Lyrics based on chosen layout & shape
            if (showLyricsView) {
                // Lyrics View
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(themeConfig.cornerRadiusDp.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lyrics,
                                contentDescription = null,
                                tint = accentColor
                            )
                            Text(
                                text = "Lyrics (Sync)",
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = track.lyrics ?: "Lyrics available for this track.\nEnjoy the melody and rhythm on Hopp!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 16.sp,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Album Art / Vinyl Presentation
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (themeConfig.albumArtShape) {
                        AlbumArtShape.VINYL -> {
                            // Spinning vinyl with circular album art center
                            Box(
                                modifier = Modifier
                                    .size(270.dp)
                                    .rotate(currentRotation)
                                    .clip(CircleShape)
                                    .background(Color(0xFF111116))
                                    .border(4.dp, Color(0xFF22222E), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                // Grooved circles
                                Box(
                                    modifier = Modifier
                                        .size(230.dp)
                                        .border(1.dp, Color(0xFF2E2E3E), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(190.dp)
                                        .border(1.dp, Color(0xFF252535), CircleShape)
                                )
                                // Center artwork sticker
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .background(accentColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (track.coverUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = track.coverUrl,
                                            contentDescription = track.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    // Spindle hole
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0F0B18))
                                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                    )
                                }
                            }
                        }
                        AlbumArtShape.CIRCLE -> {
                            Box(
                                modifier = Modifier
                                    .size(260.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, accentColor.copy(alpha = 0.4f), CircleShape)
                                    .background(Color(0xFF201B2E)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = track.coverUrl,
                                    contentDescription = track.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        AlbumArtShape.ROUNDED_SQUARE -> {
                            Card(
                                modifier = Modifier
                                    .size(280.dp)
                                    .border(1.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(themeConfig.cornerRadiusDp.dp)),
                                shape = RoundedCornerShape(themeConfig.cornerRadiusDp.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                            ) {
                                AsyncImage(
                                    model = track.coverUrl,
                                    contentDescription = track.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Track metadata & badges
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = track.artist,
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlatformBadge(provider = track.provider, showLabel = true)

                        if (track.isLossless) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2DE2C4).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LOSSLESS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2DE2C4)
                                )
                            }
                        }

                        IconButton(onClick = { showLyricsView = !showLyricsView }) {
                            Icon(
                                imageVector = Icons.Default.Lyrics,
                                contentDescription = "Toggle Lyrics",
                                tint = if (showLyricsView) accentColor else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Seekbar & Times
            Column(modifier = Modifier.fillMaxWidth()) {
                val currentSliderValue = (progressMs.toFloat() / durationMs.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)

                Slider(
                    value = currentSliderValue,
                    onValueChange = { frac ->
                        onSeek((frac * durationMs).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("now_playing_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(progressMs),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "-${formatTime((durationMs - progressMs).coerceAtLeast(0L))}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Playback Controls: Shuffle, Prev, Play/Pause (Big Bouncing Button), Next, Repeat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Button
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier.testTag("now_playing_shuffle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) accentColor else Color.White.copy(alpha = 0.6f)
                    )
                }

                // Previous Button
                IconButton(
                    onClick = onPrevClick,
                    modifier = Modifier.size(48.dp).testTag("now_playing_prev")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play / Pause Major Bouncing Action Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(accentColor, secondaryColor)
                            )
                        )
                        .clickable { onPlayPauseClick() }
                        .testTag("now_playing_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Next Button
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(48.dp).testTag("now_playing_next")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat Button
                IconButton(
                    onClick = onCycleRepeat,
                    modifier = Modifier.testTag("now_playing_repeat")
                ) {
                    val icon = when (repeatMode) {
                        RepeatMode.ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    }
                    val tint = when (repeatMode) {
                        RepeatMode.OFF -> Color.White.copy(alpha = 0.6f)
                        else -> accentColor
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Repeat",
                        tint = tint
                    )
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

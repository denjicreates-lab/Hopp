package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.PlatformBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenMusicScreen(
    openTracks: List<Track>,
    localTracks: List<Track>,
    themeConfig: ThemeConfig,
    currentLanguage: AppLanguage,
    onTrackClick: (Track) -> Unit,
    onAddLocalTrack: (String, String, String) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(themeConfig.accentColorHex)
    val secondaryColor = Color(themeConfig.secondaryColorHex)
    val cornerRadius = themeConfig.cornerRadiusDp.dp

    var showDownloadDialogForTrack by remember { mutableStateOf<Track?>(null) }

    // System File Picker for local device audio files (MP3, WAV, FLAC, M4A)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            val path = uri.lastPathSegment ?: "Local Track"
            val cleanName = path.substringAfterLast("/").substringBeforeLast(".")
            onAddLocalTrack(cleanName, "Device Storage Audio", uri.toString())
        }
        if (uris.isNotEmpty()) {
            onShowToast("Added ${uris.size} local audio files")
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = "Open Music & Local Hub",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Stream and download open-licensed FLAC/WAV audio & play device files",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }

        // Policy & Compliance Notice Banner (Mandatory as per prompt & Play Store rules)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1728))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Policy Notice",
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Streaming services (Spotify, Apple Music, YouTube, JioSaavn) do not permit offline file downloads. Hopp provides downloads exclusively for open-licensed and device-stored local audio."
                            AppLanguage.HINDI -> "स्ट्रीमिंग प्लेटफॉर्म्स (Spotify, Apple, YouTube, JioSaavn) के गाने डाउनलोड नहीं किए जा सकते; हॉप केवल ओपन-लाइसेंस्ड और लोकल म्यूजिक डाउनलोड सपोर्ट करता है।"
                            AppLanguage.HINGLISH -> "Streaming platforms (Spotify, Apple, YouTube, JioSaavn) ke gaane download nahi ho sakte; Hopp sirf open-licensed aur local music download support karta hai."
                        },
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Local Device Audio Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Local Device Library",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${localTracks.size} files imported from storage",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        Button(
                            onClick = {
                                filePickerLauncher.launch(arrayOf("audio/*"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("pick_local_audio_button")
                        ) {
                            Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Files", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (localTracks.isEmpty()) {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Add MP3, FLAC, or WAV files directly from your device storage to enjoy offline playback with zero latency."
                                AppLanguage.HINDI -> "बिना इंटरनेट के सुनने के लिए अपने डिवाइस से MP3, FLAC, या WAV फाइलें जोड़ें।"
                                AppLanguage.HINGLISH -> "Aapke device me pade MP3, FLAC, WAV gaano ko yahan add karke bina internet ke suniye."
                            },
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    } else {
                        localTracks.forEach { track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.04f))
                                    .clickable { onTrackClick(track) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = secondaryColor)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = track.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                    Text(text = track.artist, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                }
                                IconButton(onClick = { onTrackClick(track) }) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = secondaryColor)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Open-Licensed Music Section (Audius, Jamendo, Internet Archive)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Open Lossless Catalog",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Audius, Jamendo & Creative Commons Master Recordings",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        items(openTracks) { track ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(cornerRadius)),
                shape = RoundedCornerShape(cornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(cornerRadius / 2))
                                .background(Color(0xFF222232))
                        ) {
                            AsyncImage(
                                model = track.coverUrl,
                                contentDescription = track.title,
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
                                    text = track.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                PlatformBadge(provider = track.provider, showLabel = false)
                            }
                            Text(
                                text = track.artist,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (track.licenseInfo != null) {
                                Text(
                                    text = track.licenseInfo,
                                    fontSize = 10.sp,
                                    color = Color(0xFF2DE2C4),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onTrackClick(track) }) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = accentColor)
                            }

                            IconButton(
                                onClick = { showDownloadDialogForTrack = track },
                                modifier = Modifier.testTag("download_track_${track.id}")
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = "Download Formats", tint = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
        }
    }

    // Download format picker dialog
    if (showDownloadDialogForTrack != null) {
        val track = showDownloadDialogForTrack!!
        AlertDialog(
            onDismissRequest = { showDownloadDialogForTrack = null },
            title = {
                Text(
                    text = "Download: ${track.title}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Open License: ${track.licenseInfo ?: "Creative Commons"}",
                        fontSize = 12.sp,
                        color = Color(0xFF2DE2C4)
                    )
                    Text(
                        text = "Select high-fidelity audio format:",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    track.downloadFormats.forEach { format ->
                        Button(
                            onClick = {
                                onShowToast("Downloading $format for ${track.title}...")
                                showDownloadDialogForTrack = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = format, color = Color.White, fontSize = 13.sp)
                                Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDownloadDialogForTrack = null }) {
                    Text("Close", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }
}

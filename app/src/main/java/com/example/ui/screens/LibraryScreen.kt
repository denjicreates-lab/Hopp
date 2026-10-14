package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
fun LibraryScreen(
    playlists: List<Playlist>,
    selectedFilter: MusicProviderType?,
    themeConfig: ThemeConfig,
    currentLanguage: AppLanguage,
    onFilterChange: (MusicProviderType?) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCreateHoppPlaylist: (String, String) -> Unit,
    onImportPlaylist: (String, String, MusicProviderType) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(themeConfig.accentColorHex)
    val cornerRadius = themeConfig.cornerRadiusDp.dp

    var showCreateDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    var newPlaylistTitle by remember { mutableStateOf("") }
    var newPlaylistDesc by remember { mutableStateOf("") }

    var importTitle by remember { mutableStateOf("") }
    var importContent by remember { mutableStateOf("") }
    var importProvider by remember { mutableStateOf(MusicProviderType.JIOSAAVN) }

    val filteredPlaylists = remember(playlists, selectedFilter) {
        if (selectedFilter == null) playlists
        else playlists.filter { it.provider == selectedFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            // Header with action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.HINDI -> "आपकी लाइब्रेरी"
                            AppLanguage.HINGLISH -> "Aapki Library"
                            AppLanguage.ENGLISH -> "Unified Library"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "${filteredPlaylists.size} playlists across connected platforms",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .testTag("create_hopp_playlist_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Create Playlist", tint = accentColor)
                    }

                    IconButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("import_playlist_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Import Playlist", tint = Color.White)
                    }
                }
            }
        }

        // Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { onFilterChange(null) },
                        label = { Text("All", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.06f),
                            labelColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("filter_all")
                    )
                }

                items(MusicProviderType.values()) { provider ->
                    FilterChip(
                        selected = selectedFilter == provider,
                        onClick = { onFilterChange(if (selectedFilter == provider) null else provider) },
                        label = { Text(provider.displayName) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(provider.badgeColorHex))
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(provider.badgeColorHex),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.06f),
                            labelColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("filter_${provider.name}")
                    )
                }
            }
        }

        // Playlists List
        if (filteredPlaylists.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    shape = RoundedCornerShape(cornerRadius),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.MusicOff, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "No playlists found"
                                AppLanguage.HINDI -> "कोई प्लेलिस्ट नहीं मिली"
                                AppLanguage.HINGLISH -> "Abhi koi playlist nahi mili"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Connect a streaming service or create a unified Hopp playlist"
                                AppLanguage.HINDI -> "अकाउंट कनेक्ट करें या नई हॉप प्लेलिस्ट बनाएं"
                                AppLanguage.HINGLISH -> "Account connect karo ya nayi Hopp playlist banao"
                            },
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(filteredPlaylists) { playlist ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(cornerRadius))
                        .clickable { onPlaylistClick(playlist) }
                        .testTag("library_item_${playlist.id}"),
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
                                .size(60.dp)
                                .clip(RoundedCornerShape(cornerRadius / 2))
                                .background(Color(0xFF222230))
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
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (playlist.isHoppCustom) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(accentColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("HOPP", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = accentColor)
                                    }
                                }
                            }

                            Text(
                                text = playlist.description,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f),
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
                                    text = "• ${playlist.trackCount} songs",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        IconButton(onClick = { onPlaylistClick(playlist) }) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Play",
                                tint = accentColor,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Create Hopp Playlist Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Create Unified Playlist"
                        AppLanguage.HINDI -> "नई हॉप प्लेलिस्ट बनाएं"
                        AppLanguage.HINGLISH -> "Nayi Hopp Playlist Banao"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "A cross-platform mix seamlessly blending tracks from Spotify, Apple Music, YouTube & JioSaavn."
                            AppLanguage.HINDI -> "क्रॉस-प्लेटफ़ॉर्म मिक्स जिसमें सभी सेवाओं के गाने एक साथ चलेंगे।"
                            AppLanguage.HINGLISH -> "Cross-platform mix jisme Spotify, Apple Music, YouTube aur JioSaavn ke gaane saath chalenge."
                        },
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    OutlinedTextField(
                        value = newPlaylistTitle,
                        onValueChange = { newPlaylistTitle = it },
                        label = { Text("Playlist Title") },
                        placeholder = { Text("e.g. Acoustic Journey") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("new_playlist_title_input")
                    )
                    OutlinedTextField(
                        value = newPlaylistDesc,
                        onValueChange = { newPlaylistDesc = it },
                        label = { Text("Description") },
                        placeholder = { Text("My cross-platform favorites") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistTitle.isNotBlank()) {
                            onCreateHoppPlaylist(newPlaylistTitle, newPlaylistDesc)
                            newPlaylistTitle = ""
                            newPlaylistDesc = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(if (currentLanguage == AppLanguage.ENGLISH) "Create" else "Banao")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text(if (currentLanguage == AppLanguage.ENGLISH) "Cancel" else "Radd Karo", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }

    // Import Playlist Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Import Playlist / Tracklist"
                        AppLanguage.HINDI -> "प्लेलिस्ट इंपोर्ट करें"
                        AppLanguage.HINGLISH -> "Import Playlist / Tracklist"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Paste a share link or clean tracklist. Hopp imports the songs seamlessly using zero unofficial endpoints."
                            AppLanguage.HINDI -> "शेयर लिंक या गानों की सूची पेस्ट करें। बिना किसी अनधिकृत स्क्रैपिंग के हॉप गानों को इंपोर्ट करता है।"
                            AppLanguage.HINGLISH -> "JioSaavn ya external playlist ka link ya tracklist paste karein. Hopp bina unofficial scraping ke import karta hai."
                        },
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    OutlinedTextField(
                        value = importTitle,
                        onValueChange = { importTitle = it },
                        label = { Text("Playlist Name") },
                        placeholder = { Text("e.g. JioSaavn Favorites") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = importContent,
                        onValueChange = { importContent = it },
                        label = { Text("Tracklist or Link (one per line)") },
                        placeholder = { Text("Kesariya - Arijit Singh\nStarboy - The Weeknd\nPasoori - Ali Sethi") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importContent.isNotBlank()) {
                            onImportPlaylist(importTitle, importContent, importProvider)
                            importTitle = ""
                            importContent = ""
                            showImportDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(if (currentLanguage == AppLanguage.ENGLISH) "Import" else "Import Karo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text(if (currentLanguage == AppLanguage.ENGLISH) "Cancel" else "Radd Karo", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }
}

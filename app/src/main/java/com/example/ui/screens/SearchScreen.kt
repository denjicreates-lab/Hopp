package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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

@Composable
fun SearchScreen(
    searchQuery: String,
    allTracks: List<Track>,
    themeConfig: ThemeConfig,
    currentLanguage: AppLanguage,
    onQueryChange: (String) -> Unit,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(themeConfig.accentColorHex)
    val cornerRadius = themeConfig.cornerRadiusDp.dp

    var selectedProviderFilter by remember { mutableStateOf<MusicProviderType?>(null) }

    val recentSearches = listOf("Arijit Singh", "The Weeknd", "AP Dhillon", "Acoustic Indie", "Lofi Beats")

    val searchResults = remember(searchQuery, allTracks, selectedProviderFilter) {
        if (searchQuery.isBlank()) {
            if (selectedProviderFilter != null) allTracks.filter { it.provider == selectedProviderFilter }
            else emptyList()
        } else {
            val q = searchQuery.trim().lowercase()
            allTracks.filter { track ->
                val matchesText = track.title.lowercase().contains(q) ||
                        track.artist.lowercase().contains(q) ||
                        track.album.lowercase().contains(q)
                val matchesProvider = selectedProviderFilter == null || track.provider == selectedProviderFilter
                matchesText && matchesProvider
            }
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
            Text(
                text = when (currentLanguage) {
                    AppLanguage.HINDI -> "खोजें"
                    AppLanguage.HINGLISH -> "Khojo"
                    AppLanguage.ENGLISH -> "Search"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "Spotify, Apple Music, YouTube aur JioSaavn me ek saath khojein",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        // Search Input Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        if (currentLanguage == AppLanguage.ENGLISH) "Track, artist, or album title..."
                        else "Gaana, kalakaar ya album ka naam..."
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = accentColor)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(cornerRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input_field")
            )
        }

        // Provider Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedProviderFilter == null,
                        onClick = { selectedProviderFilter = null },
                        label = { Text("All Providers") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.06f),
                            labelColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }

                items(MusicProviderType.values()) { provider ->
                    FilterChip(
                        selected = selectedProviderFilter == provider,
                        onClick = {
                            selectedProviderFilter = if (selectedProviderFilter == provider) null else provider
                        },
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
                        )
                    )
                }
            }
        }

        // Recent Searches Tags (if query empty)
        if (searchQuery.isBlank() && selectedProviderFilter == null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Recent Trending Searches",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recentSearches.take(3).forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier.clickable { onQueryChange(tag) }
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search Results List
        if (searchResults.isNotEmpty()) {
            item {
                Text(
                    text = "Results (${searchResults.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            items(searchResults) { track ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(cornerRadius))
                        .clickable { onTrackClick(track) }
                        .testTag("search_result_${track.id}"),
                    shape = RoundedCornerShape(cornerRadius),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(cornerRadius / 2))
                                .background(Color(0xFF222230))
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
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                PlatformBadge(provider = track.provider, showLabel = false)
                            }
                            Text(
                                text = track.artist,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.65f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(onClick = { onTrackClick(track) }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = accentColor
                            )
                        }
                    }
                }
            }
        } else if (searchQuery.isNotBlank()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "No tracks found"
                            AppLanguage.HINDI -> "कोई गाना नहीं मिला"
                            AppLanguage.HINGLISH -> "Koi gaana nahi mila"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.ENGLISH -> "Try searching with another keyword or provider"
                            AppLanguage.HINDI -> "अन्य नाम या प्रोवाइडर से खोजें"
                            AppLanguage.HINGLISH -> "Dusra naam search karke dekhein"
                        },
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

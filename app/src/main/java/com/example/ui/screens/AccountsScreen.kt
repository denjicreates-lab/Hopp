package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.PlatformBadge

@Composable
fun AccountsScreen(
    connections: List<ProviderConnection>,
    themeConfig: ThemeConfig,
    currentLanguage: AppLanguage,
    onToggleConnection: (MusicProviderType) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(themeConfig.accentColorHex)
    val cornerRadius = themeConfig.cornerRadiusDp.dp

    var showSetupDialogForProvider by remember { mutableStateOf<MusicProviderType?>(null) }

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
                    text = "Connected Providers",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Connect your streaming platforms to unify your music into one hub",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }

        items(connections) { conn ->
            val provider = conn.provider
            val brandColor = Color(provider.badgeColorHex)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(cornerRadius))
                    .testTag("account_card_${provider.name}"),
                shape = RoundedCornerShape(cornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(brandColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (provider) {
                                        MusicProviderType.SPOTIFY -> Icons.Default.GraphicEq
                                        MusicProviderType.APPLE_MUSIC -> Icons.Default.MusicNote
                                        MusicProviderType.YOUTUBE -> Icons.Default.PlayArrow
                                        MusicProviderType.JIOSAAVN -> Icons.Default.Headphones
                                        else -> Icons.Default.Cloud
                                    },
                                    contentDescription = provider.displayName,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(text = provider.displayName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                Text(
                                    text = if (conn.isConnected) "Connected: ${conn.accountName ?: "Active Session"}" else "Not Connected",
                                    fontSize = 12.sp,
                                    color = if (conn.isConnected) Color(0xFF34D399) else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Button(
                            onClick = { onToggleConnection(provider) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (conn.isConnected) Color.White.copy(alpha = 0.12f) else brandColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("connect_btn_${provider.name}")
                        ) {
                            Text(
                                text = if (conn.isConnected) "Disconnect" else "Connect",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Tech note
                    Text(
                        text = when (provider) {
                            MusicProviderType.SPOTIFY -> "OAuth PKCE & Web Playback SDK integration (Spotify Premium required for full playback)."
                            MusicProviderType.APPLE_MUSIC -> "MusicKit developer token and user token integration for Apple Music subscribers."
                            MusicProviderType.YOUTUBE -> "Official YouTube embed player & playlists sync via Data API."
                            MusicProviderType.JIOSAAVN -> "No official API exists. Hopp imports playlists via web links and clean tracklists. Unofficial scraping is strictly disabled."
                            else -> provider.description
                        },
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        lineHeight = 16.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showSetupDialogForProvider = provider }) {
                            Text("Setup Guide & Tokens", fontSize = 11.sp, color = accentColor)
                        }
                    }
                }
            }
        }

        // Clear all data button
        item {
            OutlinedButton(
                onClick = {
                    connections.forEach { onToggleConnection(it.provider) }
                    onShowToast("Cleared all connected provider sessions")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = Color(0xFFF87171))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Disconnect All Accounts & Clear Tokens", color = Color(0xFFF87171))
            }
        }
    }

    if (showSetupDialogForProvider != null) {
        val p = showSetupDialogForProvider!!
        AlertDialog(
            onDismissRequest = { showSetupDialogForProvider = null },
            title = { Text("${p.displayName} Integration Guide", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = when (p) {
                            MusicProviderType.SPOTIFY -> "Spotify uses secure OAuth 2.0 PKCE. User tokens are stored only in local app storage and sent directly to Spotify's official servers."
                            MusicProviderType.APPLE_MUSIC -> "Configure developer token in Settings or .env to enable lossless Apple Music library access via MusicKit."
                            MusicProviderType.YOUTUBE -> "Uses official YouTube Data API endpoints. Audio is played via YouTube embed."
                            MusicProviderType.JIOSAAVN -> "Since JioSaavn has no public API, Hopp allows you to import playlists by pasting song titles or shared URLs. No unofficial endpoints are used."
                            else -> ""
                        },
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSetupDialogForProvider = null },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(if (currentLanguage == AppLanguage.ENGLISH) "Got It" else "Theek Hai")
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }
}

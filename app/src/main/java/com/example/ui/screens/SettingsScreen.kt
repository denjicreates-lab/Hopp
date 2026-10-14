package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.MusicRepository
import com.example.model.AppLanguage
import com.example.model.ThemeConfig
import com.example.player.EqualizerPreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    themeConfig: ThemeConfig,
    sleepTimerMinutesLeft: Int?,
    equalizerPreset: EqualizerPreset,
    onLanguageChange: (AppLanguage) -> Unit,
    onEqualizerChange: (EqualizerPreset) -> Unit,
    onSleepTimerChange: (Int?) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = Color(themeConfig.accentColorHex)
    val cornerRadius = themeConfig.cornerRadiusDp.dp

    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showAiKeyInfoDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

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
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Preferences, Audio Engine, AI Keys & Open Source Information",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }

        // Language Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "App UI Language", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    AppLanguage.values().forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onLanguageChange(lang) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == lang,
                                onClick = { onLanguageChange(lang) },
                                colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = lang.label, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Audio & Playback Section (Sleep timer, Equalizer)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Audio & Playback", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)

                    // Sleep Timer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showSleepTimerDialog = true }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Sleep Timer", color = Color.White, fontWeight = FontWeight.Medium)
                            Text(
                                text = if (sleepTimerMinutesLeft != null) "Active: stopping in ${sleepTimerMinutesLeft}m" else "Off",
                                fontSize = 12.sp,
                                color = if (sleepTimerMinutesLeft != null) accentColor else Color.White.copy(alpha = 0.5f)
                            )
                        }
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = accentColor)
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                    // Equalizer Presets
                    Text(text = "Equalizer Preset", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        EqualizerPreset.values().take(3).forEach { eq ->
                            FilterChip(
                                selected = equalizerPreset == eq,
                                onClick = { onEqualizerChange(eq) },
                                label = { Text(eq.name.replace("_", " "), fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accentColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.06f),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // AI Settings Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(cornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "AI Services (Gemini & Audio)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)

                    val isKeyConfigured = BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Gemini 3.5 Flash Integration", color = Color.White, fontSize = 13.sp)
                            Text(
                                text = if (isKeyConfigured) "Active & Ready" else "Using Pre-Curated Offline Fallback",
                                fontSize = 11.sp,
                                color = if (isKeyConfigured) Color(0xFF34D399) else Color(0xFFFF9F1C)
                            )
                        }
                        IconButton(onClick = { showAiKeyInfoDialog = true }) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "AI Info", tint = accentColor)
                        }
                    }
                }
            }
        }

        // Open Source & About Section (Mandatory from prompt)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_source_section"),
                shape = RoundedCornerShape(cornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1728))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = accentColor)
                        Text(text = "Open Source Community", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }

                    Text(
                        text = "Hopp is 100% free and open-source software built for music lovers who believe in choice, privacy, and full player customization.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 17.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "License", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            Text(text = MusicRepository.LICENSE_NAME, fontWeight = FontWeight.Bold, color = Color(0xFF2DE2C4), fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MusicRepository.GITHUB_REPO_URL))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    onShowToast("GitHub: ${MusicRepository.GITHUB_REPO_URL}")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("github_repo_button")
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GitHub Repo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("About Hopp & Contributing Guide", color = Color.White)
                    }
                }
            }
        }
    }

    // Sleep Timer Dialog
    if (showSleepTimerDialog) {
        val timerOptions = listOf(15, 30, 45, 60, 90)
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = { Text("Sleep Timer", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timerOptions.forEach { mins ->
                        Button(
                            onClick = {
                                onSleepTimerChange(mins)
                                showSleepTimerDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$mins Minutes", color = Color.White)
                        }
                    }
                    if (sleepTimerMinutesLeft != null) {
                        OutlinedButton(
                            onClick = {
                                onSleepTimerChange(null)
                                showSleepTimerDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Turn Off Timer", color = Color(0xFFF87171))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Close", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }

    // AI Info Dialog
    if (showAiKeyInfoDialog) {
        AlertDialog(
            onDismissRequest = { showAiKeyInfoDialog = false },
            title = { Text("Gemini AI Configuration", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Hopp uses the Gemini 3.5 Flash model for generating 15-song mood playlists and recognizing audio. You can set your GEMINI_API_KEY securely via the Secrets panel in AI Studio. When running offline or without a key, Hopp automatically provides rich curated fallback mixes.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(onClick = { showAiKeyInfoDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = accentColor)) {
                    Text(if (currentLanguage == AppLanguage.ENGLISH) "Understood" else "Samajh Gaya")
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Hopp v1.0", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Hopp is created as an open-source unifier for music streaming enthusiasts worldwide.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "• Unifies Spotify, Apple Music, YouTube & JioSaavn\n• Full theme customization & custom layouts\n• AI Mood Mode & Audio recognition\n• Zero user audio or token tracking",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = accentColor)) {
                    Text(if (currentLanguage == AppLanguage.ENGLISH) "Close" else "Superb")
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }
}

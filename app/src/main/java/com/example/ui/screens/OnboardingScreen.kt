package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ThemeConfig
import com.example.model.ThemePreset

@Composable
fun OnboardingScreen(
    currentLanguage: AppLanguage,
    themeConfig: ThemeConfig,
    onThemeSelect: (ThemePreset) -> Unit,
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(0) }
    val accentColor = Color(themeConfig.accentColorHex)

    val steps = when (currentLanguage) {
        AppLanguage.ENGLISH -> listOf(
            Triple(
                "All Services in One Hub",
                "Aggregate your playlists and libraries across Spotify, Apple Music, YouTube, and JioSaavn seamlessly into one player.",
                Icons.Default.LibraryMusic
            ),
            Triple(
                "Bespoke Personalization",
                "Customize themes, vibrant accent palettes, background styles, and navigation geometry to your personal taste.",
                Icons.Default.Palette
            ),
            Triple(
                "AI-Powered Mood Curation",
                "Describe your desired atmosphere to Gemini AI for instant 15-track playlists, or identify music playing nearby.",
                Icons.Default.AutoAwesome
            )
        )
        AppLanguage.HINDI -> listOf(
            Triple(
                "सभी प्लेटफॉर्म्स, एक ऍप",
                "Spotify, Apple Music, YouTube और JioSaavn की सभी प्लेलिस्ट्स और गाने एक ही लाइब्रेरी में।",
                Icons.Default.LibraryMusic
            ),
            Triple(
                "अपनी पसंद का डिज़ाइन",
                "कस्टम थीम्स, कलर्स और नेविगेशन बार को अपनी इच्छानुसार सेट करें।",
                Icons.Default.Palette
            ),
            Triple(
                "एआई मूड क्यूरेशन",
                "Gemini AI से मूड के अनुसार गानों की प्लेलिस्ट और ऑडियो पहचान प्राप्त करें।",
                Icons.Default.AutoAwesome
            )
        )
        AppLanguage.HINGLISH -> listOf(
            Triple(
                "Saare platforms, ek app",
                "Spotify, Apple Music, YouTube aur JioSaavn ki saari playlists aur gaane ek hi library me.",
                Icons.Default.LibraryMusic
            ),
            Triple(
                "Apna look, apni marzi",
                "Midnight, Aurora, Neon themes, custom colors aur navigation bar ko apne style se set karo.",
                Icons.Default.Palette
            ),
            Triple(
                "AI se mood ke hisaab se gaane",
                "Gemini AI se kaho 'barish ho rahi hai' aur 15 perfect songs ka queue turant taiyaar.",
                Icons.Default.AutoAwesome
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0C0C14),
                        Color(0xFF140E26),
                        Color(0xFF0A0712)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip button top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCompleteOnboarding,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text("Skip", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            }

            // Carousel card
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(accentColor, Color(0xFF7C5CFF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = steps[step].third,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Text(
                    text = steps[step].first,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = steps[step].second,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                // Quick theme picker if on step 1
                if (step == 1) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Quick Theme Pick:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ThemePreset.values()) { preset ->
                                FilterChip(
                                    selected = themeConfig.preset == preset,
                                    onClick = { onThemeSelect(preset) },
                                    label = { Text(preset.title) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = accentColor,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Indicators & Next / Start button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Step Dots
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    steps.indices.forEach { idx ->
                        Box(
                            modifier = Modifier
                                .size(if (step == idx) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (step == idx) accentColor else Color.White.copy(alpha = 0.2f))
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step < steps.size - 1) {
                            step++
                        } else {
                            onCompleteOnboarding()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(
                        text = if (step < steps.size - 1) {
                            if (currentLanguage == AppLanguage.ENGLISH) "Continue" else "Aage Badhein"
                        } else {
                            if (currentLanguage == AppLanguage.ENGLISH) "Get Started 🎵" else "Start Hopping 🎵"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.HoppingEqualizer

@Composable
fun DiscoverAiScreen(
    currentLanguage: AppLanguage,
    moodSuggestions: List<MoodSongSuggestion>,
    isCuratingMood: Boolean,
    songRecognitionResult: SongRecognitionResult?,
    isRecognizingSong: Boolean,
    recognitionTimerSec: Int,
    themeConfig: ThemeConfig,
    onCurateMood: (String) -> Unit,
    onPlayAllMoodSongs: () -> Unit,
    onStartSongRecognition: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(themeConfig.accentColorHex)
    val secondaryColor = Color(themeConfig.secondaryColorHex)
    val cornerRadius = themeConfig.cornerRadiusDp.dp

    var selectedSubTab by remember { mutableStateOf(0) } // 0 = Mood Mode, 1 = Song Recognition
    var moodInputText by remember { mutableStateOf("") }

    val presetMoods = when (currentLanguage) {
        AppLanguage.ENGLISH -> listOf(
            "Rainy afternoon with hot coffee",
            "Late night reflective highway drive",
            "High-energy intense gym session",
            "Acoustic indie road trip with friends",
            "Deep focus ambient coding",
            "Sunset rooftop cocktail lounge"
        )
        AppLanguage.HINDI -> listOf(
            "बारिश और गरम चाय का सुकून",
            "देर रात की शांत ड्राइव",
            "जिम में हाई एनर्जी वर्कआउट",
            "दोस्तों के साथ रोड ट्रिप",
            "गहरा ध्यान और कोडिंग",
            "शाम की कॉफी और रोमांटिक धुनें"
        )
        AppLanguage.HINGLISH -> listOf(
            "Barish ho rahi hai aur akela hoon",
            "Late night chai aur purane gaane",
            "Gym me heavy workout high energy",
            "Road trip with best friends",
            "Sukoon & deep meditation",
            "Shaam ki coffee aur romantic vibes"
        )
    }

    // Pulse animation for recognition mic button
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecognizingSong) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "AI Music Discovery"
                        AppLanguage.HINDI -> "एआई म्यूज़िक लैब"
                        AppLanguage.HINGLISH -> "Hopp AI Lab"
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.ENGLISH -> "Powered by Gemini 3.5 Flash — Contextual mood curation & acoustic identification"
                        AppLanguage.HINDI -> "Gemini 3.5 Flash द्वारा संचालित — मूड क्यूरेशन और ऑडियो पहचान"
                        AppLanguage.HINGLISH -> "Powered by Gemini 3.5 Flash — Mood curation & Song identification"
                    },
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }

        // Sub Tabs Selector (Mood Mode vs Song Recognition)
        item {
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = Color.White.copy(alpha = 0.06f),
                contentColor = accentColor,
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                divider = {}
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Mood, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.ENGLISH -> "Mood Mode"
                                    AppLanguage.HINDI -> "मूड मोड"
                                    AppLanguage.HINGLISH -> "Mood Mode"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_mood_mode")
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Hearing, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.ENGLISH -> "Song Recognition"
                                    AppLanguage.HINDI -> "गाना पहचानें"
                                    AppLanguage.HINGLISH -> "Song Recognition"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_song_recognition")
                )
            }
        }

        if (selectedSubTab == 0) {
            // === MOOD MODE ===
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
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "Describe your desired atmosphere or sentiment:"
                                AppLanguage.HINDI -> "अपने मूड या माहौल का वर्णन करें:"
                                AppLanguage.HINGLISH -> "Kaisa feel ho raha hai? (Describe your vibe)"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        OutlinedTextField(
                            value = moodInputText,
                            onValueChange = { moodInputText = it },
                            placeholder = {
                                Text(
                                    when (currentLanguage) {
                                        AppLanguage.ENGLISH -> "e.g. Rainy afternoon with coffee, soothing acoustic guitar melodies..."
                                        AppLanguage.HINDI -> "उदा. बारिश का मौसम, चाय, शांत और मधुर धुनें..."
                                        AppLanguage.HINGLISH -> "e.g. Barish ho rahi hai, chai pee raha hoon, kuch soothing..."
                                    }
                                )
                            },
                            singleLine = false,
                            maxLines = 3,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("mood_text_input")
                        )

                        // Quick suggestion pills
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(presetMoods) { preset ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.08f),
                                    modifier = Modifier.clickable {
                                        moodInputText = preset
                                        onCurateMood(preset)
                                    }
                                ) {
                                    Text(
                                        text = preset,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (moodInputText.isNotBlank()) {
                                    onCurateMood(moodInputText)
                                }
                            },
                            enabled = !isCuratingMood,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            modifier = Modifier.fillMaxWidth().testTag("curate_mood_button")
                        ) {
                            if (isCuratingMood) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (currentLanguage == AppLanguage.ENGLISH) "Curating 15 Tracks with Gemini AI..."
                                    else "AI Curating 15 Songs..."
                                )
                            } else {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (currentLanguage == AppLanguage.ENGLISH) "Curate Mood Playlist with Gemini AI"
                                    else "Gemini AI se Mood Mix Banao"
                                )
                            }
                        }
                    }
                }
            }

            // Results Section
            if (moodSuggestions.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "15 Curated Songs",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Matched across Spotify, Apple Music & YouTube",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        Button(
                            onClick = onPlayAllMoodSongs,
                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("play_all_mood_queue_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Play All (Queue)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                itemsIndexed(moodSuggestions) { index, song ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.width(28.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${song.artist} • ${song.language}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = song.reason,
                                    fontSize = 11.sp,
                                    color = accentColor.copy(alpha = 0.85f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // === SONG RECOGNITION (GAANA PEHCHANO) ===
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(cornerRadius),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (isRecognizingSong) {
                                if (currentLanguage == AppLanguage.ENGLISH) "Listening to ambient audio..."
                                else if (currentLanguage == AppLanguage.HINDI) "ऑडियो सुन रहे हैं..."
                                else "Sunn raha hoon..."
                            } else {
                                if (currentLanguage == AppLanguage.ENGLISH) "Identify Song Playing Nearby"
                                else if (currentLanguage == AppLanguage.HINDI) "आस-पास बज रहे गाने को पहचानें"
                                else "Aas paas bajte gaane ko pehchano"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = if (isRecognizingSong) {
                                if (currentLanguage == AppLanguage.ENGLISH) "Recording audio sample... (${recognitionTimerSec}s / 6s) Analyzing acoustic profile"
                                else "Recording clip... (${recognitionTimerSec}s / 6s) Analyzing acoustic frequencies"
                            } else {
                                if (currentLanguage == AppLanguage.ENGLISH) "Sample 6 to 10 seconds of nearby audio via microphone for Gemini AI identification."
                                else if (currentLanguage == AppLanguage.HINDI) "माइक्रोफ़ोन से 6-10 सेकंड का ऑडियो रिकॉर्ड करके गाना पहचानें।"
                                else "Microphone se 6-10 second ka audio sunkar Gemini AI gaana pehchaanta hai."
                            },
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.65f),
                            textAlign = TextAlign.Center
                        )

                        // Big Mic Pulse Action Button
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        if (isRecognizingSong) listOf(Color(0xFFFF4D6D), Color(0xFF2DE2C4))
                                        else listOf(accentColor, secondaryColor)
                                    )
                                )
                                .clickable(enabled = !isRecognizingSong) { onStartSongRecognition() }
                                .testTag("start_song_recognition_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRecognizingSong) Icons.Default.Hearing else Icons.Default.Mic,
                                contentDescription = "Listen",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        if (isRecognizingSong) {
                            HoppingEqualizer(
                                isPlaying = true,
                                barColor = accentColor,
                                maxHeight = 28.dp,
                                barWidth = 5.dp
                            )
                        }

                        // Match Result
                        if (songRecognitionResult != null && songRecognitionResult.isMatchFound) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.5.dp, Color(0xFF34D399), RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF13221C))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                                        Text(
                                            text = if (currentLanguage == AppLanguage.ENGLISH) "TRACK IDENTIFIED (${(songRecognitionResult.confidence * 100).toInt()}% Match)"
                                            else if (currentLanguage == AppLanguage.HINDI) "गाना मिल गया! (${(songRecognitionResult.confidence * 100).toInt()}% मैच)"
                                            else "GAANA MIL GAYA! (${(songRecognitionResult.confidence * 100).toInt()}% Match)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF34D399)
                                        )
                                    }

                                    Text(
                                        text = songRecognitionResult.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )

                                    Text(
                                        text = songRecognitionResult.artist,
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )

                                    Text(
                                        text = songRecognitionResult.notes,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage

@Composable
fun HoppTopBar(
    currentLanguage: AppLanguage,
    onLanguageClick: () -> Unit,
    onQuickListenClick: () -> Unit,
    onAccountsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    accentColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    // Subtle playful hop animation for Hopp brand icon
    val infiniteTransition = rememberInfiniteTransition(label = "hop_brand")
    val hopScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hop_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand logo & title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .scale(hopScale)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(accentColor, secondaryColor)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Hopp Logo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Hopp",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "HUB",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = accentColor
                        )
                    }
                }
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.HINGLISH -> "Saare music apps, ek hi jagah"
                        AppLanguage.HINDI -> "सभी म्यूज़िक ऍप्स, एक जगह"
                        AppLanguage.ENGLISH -> "All music apps, unified"
                    },
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // Action buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Quick Song Listen Button (Mic)
            IconButton(
                onClick = onQuickListenClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f))
                    .testTag("quick_listen_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Hearing,
                    contentDescription = "Song Recognition",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Language Toggle Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .clickable { onLanguageClick() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("language_toggle_button")
            ) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.HINGLISH -> "🇮🇳 Hin"
                        AppLanguage.HINDI -> "🇮🇳 हिंदी"
                        AppLanguage.ENGLISH -> "🌐 EN"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            // Accounts Button
            IconButton(
                onClick = onAccountsClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .testTag("accounts_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Connected Accounts",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Settings Button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

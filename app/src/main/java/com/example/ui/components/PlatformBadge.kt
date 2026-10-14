package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MusicProviderType

@Composable
fun PlatformBadge(
    provider: MusicProviderType,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val bgColor = Color(provider.badgeColorHex)
    val icon = when (provider) {
        MusicProviderType.SPOTIFY -> Icons.Default.GraphicEq
        MusicProviderType.APPLE_MUSIC -> Icons.Default.MusicNote
        MusicProviderType.YOUTUBE -> Icons.Default.PlayArrow
        MusicProviderType.JIOSAAVN -> Icons.Default.Headphones
        MusicProviderType.LOCAL -> Icons.Default.Folder
        MusicProviderType.OPEN_MUSIC -> Icons.Default.CloudDownload
    }

    Row(
        modifier = modifier
            .background(bgColor.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
            .padding(horizontal = if (showLabel) 6.dp else 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = provider.displayName,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
        if (showLabel) {
            Text(
                text = provider.displayName,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

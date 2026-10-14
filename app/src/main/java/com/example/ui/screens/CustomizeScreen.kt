package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.HoppingEqualizer

@Composable
fun CustomizeScreen(
    themeConfig: ThemeConfig,
    navConfig: NavigationConfig,
    currentLanguage: AppLanguage,
    onThemeChange: (ThemeConfig) -> Unit,
    onNavChange: (NavigationConfig) -> Unit,
    onResetTheme: () -> Unit,
    onExportJson: () -> String,
    onImportJson: (String) -> Boolean,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStudioTab by remember { mutableStateOf(0) } // 0 = Theme Studio, 1 = Navigation Studio
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var exportedJsonText by remember { mutableStateOf("") }

    val accentColors = listOf(
        0xFFFF4D6D to "Coral Pink",
        0xFF7C5CFF to "Neon Violet",
        0xFF2DE2C4 to "Electric Cyan",
        0xFF34D399 to "Emerald Green",
        0xFFFF9F1C to "Sunset Amber",
        0xFF3B82F6 to "Cobalt Blue"
    )

    val currentAccent = Color(themeConfig.accentColorHex)

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
                    text = "Theme & UI Studio",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Apna look, apni marzi — Customize every color, shape & navigation dock",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }

        // Sub Tabs: Theme Studio vs Navigation Editor
        item {
            TabRow(
                selectedTabIndex = selectedStudioTab,
                containerColor = Color.White.copy(alpha = 0.06f),
                contentColor = currentAccent,
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                divider = {}
            ) {
                Tab(
                    selected = selectedStudioTab == 0,
                    onClick = { selectedStudioTab = 0 },
                    text = { Text("Theme Studio", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_theme_studio")
                )
                Tab(
                    selected = selectedStudioTab == 1,
                    onClick = { selectedStudioTab = 1 },
                    text = { Text("Navigation Editor", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_nav_editor")
                )
            }
        }

        // Live Preview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(themeConfig.cornerRadiusDp.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            when (themeConfig.backgroundStyle) {
                                BackgroundStyle.SOLID -> Brush.linearGradient(listOf(Color(0xFF151520), Color(0xFF151520)))
                                BackgroundStyle.GRADIENT -> Brush.verticalGradient(listOf(Color(0xFF221330), Color(0xFF120E1E)))
                                BackgroundStyle.BLURRED_ART -> Brush.radialGradient(listOf(currentAccent.copy(alpha = 0.35f), Color(0xFF100C1A)))
                                BackgroundStyle.ANIMATED_MESH -> Brush.sweepGradient(listOf(currentAccent.copy(alpha = 0.3f), Color(0xFF7C5CFF).copy(alpha = 0.3f), Color(0xFF120E1E)))
                            }
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE THEME PREVIEW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentAccent,
                            letterSpacing = 1.sp
                        )
                        HoppingEqualizer(isPlaying = true, barColor = currentAccent)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(
                                    when (themeConfig.albumArtShape) {
                                        AlbumArtShape.CIRCLE, AlbumArtShape.VINYL -> CircleShape
                                        AlbumArtShape.ROUNDED_SQUARE -> RoundedCornerShape(themeConfig.cornerRadiusDp.dp)
                                    }
                                )
                                .background(currentAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (themeConfig.albumArtShape == AlbumArtShape.VINYL) Icons.Default.Album else Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Hopp Soundscape", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text(text = "Layout: ${themeConfig.playerLayout.title} • Radius: ${themeConfig.cornerRadiusDp}dp", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                        }

                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = currentAccent),
                            shape = RoundedCornerShape(themeConfig.cornerRadiusDp.dp)
                        ) {
                            Text("Play", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        if (selectedStudioTab == 0) {
            // === THEME STUDIO SETTINGS ===

            // Presets
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Theme Presets", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ThemePreset.values()) { preset ->
                            val isSelected = themeConfig.preset == preset
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val newConfig = when (preset) {
                                        ThemePreset.MIDNIGHT -> themeConfig.copy(preset = preset, accentColorHex = 0xFFFF4D6D, backgroundStyle = BackgroundStyle.GRADIENT)
                                        ThemePreset.AURORA -> themeConfig.copy(preset = preset, accentColorHex = 0xFF2DE2C4, backgroundStyle = BackgroundStyle.BLURRED_ART)
                                        ThemePreset.SUNSET -> themeConfig.copy(preset = preset, accentColorHex = 0xFFFF9F1C, backgroundStyle = BackgroundStyle.GRADIENT)
                                        ThemePreset.MONO -> themeConfig.copy(preset = preset, accentColorHex = 0xFFFFFFFF, backgroundStyle = BackgroundStyle.SOLID)
                                        ThemePreset.NEON -> themeConfig.copy(preset = preset, accentColorHex = 0xFF7C5CFF, backgroundStyle = BackgroundStyle.ANIMATED_MESH)
                                        ThemePreset.LIGHT -> themeConfig.copy(preset = preset, accentColorHex = 0xFFFF4D6D, backgroundStyle = BackgroundStyle.SOLID)
                                    }
                                    onThemeChange(newConfig)
                                },
                                label = { Text(preset.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentAccent,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.06f),
                                    labelColor = Color.White
                                ),
                                modifier = Modifier.testTag("preset_${preset.name}")
                            )
                        }
                    }
                }
            }

            // Accent Colors
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Accent Color", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        accentColors.forEach { (colorHex, _) ->
                            val color = Color(colorHex)
                            val isSelected = themeConfig.accentColorHex == colorHex
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onThemeChange(themeConfig.copy(accentColorHex = colorHex))
                                    }
                                    .testTag("accent_color_$colorHex"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Background Style
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Background Style", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(BackgroundStyle.values()) { style ->
                            FilterChip(
                                selected = themeConfig.backgroundStyle == style,
                                onClick = { onThemeChange(themeConfig.copy(backgroundStyle = style)) },
                                label = { Text(style.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentAccent,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.06f),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Corner Radius Selector
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Card Corner Radius: ${themeConfig.cornerRadiusDp}dp", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Slider(
                        value = themeConfig.cornerRadiusDp.toFloat(),
                        onValueChange = { onThemeChange(themeConfig.copy(cornerRadiusDp = it.toInt())) },
                        valueRange = 8f..32f,
                        steps = 5,
                        colors = SliderDefaults.colors(thumbColor = currentAccent, activeTrackColor = currentAccent)
                    )
                }
            }

            // Now Playing Layout Style
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Now Playing Layout", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlayerLayout.values().forEach { layout ->
                            FilterChip(
                                selected = themeConfig.playerLayout == layout,
                                onClick = { onThemeChange(themeConfig.copy(playerLayout = layout)) },
                                label = { Text(layout.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentAccent,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.06f),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Album Art Shape
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Album Art Shape", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AlbumArtShape.values().forEach { shape ->
                            FilterChip(
                                selected = themeConfig.albumArtShape == shape,
                                onClick = { onThemeChange(themeConfig.copy(albumArtShape = shape)) },
                                label = { Text(shape.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentAccent,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.06f),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Action Buttons: Reset, Export JSON, Import JSON
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onResetTheme,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Default")
                    }

                    Button(
                        onClick = {
                            exportedJsonText = onExportJson()
                            showExportDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = currentAccent),
                        modifier = Modifier.weight(1f).testTag("export_theme_json_button")
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export JSON")
                    }

                    IconButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.08f)).testTag("import_theme_json_button")
                    ) {
                        Icon(imageVector = Icons.Default.FileUpload, contentDescription = "Import", tint = Color.White)
                    }
                }
            }
        } else {
            // === NAVIGATION STUDIO SETTINGS ===
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Navigation Dock Style", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    NavigationType.values().forEach { type ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavChange(navConfig.copy(navType = type)) }
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (navConfig.navType == type) currentAccent.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = navConfig.navType == type,
                                    onClick = { onNavChange(navConfig.copy(navType = type)) },
                                    colors = RadioButtonDefaults.colors(selectedColor = currentAccent)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = type.title, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(
                                        text = when (type) {
                                            NavigationType.BOTTOM_BAR -> "Standard Material 3 bottom bar"
                                            NavigationType.FLOATING_DOCK -> "Modern floating rounded pill bar"
                                            NavigationType.SIDEBAR -> "Ergonomic left navigation rail"
                                            NavigationType.TOP_TABS -> "Header scrollable top tab tabs"
                                        },
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Icon Only Toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Icon-Only Navigation", fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "Hide text labels for minimal modern look", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                        Switch(
                            checked = navConfig.isIconOnly,
                            onCheckedChange = { onNavChange(navConfig.copy(isIconOnly = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = currentAccent, checkedTrackColor = currentAccent.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }
    }

    // Export JSON Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Exported Theme JSON", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = exportedJsonText,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onShowToast("Theme JSON copied!")
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = currentAccent)
                ) {
                    Text("OK")
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }

    // Import JSON Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Theme JSON", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = importJsonText,
                    onValueChange = { importJsonText = it },
                    placeholder = { Text("Paste exported theme JSON here...") },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (onImportJson(importJsonText)) {
                            showImportDialog = false
                            importJsonText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = currentAccent)
                ) {
                    Text("Apply Theme")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }
}

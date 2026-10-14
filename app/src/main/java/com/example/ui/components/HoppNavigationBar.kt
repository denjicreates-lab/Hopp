package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.NavigationConfig
import com.example.model.NavigationType

@Composable
fun HoppNavigationBar(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    navConfig: NavigationConfig,
    currentLanguage: AppLanguage,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val items = navConfig.items.filter { it.isVisible }

    fun getIcon(id: String): ImageVector = when (id) {
        "home" -> Icons.Default.Home
        "library" -> Icons.Default.LibraryMusic
        "search" -> Icons.Default.Search
        "discover" -> Icons.Default.AutoAwesome
        "open_music" -> Icons.Default.CloudDownload
        "customize" -> Icons.Default.Palette
        else -> Icons.Default.Circle
    }

    fun getLabel(id: String): String {
        val item = items.firstOrNull { it.id == id } ?: return id
        return when (currentLanguage) {
            AppLanguage.HINDI -> item.labelHindi
            AppLanguage.HINGLISH -> item.labelHinglish
            AppLanguage.ENGLISH -> item.labelEnglish
        }
    }

    when (navConfig.navType) {
        NavigationType.FLOATING_DOCK -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFF181824).copy(alpha = 0.95f),
                    shadowElevation = 10.dp,
                    modifier = Modifier.clip(RoundedCornerShape(32.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items.forEach { item ->
                            val isSelected = activeTab == item.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) accentColor.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { onTabSelected(item.id) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("nav_tab_${item.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = getIcon(item.id),
                                        contentDescription = getLabel(item.id),
                                        tint = if (isSelected) accentColor else Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (isSelected && !navConfig.isIconOnly) {
                                        Text(
                                            text = getLabel(item.id),
                                            color = accentColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        NavigationType.TOP_TABS -> {
            ScrollableTabRow(
                selectedTabIndex = items.indexOfFirst { it.id == activeTab }.coerceAtLeast(0),
                containerColor = Color(0xFF111118),
                contentColor = accentColor,
                edgePadding = 12.dp,
                divider = {},
                modifier = modifier.fillMaxWidth()
            ) {
                items.forEach { item ->
                    val isSelected = activeTab == item.id
                    Tab(
                        selected = isSelected,
                        onClick = { onTabSelected(item.id) },
                        text = {
                            if (!navConfig.isIconOnly) {
                                Text(
                                    text = getLabel(item.id),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = getIcon(item.id),
                                contentDescription = getLabel(item.id),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        selectedContentColor = accentColor,
                        unselectedContentColor = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.testTag("nav_tab_${item.id}")
                    )
                }
            }
        }

        NavigationType.SIDEBAR -> {
            NavigationRail(
                containerColor = Color(0xFF13131D),
                contentColor = Color.White,
                modifier = modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                items.forEach { item ->
                    val isSelected = activeTab == item.id
                    NavigationRailItem(
                        selected = isSelected,
                        onClick = { onTabSelected(item.id) },
                        icon = {
                            Icon(
                                imageVector = getIcon(item.id),
                                contentDescription = getLabel(item.id)
                            )
                        },
                        label = if (!navConfig.isIconOnly) {
                            { Text(text = getLabel(item.id), fontSize = 10.sp) }
                        } else null,
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = accentColor,
                            selectedTextColor = accentColor,
                            indicatorColor = accentColor.copy(alpha = 0.18f),
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${item.id}")
                    )
                }
            }
        }

        NavigationType.BOTTOM_BAR -> {
            NavigationBar(
                containerColor = Color(0xFF12121A),
                contentColor = Color.White,
                tonalElevation = 8.dp,
                modifier = modifier.fillMaxWidth()
            ) {
                items.forEach { item ->
                    val isSelected = activeTab == item.id
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onTabSelected(item.id) },
                        icon = {
                            Icon(
                                imageVector = getIcon(item.id),
                                contentDescription = getLabel(item.id),
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = if (!navConfig.isIconOnly) {
                            {
                                Text(
                                    text = getLabel(item.id),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        } else null,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = accentColor,
                            selectedTextColor = accentColor,
                            indicatorColor = accentColor.copy(alpha = 0.18f),
                            unselectedIconColor = Color.White.copy(alpha = 0.55f),
                            unselectedTextColor = Color.White.copy(alpha = 0.55f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${item.id}")
                    )
                }
            }
        }
    }
}

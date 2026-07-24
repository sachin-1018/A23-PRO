package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ScreenTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    REPORT("7-Day Report", Icons.Default.Assessment),
    DATA("Data & Market", Icons.Default.AddBox),
    HISTORY("History", Icons.Default.History),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun BottomNavBarPro(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0A0F1D).copy(alpha = 0.55f))
                .border(
                    1.5.dp,
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFFD700).copy(alpha = 0.5f),
                            Color(0xFF00FFCC).copy(alpha = 0.3f),
                            Color(0xFFFFD700).copy(alpha = 0.5f)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScreenTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                val tint = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.5f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("tab_${tab.name.lowercase()}")
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.title,
                        color = tint,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

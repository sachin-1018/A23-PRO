package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopAppBarPro(
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMicClick: () -> Unit,
    isPinLocked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Menu Button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF222834).copy(alpha = 0.8f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                .clickable { onMenuClick() }
                .testTag("top_menu_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FormatListBulleted,
                contentDescription = "Menu",
                tint = Color(0xFFFFD700)
            )
        }

        // App Name Title Pill "A23 PRO"
        Box(
            modifier = Modifier
                .height(38.dp)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF151922).copy(alpha = 0.85f))
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "A23 PRO",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Voice / Mic icon
            IconButton(
                onClick = onMicClick,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF222834).copy(alpha = 0.8f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .testTag("top_mic_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Assistant",
                    tint = Color.White
                )
            }

            // Profile Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF333D4F).copy(alpha = 0.85f))
                    .border(
                        1.dp,
                        if (isPinLocked) Color(0xFFFF4444) else Color(0xFF00FFCC),
                        CircleShape
                    )
                    .clickable { onProfileClick() }
                    .testTag("top_profile_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White
                )
            }
        }
    }
}

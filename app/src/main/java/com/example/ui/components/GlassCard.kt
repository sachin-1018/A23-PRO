package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderWidth: Dp = 1.dp,
    borderColors: List<Color> = listOf(
        Color(0xFFFFD700).copy(alpha = 0.4f), // Gold
        Color(0xFF00FFCC).copy(alpha = 0.2f), // Cyan
        Color.White.copy(alpha = 0.1f)
    ),
    backgroundColor: Color = Color(0xFF0A0F1D).copy(alpha = 0.45f),
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderBrush = Brush.linearGradient(colors = borderColors)

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor, shape)
            .border(borderWidth, borderBrush, shape)
            .padding(contentPadding),
        content = content
    )
}

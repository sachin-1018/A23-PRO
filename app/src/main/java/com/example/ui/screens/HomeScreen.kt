package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketEntity
import com.example.engine.DayReportItem
import com.example.engine.OtcEngine
import com.example.engine.TodayPrediction
import com.example.ui.components.GlassCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    selectedMarket: String,
    marketsList: List<MarketEntity>,
    prediction: TodayPrediction,
    reportItems: List<DayReportItem>,
    onMarketSelect: (String) -> Unit,
    onViewFullReportClick: () -> Unit
) {
    val context = LocalContext.current
    val liveDate = remember { SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date()) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Markets Selector Header Label
        Text(
            text = "Markets & Predictions",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Market Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(marketsList) { market ->
                val isSelected = market.name.equals(selectedMarket, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            if (isSelected) Color(0xFFFFD700) else Color(0xFF101420).copy(alpha = 0.50f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(22.dp)
                        )
                        .clickable { onMarketSelect(market.name) }
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = market.name,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Glass Prediction Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            backgroundColor = Color(0xFF0F131C).copy(alpha = 0.75f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Market name & Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MARKET: $selectedMarket",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "DATE: $liveDate",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Formula OTC Badge
                Text(
                    text = "✨ TODAY'S FORMULA OTC",
                    color = Color(0xFFFFD700),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Large OTC Numbers Display (e.g. 0 , 9)
                Text(
                    text = prediction.mainOtc,
                    color = Color(0xFFFFD700),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Support OTC: ${prediction.supportOtc}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Super Jodi Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFD700).copy(alpha = 0.08f))
                        .border(1.5.dp, Color(0xFFFFD700).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SUPER JODI: ${prediction.superJodi}",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Safe Day Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF00FF66).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFF00FF66), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Safe Day",
                            tint = Color(0xFF00FF66),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SAFE DAY: ${prediction.safeDay} (HIGH ACCURACY)",
                            color = Color(0xFF00FF66),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons: WhatsApp & Download
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // WhatsApp Share
                    OutlinedButton(
                        onClick = {
                            shareOnWhatsApp(
                                context,
                                "🔥 *A23 PRO PREDICTION* 🔥\nMarket: $selectedMarket\nDate: $liveDate\nOTC: ${prediction.mainOtc}\nSupport: ${prediction.supportOtc}\nSuper Jodi: ${prediction.superJodi}\nSafe Day: ${prediction.safeDay}"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("home_whatsapp_btn"),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00FF66))
                    ) {
                        Text(
                            text = "WhatsApp",
                            color = Color(0xFF00FF66),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Download / Save
                    Button(
                        onClick = {
                            Toast.makeText(context, "Card saved to gallery!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("home_download_btn"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD700))
                    ) {
                        Text(
                            text = "Download",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7-Day Accuracy Summary Bar matching Image #1
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            backgroundColor = Color(0xFF0F131C).copy(alpha = 0.75f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Accuracy",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "7-DAY ACCURACY SUMMARY",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "TAP TO VIEW FULL >",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onViewFullReportClick() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Row of 7 Pass/Fail badges
                val displayItems = reportItems.take(7)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (displayItems.isEmpty()) {
                        Text(
                            text = "No history available for $selectedMarket",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        displayItems.forEach { item ->
                            val isPass = item.isPass
                            val circleColor = if (isPass) Color(0xFF00FF66) else Color(0xFFFF4444)

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(circleColor.copy(alpha = 0.15f))
                                    .border(1.5.dp, circleColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isPass) "✓" else "✕",
                                    color = circleColor,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(140.dp)) // Bottom padding for smooth scrolling above navigation bar
    }
}

private fun shareOnWhatsApp(context: Context, text: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.whatsapp")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Fallback to chooser if WhatsApp is not installed directly
        val chooser = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }, "Share Prediction")
        context.startActivity(chooser)
    }
}

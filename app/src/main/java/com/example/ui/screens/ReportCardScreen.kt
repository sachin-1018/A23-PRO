package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.DayReportItem
import com.example.ui.components.GlassCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportCardScreen(
    selectedMarket: String,
    reportList: List<DayReportItem>
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Set of selected date strings for tick mark functionality
    var selectedDates by remember { mutableStateOf(setOf<String>()) }
    var highlightedDates by remember { mutableStateOf(setOf<String>()) }

    // Long pressed item state for dialog
    var longPressedItem by remember { mutableStateOf<DayReportItem?>(null) }

    // Dialog for Long Press options
    if (longPressedItem != null) {
        val item = longPressedItem!!
        val isSelected = selectedDates.contains(item.dateStr)
        val isHighlighted = highlightedDates.contains(item.dateStr)

        AlertDialog(
            onDismissRequest = { longPressedItem = null },
            title = {
                Text(
                    text = "Day Action: ${item.dateStr}",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Result: ${item.resultRaw} | Status: ${if (item.isPass) "PASS ❤️" else "FAIL ✕"}",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 1: Toggle Tick Selection
                    Button(
                        onClick = {
                            selectedDates = if (isSelected) {
                                selectedDates - item.dateStr
                            } else {
                                selectedDates + item.dateStr
                            }
                            longPressedItem = null
                            Toast.makeText(context, if (isSelected) "Unticked ${item.dateStr}" else "Ticked ${item.dateStr} ✓", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E283A))
                    ) {
                        Icon(imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank, contentDescription = "Tick", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isSelected) "Remove Tick (Untick)" else "Tick / Select Day", color = Color.White)
                    }

                    // Option 2: Copy Result to Clipboard
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("${item.dateStr} - OTC: ${item.otcStr} - Result: ${item.resultRaw}"))
                            Toast.makeText(context, "Copied result for ${item.dateStr}!", Toast.LENGTH_SHORT).show()
                            longPressedItem = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E283A))
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF00FFCC))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Result", color = Color.White)
                    }

                    // Option 3: Share Day on WhatsApp
                    Button(
                        onClick = {
                            val singleText = "🔥 *A23 PRO - $selectedMarket*\n📅 Date: ${item.dateStr}\nOTC: ${item.otcStr}\nJodi: ${item.jodiStr}\nResult: ${item.resultRaw}\nStatus: ${if (item.isPass) "PASS ❤️" else "FAIL ❌"}"
                            shareReportOnWhatsApp(context, singleText)
                            longPressedItem = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AA44))
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Day on WhatsApp", color = Color.White)
                    }

                    // Option 4: Highlight / Pin
                    Button(
                        onClick = {
                            highlightedDates = if (isHighlighted) {
                                highlightedDates - item.dateStr
                            } else {
                                highlightedDates + item.dateStr
                            }
                            longPressedItem = null
                            Toast.makeText(context, if (isHighlighted) "Un-highlighted" else "Highlighted ${item.dateStr} ✨", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E283A))
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Highlight", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isHighlighted) "Remove Highlight" else "Highlight / Pin Day ✨", color = Color.White)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { longPressedItem = null }) {
                    Text("Close", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF0F1422).copy(alpha = 0.95f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "7-DAY REPORT CARD",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Market: $selectedMarket",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Instructions badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                    .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "💡 Long press item for options",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected items bar when items are ticked
        AnimatedVisibility(visible = selectedDates.isNotEmpty()) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                cornerRadius = 14.dp,
                backgroundColor = Color(0xFFFFD700).copy(alpha = 0.2f),
                borderColors = listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Ticked", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${selectedDates.size} Day(s) Ticked",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                val selectedItemsText = reportList
                                    .filter { selectedDates.contains(it.dateStr) }
                                    .joinToString("\n") { "📅 ${it.dateStr}: Result ${it.resultRaw} | OTC ${it.otcStr}" }

                                clipboardManager.setText(AnnotatedString(selectedItemsText))
                                Toast.makeText(context, "Copied selected results!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Selected", tint = Color(0xFF00FFCC))
                        }

                        IconButton(
                            onClick = {
                                val selectedSummary = buildString {
                                    append("📊 *SELECTED REPORT DAYS - $selectedMarket*\n\n")
                                    reportList.filter { selectedDates.contains(it.dateStr) }.forEach { item ->
                                        val status = if (item.isPass) "PASS ❤️" else "FAIL ❌"
                                        append("📅 ${item.dateStr} | Status: $status\n")
                                        append("OTC: ${item.otcStr} | Result: ${item.resultRaw}\n\n")
                                    }
                                    append("Powered by A23 Pro")
                                }
                                shareReportOnWhatsApp(context, selectedSummary)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share Selected", tint = Color(0xFF00FF66))
                        }

                        IconButton(onClick = { selectedDates = emptySet() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Selection", tint = Color.White)
                        }
                    }
                }
            }
        }

        // WhatsApp Share Button for Full 7-Day
        Button(
            onClick = {
                val reportSummary = buildString {
                    append("📊 *7-DAY REPORT CARD - $selectedMarket*\n\n")
                    reportList.take(7).forEach { item ->
                        val status = if (item.isPass) "PASS ❤️" else "FAIL ❌"
                        append("📅 ${item.dateStr} | Status: $status\n")
                        append("OTC: ${item.otcStr} | Result: ${item.resultRaw}\n\n")
                    }
                    append("Powered by A23 Pro")
                }
                shareReportOnWhatsApp(context, reportSummary)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("report_share_whatsapp_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00AA44)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Share All 7-Day Report on WhatsApp",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Reports List
        if (reportList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No report entries available for $selectedMarket.\nAdd market data in 'Data & Market' tab.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(reportList) { item ->
                    val isTicked = selectedDates.contains(item.dateStr)
                    val isHighlighted = highlightedDates.contains(item.dateStr)

                    ReportItemCard(
                        item = item,
                        isTicked = isTicked,
                        isHighlighted = isHighlighted,
                        onTickToggle = {
                            selectedDates = if (isTicked) selectedDates - item.dateStr else selectedDates + item.dateStr
                        },
                        onLongClick = {
                            longPressedItem = item
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportItemCard(
    item: DayReportItem,
    isTicked: Boolean,
    isHighlighted: Boolean,
    onTickToggle: () -> Unit,
    onLongClick: () -> Unit
) {
    // Glass Card with fully translucent background so background wallpaper shows clearly!
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { onTickToggle() },
                onLongClick = { onLongClick() }
            ),
        cornerRadius = 16.dp,
        borderWidth = if (isHighlighted || isTicked) 2.dp else 1.dp,
        borderColors = when {
            isTicked -> listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
            isHighlighted -> listOf(Color(0xFFFFD700), Color(0xFFFF8800))
            item.isPass -> listOf(Color(0xFF00FF66).copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
            else -> listOf(Color(0xFFFF4444).copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
        },
        backgroundColor = if (isTicked) Color(0xFFFFD700).copy(alpha = 0.25f) else Color(0xFF0A0F1D).copy(alpha = 0.40f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Checkbox Tick, Date & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isTicked,
                        onCheckedChange = { onTickToggle() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFD700),
                            uncheckedColor = Color.White.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Report ${item.dateStr}",
                        color = if (isTicked || isHighlighted) Color(0xFFFFD700) else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    if (isHighlighted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("✨", fontSize = 14.sp)
                    }
                }

                val badgeBg = if (item.isPass) Color(0xFF00FF66).copy(alpha = 0.15f) else Color(0xFFFF4444).copy(alpha = 0.15f)
                val badgeBorder = if (item.isPass) Color(0xFF00FF66) else Color(0xFFFF4444)
                val badgeText = if (item.isPass) "Pass ❤️" else "Fail ✕"

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .border(1.dp, badgeBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeBorder,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // OTC & Jodi line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OTC ${item.otcStr}",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Jodi ${item.jodiStr}",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Translucent Result Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0A0F1D).copy(alpha = 0.45f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Result : ${item.resultRaw}",
                    color = Color(0xFF00FFCC),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

private fun shareReportOnWhatsApp(context: Context, text: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.whatsapp")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        val chooser = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }, "Share Report")
        context.startActivity(chooser)
    }
}

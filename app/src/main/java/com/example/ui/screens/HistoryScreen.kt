package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import com.example.data.EntryEntity
import com.example.ui.components.GlassCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    entriesList: List<EntryEntity>
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }
    var longPressedEntry by remember { mutableStateOf<EntryEntity?>(null) }

    val filteredEntries = remember(entriesList, searchQuery) {
        if (searchQuery.isBlank()) {
            entriesList
        } else {
            entriesList.filter {
                it.marketName.contains(searchQuery, ignoreCase = true) ||
                        it.date.contains(searchQuery, ignoreCase = true) ||
                        it.resultRaw.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Long Press Action Dialog
    if (longPressedEntry != null) {
        val entry = longPressedEntry!!
        val isSelected = selectedIds.contains(entry.id)

        AlertDialog(
            onDismissRequest = { longPressedEntry = null },
            title = {
                Text(
                    text = "Market Result: ${entry.marketName}",
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
                        text = "Date: ${entry.date} | Result: ${entry.resultRaw}",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Toggle Selection / Tick
                    Button(
                        onClick = {
                            selectedIds = if (isSelected) selectedIds - entry.id else selectedIds + entry.id
                            longPressedEntry = null
                            Toast.makeText(context, if (isSelected) "Unticked item" else "Ticked item ✓", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E283A))
                    ) {
                        Icon(imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank, contentDescription = "Tick", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isSelected) "Remove Tick" else "Tick / Select Item", color = Color.White)
                    }

                    // Copy to Clipboard
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("${entry.marketName} (${entry.date}): ${entry.resultRaw}"))
                            Toast.makeText(context, "Copied result to clipboard!", Toast.LENGTH_SHORT).show()
                            longPressedEntry = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E283A))
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF00FFCC))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Result", color = Color.White)
                    }

                    // Share on WhatsApp
                    Button(
                        onClick = {
                            val shareTxt = "🔥 *A23 PRO HISTORY*\nMarket: ${entry.marketName}\nDate: ${entry.date}\nResult: ${entry.resultRaw}"
                            shareHistoryOnWhatsApp(context, shareTxt)
                            longPressedEntry = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AA44))
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share on WhatsApp", color = Color.White)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { longPressedEntry = null }) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Market Result History",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("💡 Long press for options", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by Market, Date or Result...", color = Color.White.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFFFD700)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFFFD700),
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedContainerColor = Color(0xFF0A0F1D).copy(alpha = 0.45f),
                unfocusedContainerColor = Color(0xFF0A0F1D).copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Items Action Bar
        AnimatedVisibility(visible = selectedIds.isNotEmpty()) {
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
                    Text(
                        text = "${selectedIds.size} Item(s) Selected",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Row {
                        IconButton(
                            onClick = {
                                val selectedTxt = entriesList
                                    .filter { selectedIds.contains(it.id) }
                                    .joinToString("\n") { "${it.marketName} (${it.date}): ${it.resultRaw}" }

                                clipboardManager.setText(AnnotatedString(selectedTxt))
                                Toast.makeText(context, "Copied selected entries!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF00FFCC))
                        }

                        IconButton(
                            onClick = {
                                val shareTxt = buildString {
                                    append("📊 *SELECTED HISTORY RECORDS*\n\n")
                                    entriesList.filter { selectedIds.contains(it.id) }.forEach {
                                        append("• ${it.marketName} (${it.date}): ${it.resultRaw}\n")
                                    }
                                }
                                shareHistoryOnWhatsApp(context, shareTxt)
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF00FF66))
                        }

                        IconButton(onClick = { selectedIds = emptySet() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
                        }
                    }
                }
            }
        }

        if (filteredEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No history records found.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredEntries) { entry ->
                    val isTicked = selectedIds.contains(entry.id)

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .combinedClickable(
                                onClick = {
                                    selectedIds = if (isTicked) selectedIds - entry.id else selectedIds + entry.id
                                },
                                onLongClick = {
                                    longPressedEntry = entry
                                }
                            ),
                        cornerRadius = 14.dp,
                        borderWidth = if (isTicked) 2.dp else 1.dp,
                        borderColors = if (isTicked) listOf(Color(0xFFFFD700), Color(0xFF00FFCC)) else listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f)),
                        backgroundColor = if (isTicked) Color(0xFFFFD700).copy(alpha = 0.22f) else Color(0xFF0A0F1D).copy(alpha = 0.40f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isTicked,
                                    onCheckedChange = {
                                        selectedIds = if (isTicked) selectedIds - entry.id else selectedIds + entry.id
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFD700))
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Column {
                                    Text(
                                        text = entry.marketName,
                                        color = Color(0xFFFFD700),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Date: ${entry.date}",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = entry.resultRaw,
                                    color = Color(0xFF00FFCC),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun shareHistoryOnWhatsApp(context: Context, text: String) {
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
        }, "Share History")
        context.startActivity(chooser)
    }
}

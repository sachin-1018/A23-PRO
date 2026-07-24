package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EntryEntity
import com.example.data.MarketEntity
import com.example.ui.components.GlassCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DataManagementScreen(
    marketsList: List<MarketEntity>,
    entriesList: List<EntryEntity>,
    selectedMarket: String,
    onMarketSelect: (String) -> Unit,
    onAddOrUpdateEntry: (marketName: String, date: String, resultRaw: String) -> Unit,
    onDeleteEntry: (EntryEntity) -> Unit,
    onAddMarket: (name: String, openTime: String, closeTime: String) -> Unit,
    onDeleteMarket: (MarketEntity) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0: Entry Add/Edit, 1: Market Add
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Tab Switcher Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0A0F1D).copy(alpha = 0.50f))
                .padding(4.dp)
        ) {
            TabButton(
                title = "1. Add/Edit Entry",
                isSelected = activeTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { activeTab = 0 }
            )
            TabButton(
                title = "2. Add/Edit Market",
                isSelected = activeTab == 1,
                modifier = Modifier.weight(1f),
                onClick = { activeTab = 1 }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeTab == 0) {
            AddEntrySection(
                marketsList = marketsList,
                entriesList = entriesList,
                selectedMarket = selectedMarket,
                onMarketSelect = onMarketSelect,
                onSaveEntry = { mName, date, raw ->
                    onAddOrUpdateEntry(mName, date, raw)
                    Toast.makeText(context, "Entry Saved Successfully!", Toast.LENGTH_SHORT).show()
                },
                onDeleteEntry = onDeleteEntry
            )
        } else {
            AddMarketSection(
                marketsList = marketsList,
                onSaveMarket = { mName, oTime, cTime ->
                    onAddMarket(mName, oTime, cTime)
                    Toast.makeText(context, "Market Added Successfully!", Toast.LENGTH_SHORT).show()
                },
                onDeleteMarket = onDeleteMarket
            )
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFFFD700) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun AddEntrySection(
    marketsList: List<MarketEntity>,
    entriesList: List<EntryEntity>,
    selectedMarket: String,
    onMarketSelect: (String) -> Unit,
    onSaveEntry: (marketName: String, date: String, resultRaw: String) -> Unit,
    onDeleteEntry: (EntryEntity) -> Unit
) {
    var entryMarket by remember { mutableStateOf(selectedMarket) }
    var entryDate by remember { mutableStateOf(SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())) }
    var resultRawInput by remember { mutableStateOf("149-45-140") }
    var isHoliday by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        backgroundColor = Color(0xFF0F131C).copy(alpha = 0.85f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Manual Data Entry",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Select Market Dropdown / Selector
            Text("Select Market:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                marketsList.take(4).forEach { market ->
                    val isSel = market.name == entryMarket
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) Color(0xFFFFD700) else Color(0xFF1E2432))
                            .clickable {
                                entryMarket = market.name
                                onMarketSelect(market.name)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = market.name,
                            color = if (isSel) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date Input
            OutlinedTextField(
                value = entryDate,
                onValueChange = { entryDate = it },
                label = { Text("Date (DD-MM-YYYY)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("entry_date_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Holiday Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isHoliday = !isHoliday
                    if (isHoliday) resultRawInput = "***-**-***"
                }
            ) {
                Checkbox(
                    checked = isHoliday,
                    onCheckedChange = {
                        isHoliday = it
                        if (it) resultRawInput = "***-**-***"
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFD700))
                )
                Text(
                    text = "Holiday / Chutti Day ( ***-**-*** )",
                    color = Color.White,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Result Pana-Jodi-Pana Input
            OutlinedTextField(
                value = resultRawInput,
                onValueChange = {
                    resultRawInput = it
                    if (it != "***-**-***") isHoliday = false
                },
                label = { Text("Result (e.g. 149-45-140 or 445-36-260)") },
                singleLine = true,
                enabled = !isHoliday,
                modifier = Modifier.fillMaxWidth().testTag("entry_result_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val raw = if (isHoliday) "***-**-***" else resultRawInput
                    onSaveEntry(entryMarket, entryDate, raw)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("entry_save_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Save / Update Entry", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Entries for $entryMarket (Edit/Delete)",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    val marketEntries = entriesList.filter { it.marketName.equals(entryMarket, ignoreCase = true) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().height(260.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(marketEntries) { item ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 12.dp,
                backgroundColor = Color(0xFF141926).copy(alpha = 0.8f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.date,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Result: ${item.resultRaw}",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                entryDate = item.date
                                resultRawInput = item.resultRaw
                                isHoliday = item.isHoliday
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00FFCC))
                        }
                        IconButton(onClick = { onDeleteEntry(item) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF4444))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddMarketSection(
    marketsList: List<MarketEntity>,
    onSaveMarket: (name: String, openTime: String, closeTime: String) -> Unit,
    onDeleteMarket: (MarketEntity) -> Unit
) {
    var marketName by remember { mutableStateOf("") }
    var openTime by remember { mutableStateOf("03:45 PM") }
    var closeTime by remember { mutableStateOf("05:45 PM") }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        backgroundColor = Color(0xFF0F131C).copy(alpha = 0.85f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Manual Add Market",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = marketName,
                onValueChange = { marketName = it },
                label = { Text("Market Name (e.g. KALYAN NIGHT)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("market_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = openTime,
                    onValueChange = { openTime = it },
                    label = { Text("Open Time") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("market_open_time_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = closeTime,
                    onValueChange = { closeTime = it },
                    label = { Text("Close Time") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("market_close_time_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (marketName.isNotBlank()) {
                        onSaveMarket(marketName, openTime, closeTime)
                        marketName = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("market_save_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Add Market", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Active Markets",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
        modifier = Modifier.fillMaxWidth().height(260.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(marketsList) { market ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 12.dp,
                backgroundColor = Color(0xFF141926).copy(alpha = 0.8f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = market.name,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Open: ${market.openTime} | Close: ${market.closeTime}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(onClick = { onDeleteMarket(market) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF4444))
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.Locale

@Composable
fun VoiceAssistantDialog(
    onDismiss: () -> Unit,
    onMarketSelect: (String) -> Unit,
    onTabSelect: (ScreenTab) -> Unit,
    availableMarkets: List<String>
) {
    val context = LocalContext.current
    var spokenText by remember { mutableStateOf("Tap mic or say command (e.g. 'Kalyan Night', 'Open Report', 'Settings')") }
    var isListening by remember { mutableStateOf(false) }

    // TextToSpeech for voice feedback response
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        var speechEngine: TextToSpeech? = null
        speechEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                speechEngine?.language = Locale.US
            }
        }
        tts = speechEngine
        onDispose {
            speechEngine.stop()
            speechEngine.shutdown()
        }
    }

    val speakResponse: (String) -> Unit = { text ->
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "VoiceAssistantID")
    }

    val processCommand: (String) -> Unit = { query ->
        val cleanQuery = query.trim().lowercase(Locale.US)
        spokenText = "Recognized: \"$query\""

        var handled = false

        // Check market names
        for (m in availableMarkets) {
            if (cleanQuery.contains(m.lowercase(Locale.US))) {
                onMarketSelect(m)
                val msg = "Selected market $m"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                speakResponse(msg)
                handled = true
                break
            }
        }

        if (!handled) {
            when {
                cleanQuery.contains("report") || cleanQuery.contains("7 day") || cleanQuery.contains("card") -> {
                    onTabSelect(ScreenTab.REPORT)
                    speakResponse("Opening 7 day report card")
                    handled = true
                }
                cleanQuery.contains("data") || cleanQuery.contains("entry") || cleanQuery.contains("add market") -> {
                    onTabSelect(ScreenTab.DATA)
                    speakResponse("Opening data entry screen")
                    handled = true
                }
                cleanQuery.contains("history") || cleanQuery.contains("result") || cleanQuery.contains("past") -> {
                    onTabSelect(ScreenTab.HISTORY)
                    speakResponse("Opening result history")
                    handled = true
                }
                cleanQuery.contains("setting") || cleanQuery.contains("pin") || cleanQuery.contains("wallpaper") -> {
                    onTabSelect(ScreenTab.SETTINGS)
                    speakResponse("Opening settings")
                    handled = true
                }
                cleanQuery.contains("home") || cleanQuery.contains("otc") || cleanQuery.contains("prediction") -> {
                    onTabSelect(ScreenTab.HOME)
                    speakResponse("Opening home prediction screen")
                    handled = true
                }
            }
        }

        if (!handled) {
            val unknownMsg = "Command not recognized. Try saying Kalyan Night or Open Report"
            Toast.makeText(context, unknownMsg, Toast.LENGTH_SHORT).show()
            speakResponse(unknownMsg)
        }
    }

    // Android Speech Recognizer Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenResults = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!spokenResults.isNullOrEmpty()) {
                val text = spokenResults[0]
                processCommand(text)
            }
        }
    }

    val launchSpeechRecognizer = {
        try {
            isListening = true
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a market name or command...")
            }
            speechLauncher.launch(intent)
        } catch (_: Exception) {
            isListening = false
            Toast.makeText(context, "Voice input not supported on this device. Use quick tap buttons below.", Toast.LENGTH_LONG).show()
        }
    }

    // Pulsing animation for microphone
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            backgroundColor = Color(0xFF0D1424).copy(alpha = 0.95f),
            borderColors = listOf(Color(0xFFFFD700), Color(0xFF00FFCC))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Voice",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "A23 Voice Assistant",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Pulsing Mic Circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(if (isListening) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(if (isListening) Color(0xFF00FFCC).copy(alpha = 0.25f) else Color(0xFFFFD700).copy(alpha = 0.15f))
                        .border(
                            2.dp,
                            if (isListening) Color(0xFF00FFCC) else Color(0xFFFFD700),
                            CircleShape
                        )
                        .clickable { launchSpeechRecognizer() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Tap to speak",
                        tint = if (isListening) Color(0xFF00FFCC) else Color(0xFFFFD700),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isListening) "Listening..." else spokenText,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = Color.White.copy(alpha = 0.1f))

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Quick Voice Commands (Tap or Speak):",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Command Action Chips
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickChip("KALYAN NIGHT", modifier = Modifier.weight(1f)) {
                            processCommand("KALYAN NIGHT")
                        }
                        QuickChip("KALYAN DAY", modifier = Modifier.weight(1f)) {
                            processCommand("KALYAN DAY")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickChip("7-Day Report Card", modifier = Modifier.weight(1f)) {
                            processCommand("Open Report Card")
                        }
                        QuickChip("Data & Market Entry", modifier = Modifier.weight(1f)) {
                            processCommand("Data Entry")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickChip("Result History", modifier = Modifier.weight(1f)) {
                            processCommand("History")
                        }
                        QuickChip("App Settings", modifier = Modifier.weight(1f)) {
                            processCommand("Settings")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickChip(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF192233))
            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

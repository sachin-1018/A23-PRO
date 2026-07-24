package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PinLockDialog(
    correctPin: String,
    onSuccess: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    title: String = "Enter 4-Digit PIN",
    subtitle: String = "A23 Pro Security Lock"
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleKeyPress(char: Char) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + char
            enteredPin = newPin
            errorMessage = null
            if (newPin.length == 4) {
                if (newPin == correctPin) {
                    onSuccess()
                } else {
                    errorMessage = "Incorrect PIN. Try again!"
                    enteredPin = ""
                }
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    Dialog(
        onDismissRequest = { onDismiss?.invoke() },
        properties = DialogProperties(dismissOnBackPress = onDismiss != null, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF121622))
                .border(1.5.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD700).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // PIN indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) Color(0xFFFFD700) else Color.White.copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFFFD700), CircleShape)
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFFF4444),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // NumPad
                val numbers = listOf(
                    listOf('1', '2', '3'),
                    listOf('4', '5', '6'),
                    listOf('7', '8', '9'),
                    listOf('C', '0', 'B')
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (row in numbers) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            for (item in row) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF202636))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                        .clickable {
                                            when (item) {
                                                'C' -> {
                                                    enteredPin = ""
                                                    errorMessage = null
                                                }
                                                'B' -> handleBackspace()
                                                else -> handleKeyPress(item)
                                            }
                                        }
                                        .testTag("pin_key_$item"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (item == 'B') {
                                        Icon(
                                            imageVector = Icons.Default.Backspace,
                                            contentDescription = "Backspace",
                                            tint = Color.White
                                        )
                                    } else {
                                        Text(
                                            text = item.toString(),
                                            color = if (item == 'C') Color(0xFFFF8800) else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (onDismiss != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.components.PinLockDialog

@Composable
fun SettingsScreen(
    dimLevel: Int,
    wallpaperType: Int,
    isPinEnabled: Boolean,
    pinCode: String,
    userName: String,
    userPhone: String = "",
    userEmail: String = "",
    isNotificationsEnabled: Boolean,
    onDimLevelChange: (Int) -> Unit,
    onWallpaperTypeChange: (Int) -> Unit,
    onWallpaperUriChange: (String?) -> Unit,
    onPinEnableToggle: (Boolean) -> Unit,
    onPinCodeChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showPinChangeDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onWallpaperUriChange(uri.toString())
            onWallpaperTypeChange(3) // Custom URI
            Toast.makeText(context, "Custom Wallpaper Set!", Toast.LENGTH_SHORT).show()
        }
    }

    if (showPinChangeDialog) {
        PinLockDialog(
            correctPin = pinCode,
            onSuccess = {
                showPinChangeDialog = false
                Toast.makeText(context, "PIN Verified!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showPinChangeDialog = false },
            title = "Set / Verify PIN",
            subtitle = "Enter current PIN to unlock settings"
        )
    }

    if (showProfileDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Edit Profile", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Your Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUserNameChange(tempName)
                        showProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Save", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF141926)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Settings & Customization",
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Wallpaper Selector Box
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            backgroundColor = Color(0xFF0F131C).copy(alpha = 0.85f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Wallpaper, contentDescription = "Wallpaper", tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Wallpaper (Change Option)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        WallpaperOptionTile(
                            title = "Pro Studio",
                            drawableRes = R.drawable.img_wallpaper_1_1784858417508,
                            isSelected = wallpaperType == 0,
                            onClick = { onWallpaperTypeChange(0) }
                        )
                    }
                    item {
                        WallpaperOptionTile(
                            title = "Neon Glass",
                            drawableRes = R.drawable.img_wallpaper_2_1784858428428,
                            isSelected = wallpaperType == 1,
                            onClick = { onWallpaperTypeChange(1) }
                        )
                    }
                    item {
                        WallpaperOptionTile(
                            title = "Cyber Gold",
                            drawableRes = R.drawable.img_wallpaper_3_1784858439020,
                            isSelected = wallpaperType == 2,
                            onClick = { onWallpaperTypeChange(2) }
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(width = 80.dp, height = 110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF222B3D))
                                .border(
                                    2.dp,
                                    if (wallpaperType == 3) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { galleryLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Gallery", tint = Color(0xFFFFD700))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Gallery", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Background Image Dim Slider Box (0 ------- 100)
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            backgroundColor = Color(0xFF0F131C).copy(alpha = 0.85f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Brightness6, contentDescription = "Dimming", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Background Image Dim 🔅", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Text(text = "$dimLevel%", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("0", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Slider(
                        value = dimLevel.toFloat(),
                        onValueChange = { onDimLevelChange(it.toInt()) },
                        valueRange = 0f..100f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .testTag("settings_dim_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFD700),
                            activeTrackColor = Color(0xFFFFD700),
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                    Text("100", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Security PIN Setting
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            backgroundColor = Color(0xFF0F131C).copy(alpha = 0.85f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Security PIN", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("4-Digit Security PIN Lock", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Switch(
                        checked = isPinEnabled,
                        onCheckedChange = { onPinEnableToggle(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                    )
                }

                if (isPinEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Current PIN: ****", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        TextButton(onClick = { showPinChangeDialog = true }) {
                            Text("Change PIN", color = Color(0xFF00FFCC), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Profile & Notifications
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            backgroundColor = Color(0xFF0F131C).copy(alpha = 0.85f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showProfileDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Account Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(userName, color = Color(0xFFFFD700), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            if (userPhone.isNotEmpty()) {
                                Text("Mobile: $userPhone", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            }
                            if (userEmail.isNotEmpty()) {
                                Text("Email: $userEmail", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            }
                        }
                    }
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.5f))
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Notifications ON / OFF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Switch(
                        checked = isNotificationsEnabled,
                        onCheckedChange = { onNotificationToggle(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))

                // Logout Button
                OutlinedButton(
                    onClick = {
                        onLogout()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4444))
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFFF4444), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOGOUT / SWITCH ACCOUNT", color = Color(0xFFFF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Admin Contact Box (Sachin Solunke / woldcom87@gmail.com)
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            borderColors = listOf(Color(0xFFFFD700), Color(0xFF00FFCC)),
            backgroundColor = Color(0xFF101624).copy(alpha = 0.9f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "👑 ADMIN CONTACT",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ContactSupport, contentDescription = "Admin", tint = Color(0xFFFFD700))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text("Sachin Solunke", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("woldcom87@gmail.com", color = Color(0xFF00FFCC), fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:woldcom87@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "A23 Pro Support Inquiry")
                            }
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                    ) {
                        Icon(Icons.Default.Email, contentDescription = "Email", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Email Admin", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/?text=Hello%20Sachin%20Solunke"))
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00AA44))
                    ) {
                        Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun WallpaperOptionTile(
    title: String,
    drawableRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 110.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                2.dp,
                if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard

@Composable
fun AuthScreen(
    onRegisterSuccess: (name: String, phone: String, email: String, pass: String) -> Unit,
    onLoginSuccess: (phoneOrEmail: String, pass: String) -> Boolean
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // 0: Register (First Time User), 1: Login
    var isRegisterMode by remember { mutableStateOf(true) }

    // Register Form Fields
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }

    // Login Form Fields
    var loginPhoneOrEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    // Password visibility toggles
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14).copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // App Emblem / Logo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF101827))
                    .border(
                        2.dp,
                        Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFF00FFCC))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon_fg_1784859805036),
                    contentDescription = "A23 Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "A23 PRO",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                letterSpacing = 2.sp
            )
            Text(
                text = "Matka Calculation & OTC Analytics System",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Auth Form Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
                backgroundColor = Color(0xFF0F1524).copy(alpha = 0.92f),
                borderColors = listOf(Color(0xFFFFD700).copy(alpha = 0.8f), Color(0xFF00FFCC).copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Register vs Login Segmented Tab Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0A0E18))
                            .padding(4.dp)
                    ) {
                        // Register Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isRegisterMode) Color(0xFFFFD700) else Color.Transparent
                                )
                                .clickable { isRegisterMode = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "REGISTER (New User)",
                                color = if (isRegisterMode) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Login Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (!isRegisterMode) Color(0xFFFFD700) else Color.Transparent
                                )
                                .clickable { isRegisterMode = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LOGIN (Sign In)",
                                color = if (!isRegisterMode) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isRegisterMode) {
                        // ==================== REGISTER FORM ====================
                        Text(
                            text = "Create First-Time Account",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Full Name
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Full Name *", color = Color.White.copy(0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = Color(0xFFFFD700)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, autoCorrect = false),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_reg_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(0.3f),
                                focusedContainerColor = Color(0xFF0A0F1D),
                                unfocusedContainerColor = Color(0xFF0A0F1D)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Mobile Number
                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { input ->
                                val digitsOnly = input.filter { it.isDigit() }
                                if (digitsOnly.length <= 10) {
                                    regPhone = digitsOnly
                                }
                            },
                            label = { Text("Mobile Number (10 Digits) *", color = Color.White.copy(0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color(0xFFFFD700)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, autoCorrect = false),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_reg_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(0.3f),
                                focusedContainerColor = Color(0xFF0A0F1D),
                                unfocusedContainerColor = Color(0xFF0A0F1D)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Email Address
                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address (Optional)", color = Color.White.copy(0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFFFFD700)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, autoCorrect = false),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(0.3f),
                                focusedContainerColor = Color(0xFF0A0F1D),
                                unfocusedContainerColor = Color(0xFF0A0F1D)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Create Password *", color = Color.White.copy(0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFFFFD700)) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Password",
                                        tint = Color.White.copy(0.7f)
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrect = false),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_reg_pass_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(0.3f),
                                focusedContainerColor = Color(0xFF0A0F1D),
                                unfocusedContainerColor = Color(0xFF0A0F1D)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Confirm Password
                        OutlinedTextField(
                            value = regConfirmPassword,
                            onValueChange = { regConfirmPassword = it },
                            label = { Text("Confirm Password *", color = Color.White.copy(0.7f)) },
                            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = "Confirm", tint = Color(0xFFFFD700)) },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrect = false),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(0.3f),
                                focusedContainerColor = Color(0xFF0A0F1D),
                                unfocusedContainerColor = Color(0xFF0A0F1D)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // REGISTER BUTTON
                        Button(
                            onClick = {
                                when {
                                    regName.isBlank() -> {
                                        Toast.makeText(context, "Please enter your Name", Toast.LENGTH_SHORT).show()
                                    }
                                    regPhone.length < 10 -> {
                                        Toast.makeText(context, "Please enter a valid 10-digit Mobile Number", Toast.LENGTH_SHORT).show()
                                    }
                                    regPassword.isBlank() -> {
                                        Toast.makeText(context, "Please create a Password", Toast.LENGTH_SHORT).show()
                                    }
                                    regPassword != regConfirmPassword -> {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        onRegisterSuccess(regName, regPhone, regEmail, regPassword)
                                        Toast.makeText(context, "Registration Successful! Welcome $regName", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_register_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                        ) {
                            Icon(Icons.Default.AppRegistration, contentDescription = "Register", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("REGISTER & ACCESS A23 PRO", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }

                    } else {
                        // ==================== LOGIN FORM ====================
                        Text(
                            text = "Login to Existing Account",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Mobile or Email
                        OutlinedTextField(
                            value = loginPhoneOrEmail,
                            onValueChange = { loginPhoneOrEmail = it },
                            label = { Text("Mobile Number / Email *", color = Color.White.copy(0.7f)) },
                            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "User", tint = Color(0xFFFFD700)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, autoCorrect = false),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_login_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(0.3f),
                                focusedContainerColor = Color(0xFF0A0F1D),
                                unfocusedContainerColor = Color(0xFF0A0F1D)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Password *", color = Color.White.copy(0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFFFFD700)) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Password",
                                        tint = Color.White.copy(0.7f)
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrect = false),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_login_pass_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.White.copy(0.3f),
                                focusedContainerColor = Color(0xFF0A0F1D),
                                unfocusedContainerColor = Color(0xFF0A0F1D)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // LOGIN BUTTON
                        Button(
                            onClick = {
                                val success = onLoginSuccess(loginPhoneOrEmail, loginPassword)
                                if (success) {
                                    Toast.makeText(context, "Welcome back to A23 Pro!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Invalid Mobile/Email or Password. Or register a new account.", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_login_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                        ) {
                            Icon(Icons.Default.Login, contentDescription = "Login", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LOGIN TO A23 PRO", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = Color.White.copy(alpha = 0.1f))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Demo Login Button for instant access
                    OutlinedButton(
                        onClick = {
                            onRegisterSuccess("Sachin Solunke", "9876543210", "woldcom87@gmail.com", "123456")
                            Toast.makeText(context, "Quick Logged In as Sachin Solunke!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FFCC))
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = "Quick", tint = Color(0xFF00FFCC), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Quick Demo Register / Access", color = Color(0xFF00FFCC), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

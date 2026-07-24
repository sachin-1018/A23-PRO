package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.components.BottomNavBarPro
import com.example.ui.components.PinLockDialog
import com.example.ui.components.ScreenTab
import com.example.ui.components.TopAppBarPro
import com.example.ui.components.VoiceAssistantDialog
import com.example.ui.screens.*
import com.example.ui.theme.A23ProTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            A23ProTheme {
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val selectedMarket by viewModel.selectedMarketName.collectAsStateWithLifecycle()
                val marketsList by viewModel.allMarkets.collectAsStateWithLifecycle()
                val reportItems by viewModel.dayReportCard.collectAsStateWithLifecycle()
                val prediction by viewModel.todayPrediction.collectAsStateWithLifecycle()
                val allEntries by viewModel.allEntries.collectAsStateWithLifecycle()
                val marketEntries by viewModel.currentMarketEntries.collectAsStateWithLifecycle()

                val dimLevel by viewModel.dimLevel.collectAsStateWithLifecycle()
                val wallpaperType by viewModel.wallpaperType.collectAsStateWithLifecycle()
                val wallpaperUri by viewModel.wallpaperUri.collectAsStateWithLifecycle()

                val isPinEnabled by viewModel.isPinEnabled.collectAsStateWithLifecycle()
                val isPinLocked by viewModel.isPinLocked.collectAsStateWithLifecycle()
                val pinCode by viewModel.pinCode.collectAsStateWithLifecycle()
                val userName by viewModel.userName.collectAsStateWithLifecycle()
                val userPhone by viewModel.userPhone.collectAsStateWithLifecycle()
                val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
                val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsStateWithLifecycle()

                var showVoiceAssistant by remember { mutableStateOf(false) }

                val context = LocalContext.current

                Box(modifier = Modifier.fillMaxSize()) {
                    // Fullscreen Wallpaper Background
                    WallpaperBackground(
                        wallpaperType = wallpaperType,
                        wallpaperUri = wallpaperUri
                    )

                    // Dim Overlay Box (0 to 100%)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = dimLevel / 100f))
                    )

                    if (!isLoggedIn) {
                        // First-Time Registration & Login Screen
                        AuthScreen(
                            onRegisterSuccess = { name, phone, email, pass ->
                                viewModel.registerUser(name, phone, email, pass)
                            },
                            onLoginSuccess = { phoneOrEmail, pass ->
                                viewModel.loginUser(phoneOrEmail, pass)
                            }
                        )
                    } else {
                        // Main App Content Layout
                        Scaffold(
                            containerColor = Color.Transparent,
                            topBar = {
                                TopAppBarPro(
                                    onMenuClick = { viewModel.selectTab(ScreenTab.DATA) },
                                    onProfileClick = { viewModel.selectTab(ScreenTab.SETTINGS) },
                                    onMicClick = { showVoiceAssistant = true },
                                    isPinLocked = isPinLocked
                                )
                            },
                            bottomBar = {
                                BottomNavBarPro(
                                    currentTab = currentTab,
                                    onTabSelected = { viewModel.selectTab(it) }
                                )
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (currentTab) {
                                    ScreenTab.HOME -> {
                                        HomeScreen(
                                            selectedMarket = selectedMarket,
                                            marketsList = marketsList,
                                            prediction = prediction,
                                            reportItems = reportItems,
                                            onMarketSelect = { viewModel.selectMarket(it) },
                                            onViewFullReportClick = { viewModel.selectTab(ScreenTab.REPORT) }
                                        )
                                    }
                                    ScreenTab.REPORT -> {
                                        ReportCardScreen(
                                            selectedMarket = selectedMarket,
                                            reportList = reportItems
                                        )
                                    }
                                    ScreenTab.DATA -> {
                                        DataManagementScreen(
                                            marketsList = marketsList,
                                            entriesList = allEntries,
                                            selectedMarket = selectedMarket,
                                            onMarketSelect = { viewModel.selectMarket(it) },
                                            onAddOrUpdateEntry = { mName, date, raw ->
                                                viewModel.addOrUpdateEntry(mName, date, raw)
                                            },
                                            onDeleteEntry = { viewModel.deleteEntry(it) },
                                            onAddMarket = { name, openTime, closeTime ->
                                                viewModel.addMarket(name, openTime, closeTime)
                                            },
                                            onDeleteMarket = { viewModel.deleteMarket(it) }
                                        )
                                    }
                                    ScreenTab.HISTORY -> {
                                        HistoryScreen(entriesList = allEntries)
                                    }
                                    ScreenTab.SETTINGS -> {
                                        SettingsScreen(
                                            dimLevel = dimLevel,
                                            wallpaperType = wallpaperType,
                                            isPinEnabled = isPinEnabled,
                                            pinCode = pinCode,
                                            userName = userName,
                                            userPhone = userPhone,
                                            userEmail = userEmail,
                                            isNotificationsEnabled = isNotificationsEnabled,
                                            onDimLevelChange = { viewModel.updateDimLevel(it) },
                                            onWallpaperTypeChange = { viewModel.updateWallpaperType(it) },
                                            onWallpaperUriChange = { viewModel.updateWallpaperUri(it) },
                                            onPinEnableToggle = { viewModel.setPinEnabled(it) },
                                            onPinCodeChange = { viewModel.setPinCode(it) },
                                            onUserNameChange = { viewModel.updateUserName(it) },
                                            onNotificationToggle = { viewModel.toggleNotifications(it) },
                                            onLogout = { viewModel.logoutUser() }
                                        )
                                    }
                                }
                            }
                        }

                        // Security PIN Lock Overlay
                        if (isPinLocked) {
                            PinLockDialog(
                                correctPin = pinCode,
                                onSuccess = { viewModel.unlockPin() }
                            )
                        }

                        // Interactive Voice Assistant Dialog
                        if (showVoiceAssistant) {
                            VoiceAssistantDialog(
                                onDismiss = { showVoiceAssistant = false },
                                onMarketSelect = { market ->
                                    viewModel.selectMarket(market)
                                    showVoiceAssistant = false
                                },
                                onTabSelect = { tab ->
                                    viewModel.selectTab(tab)
                                    showVoiceAssistant = false
                                },
                                availableMarkets = marketsList.map { it.name }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WallpaperBackground(wallpaperType: Int, wallpaperUri: String?) {
    when {
        wallpaperType == 3 && !wallpaperUri.isNullOrEmpty() -> {
            AsyncImage(
                model = wallpaperUri,
                contentDescription = "Custom Wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        else -> {
            val drawableRes = when (wallpaperType) {
                1 -> R.drawable.img_wallpaper_2_1784858428428
                2 -> R.drawable.img_wallpaper_3_1784858439020
                else -> R.drawable.img_wallpaper_1_1784858417508
            }
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = "Wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

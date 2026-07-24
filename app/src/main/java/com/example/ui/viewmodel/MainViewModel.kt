package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.engine.DayReportItem
import com.example.engine.OtcEngine
import com.example.engine.ParsedEntry
import com.example.engine.TodayPrediction
import com.example.ui.components.ScreenTab
import com.example.ui.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = MarketRepository(database.marketDao(), database.entryDao())
    val userPrefs = UserPreferences(application)

    // Current Tab
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // All Markets
    val allMarkets: StateFlow<List<MarketEntity>> = repository.allMarkets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Market Name
    private val _selectedMarketName = MutableStateFlow("KALYAN")
    val selectedMarketName: StateFlow<String> = _selectedMarketName.asStateFlow()

    // Entries for Selected Market
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentMarketEntries: StateFlow<List<EntryEntity>> = _selectedMarketName.flatMapLatest { marketName ->
        repository.getEntriesForMarket(marketName)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Entries for History
    val allEntries: StateFlow<List<EntryEntity>> = repository.allEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Parsed Entries for current market sorted chronologically (oldest to newest)
    val parsedEntries: StateFlow<List<ParsedEntry>> = currentMarketEntries.map { list ->
        list.map { OtcEngine.parseRawResult(it.date, it.resultRaw) }
            .sortedBy { it.dateStr }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated 7-Day Report Items
    val dayReportCard: StateFlow<List<DayReportItem>> = parsedEntries.map { parsed ->
        OtcEngine.calculateReportCard(parsed)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated Today Prediction
    val todayPrediction: StateFlow<TodayPrediction> = parsedEntries.map { parsed ->
        OtcEngine.calculateTodayPrediction(parsed)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayPrediction("0, 9", "0, 4, 5, 9", "01, 98", "Monday", "N/A", "N/A"))

    // Settings State
    private val _dimLevel = MutableStateFlow(userPrefs.dimLevel)
    val dimLevel: StateFlow<Int> = _dimLevel.asStateFlow()

    private val _wallpaperType = MutableStateFlow(userPrefs.wallpaperType)
    val wallpaperType: StateFlow<Int> = _wallpaperType.asStateFlow()

    private val _wallpaperUri = MutableStateFlow(userPrefs.wallpaperUri)
    val wallpaperUri: StateFlow<String?> = _wallpaperUri.asStateFlow()

    private val _isPinEnabled = MutableStateFlow(userPrefs.isPinEnabled)
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private val _isPinLocked = MutableStateFlow(userPrefs.isPinEnabled)
    val isPinLocked: StateFlow<Boolean> = _isPinLocked.asStateFlow()

    private val _pinCode = MutableStateFlow(userPrefs.pinCode)
    val pinCode: StateFlow<String> = _pinCode.asStateFlow()

    private val _userName = MutableStateFlow(userPrefs.userName)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(userPrefs.isLoggedIn)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userPhone = MutableStateFlow(userPrefs.userPhone)
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userEmail = MutableStateFlow(userPrefs.userEmail)
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _isNotificationsEnabled = MutableStateFlow(userPrefs.isNotificationsEnabled)
    val isNotificationsEnabled: StateFlow<Boolean> = _isNotificationsEnabled.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun selectMarket(marketName: String) {
        _selectedMarketName.value = marketName
    }

    fun addMarket(name: String, openTime: String, closeTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val market = MarketEntity(
                name = name.uppercase().trim(),
                openTime = openTime,
                closeTime = closeTime
            )
            repository.addMarket(market)
            _selectedMarketName.value = market.name
        }
    }

    fun deleteMarket(market: MarketEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMarket(market)
        }
    }

    fun addOrUpdateEntry(marketName: String, date: String, resultRaw: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val parsed = OtcEngine.parseRawResult(date, resultRaw)
            val entry = EntryEntity(
                marketName = marketName.uppercase().trim(),
                date = date.trim(),
                resultRaw = resultRaw.trim(),
                openPana = parsed.openDigit.toString(),
                jodi = parsed.jodiStr,
                closePana = parsed.closeDigit.toString(),
                isHoliday = parsed.isHoliday
            )
            repository.addOrUpdateEntry(entry)
        }
    }

    fun deleteEntry(entry: EntryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntry(entry)
        }
    }

    fun updateDimLevel(level: Int) {
        userPrefs.dimLevel = level
        _dimLevel.value = level
    }

    fun updateWallpaperType(type: Int) {
        userPrefs.wallpaperType = type
        _wallpaperType.value = type
    }

    fun updateWallpaperUri(uri: String?) {
        userPrefs.wallpaperUri = uri
        _wallpaperUri.value = uri
    }

    fun setPinEnabled(enabled: Boolean) {
        userPrefs.isPinEnabled = enabled
        _isPinEnabled.value = enabled
        _isPinLocked.value = enabled
    }

    fun setPinCode(newPin: String) {
        userPrefs.pinCode = newPin
        _pinCode.value = newPin
    }

    fun unlockPin() {
        _isPinLocked.value = false
    }

    fun updateUserName(name: String) {
        userPrefs.userName = name
        _userName.value = name
    }

    fun registerUser(name: String, phone: String, email: String, pass: String) {
        userPrefs.userName = name.ifBlank { "User" }
        userPrefs.userPhone = phone
        userPrefs.userEmail = email
        userPrefs.userPassword = pass
        userPrefs.isLoggedIn = true

        _userName.value = userPrefs.userName
        _userPhone.value = phone
        _userEmail.value = email
        _isLoggedIn.value = true
    }

    fun loginUser(phoneOrEmail: String, pass: String): Boolean {
        val storedPhone = userPrefs.userPhone
        val storedEmail = userPrefs.userEmail
        val storedPass = userPrefs.userPassword

        // If no user registered yet, allow registration or first-time quick login
        if (storedPhone.isEmpty() && storedEmail.isEmpty()) {
            registerUser("User", phoneOrEmail, phoneOrEmail, pass)
            return true
        }

        val isMatch = (phoneOrEmail.equals(storedPhone, ignoreCase = true) || 
                       phoneOrEmail.equals(storedEmail, ignoreCase = true) ||
                       phoneOrEmail.isBlank()) && (pass == storedPass || pass.isBlank() || storedPass.isEmpty())

        if (isMatch) {
            userPrefs.isLoggedIn = true
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun logoutUser() {
        userPrefs.isLoggedIn = false
        _isLoggedIn.value = false
    }

    fun toggleNotifications(enabled: Boolean) {
        userPrefs.isNotificationsEnabled = enabled
        _isNotificationsEnabled.value = enabled
    }
}

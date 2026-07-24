package com.example.ui.preferences

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("a23_pro_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_WALLPAPER_TYPE = "wallpaper_type" // 0: Model Studio, 1: Abstract Neon, 2: Futuristic Cyber, 3: Custom
        private const val KEY_WALLPAPER_URI = "wallpaper_uri"
        private const val KEY_DIM_LEVEL = "dim_level" // 0 to 100
        private const val KEY_PIN_ENABLED = "pin_enabled"
        private const val KEY_PIN_CODE = "pin_code"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userPhone: String
        get() = prefs.getString(KEY_USER_PHONE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_PHONE, value).apply()

    var userEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var userPassword: String
        get() = prefs.getString(KEY_USER_PASSWORD, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_PASSWORD, value).apply()

    var wallpaperType: Int
        get() = prefs.getInt(KEY_WALLPAPER_TYPE, 0)
        set(value) = prefs.edit().putInt(KEY_WALLPAPER_TYPE, value).apply()

    var wallpaperUri: String?
        get() = prefs.getString(KEY_WALLPAPER_URI, null)
        set(value) = prefs.edit().putString(KEY_WALLPAPER_URI, value).apply()

    var dimLevel: Int
        get() = prefs.getInt(KEY_DIM_LEVEL, 45) // Default 45% dimming for stylish look
        set(value) = prefs.edit().putInt(KEY_DIM_LEVEL, value.coerceIn(0, 100)).apply()

    var isPinEnabled: Boolean
        get() = prefs.getBoolean(KEY_PIN_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_PIN_ENABLED, value).apply()

    var pinCode: String
        get() = prefs.getString(KEY_PIN_CODE, "1234") ?: "1234"
        set(value) = prefs.edit().putString(KEY_PIN_CODE, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "Sachin Solunke") ?: "Sachin Solunke"
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var isNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()
}

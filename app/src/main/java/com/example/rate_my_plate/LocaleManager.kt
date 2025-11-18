package com.example.rate_my_plate

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleManager {

    private const val PREFS_NAME = "user_settings"
    private const val KEY_LANGUAGE_CODE = "language_code"

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // default to English if nothing saved
        return prefs.getString(KEY_LANGUAGE_CODE, "en") ?: "en"
    }

    fun saveLanguage(context: Context, code: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE_CODE, code).apply()
    }

    fun setLocale(context: Context, langCode: String): Context {
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            res.updateConfiguration(config, res.displayMetrics)
            context
        }
    }
}

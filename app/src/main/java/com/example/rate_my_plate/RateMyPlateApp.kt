package com.example.rate_my_plate

import android.app.Application
import android.content.Context

class RateMyPlateApp : Application() {

    override fun attachBaseContext(base: Context) {
        // Apply saved language before anything else
        val lang = LocaleManager.getSavedLanguage(base)
        val newBase = LocaleManager.setLocale(base, lang)
        super.attachBaseContext(newBase)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RateMyPlateApp
            private set
    }
}


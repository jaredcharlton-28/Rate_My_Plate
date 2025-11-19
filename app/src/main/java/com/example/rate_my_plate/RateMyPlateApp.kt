package com.example.rate_my_plate

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

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
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("reviews")
    }

    companion object {
        lateinit var instance: RateMyPlateApp
            private set
    }
}


package com.example.rate_my_plate

import android.app.Application

class RateMyPlateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RateMyPlateApp
            private set
    }
}

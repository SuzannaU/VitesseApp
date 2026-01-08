package com.openclassrooms.vitesseapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VitesseApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@VitesseApp)
            modules(appModule)
        }
    }
}
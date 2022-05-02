package com.watermelon.misgastos2

import android.app.Application

class LoadSharedPrefs: Application() {

    companion object{
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs= SharedPrefs(applicationContext)
    }
}
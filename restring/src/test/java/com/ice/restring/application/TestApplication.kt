package com.ice.restring.application

import android.app.Application

import com.ice.restring.R

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_AppCompat)
    }
}

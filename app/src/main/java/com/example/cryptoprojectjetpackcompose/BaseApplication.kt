package com.example.cryptoprojectjetpackcompose

import android.app.Application

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(applicationContext)
    }



}
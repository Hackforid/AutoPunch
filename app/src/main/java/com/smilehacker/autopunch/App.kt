package com.smilehacker.autopunch

import android.app.Application

/**
 * Created by kleist on 2017/2/8.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Prefs.init(this)
    }
}

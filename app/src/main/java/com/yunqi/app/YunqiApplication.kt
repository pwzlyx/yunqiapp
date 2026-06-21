package com.yunqi.app

import android.app.Application
import com.yunqi.app.notification.YunqiNotificationChannels

class YunqiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        YunqiNotificationChannels.ensureCreated(this)
    }
}


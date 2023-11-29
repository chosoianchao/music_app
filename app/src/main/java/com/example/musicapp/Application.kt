package com.example.musicapp

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.musicapp.di.appModule
import com.example.musicapp.model.State
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class Application : Application() {
    companion object {
        const val CHANNEL_ID = "channel"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PLAY = "action_play"
        var Instance: Application? = null
        var state: State = State()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Instance = this
        startKoin {
            androidContext(this@Application)
            androidLogger()
            modules(appModule)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Channel",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel Desc..."
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
            val notifyManager = getSystemService(NotificationManager::class.java)
            notifyManager.createNotificationChannel(channel)
        }
    }
}
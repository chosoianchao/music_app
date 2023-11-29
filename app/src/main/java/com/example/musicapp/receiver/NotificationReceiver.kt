package com.example.musicapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicapp.Application.Companion.ACTION_NEXT
import com.example.musicapp.Application.Companion.ACTION_PLAY
import com.example.musicapp.Application.Companion.ACTION_PREVIOUS
import com.example.musicapp.Constants.ActionName
import com.example.musicapp.Constants.Next
import com.example.musicapp.Constants.PlayPause
import com.example.musicapp.Constants.Previous
import com.example.musicapp.service.MusicService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        val serviceIntent = Intent(context, MusicService::class.java)
        if (actionName != null) {
            when (actionName) {
                ACTION_PLAY -> {
                    serviceIntent.putExtra(ActionName, PlayPause)
                    context?.startService(serviceIntent)
                }

                ACTION_NEXT -> {
                    serviceIntent.putExtra(ActionName, Next)
                    context?.startService(serviceIntent)
                }

                ACTION_PREVIOUS -> {
                    serviceIntent.putExtra(ActionName, Previous)
                    context?.startService(serviceIntent)
                }
            }
        }
    }
}
package com.example.musicapp.repository.player

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PlayerRepositoryImpl : PlayerRepository {
    override fun formattedTime(currentPosition: Int): String {
        val totalOut: String
        val totalNew: String
        val seconds = currentPosition % 60
        val minutes = currentPosition / 60
        totalOut = "$minutes : $seconds"
        totalNew = "$minutes : 0$seconds"
        return if (seconds.toString().length == 1) {
            totalNew
        } else {
            totalOut
        }
    }

    override suspend fun launchMusic(isActive: Boolean, action: () -> Unit) {
        while (isActive) {
            withContext(Dispatchers.Main) {
                action()
            }
        }
        delay(1000)
    }

    override suspend fun playBtn(action: () -> Unit) {
        withContext(Dispatchers.Main) {
            action()
        }
    }
}

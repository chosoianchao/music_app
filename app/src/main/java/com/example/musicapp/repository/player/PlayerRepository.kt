package com.example.musicapp.repository.player

interface PlayerRepository {
    fun formattedTime(currentPosition: Int): String
    suspend fun launchMusic(isActive: Boolean, action: () -> Unit)
    suspend fun playBtn(action: () -> Unit)
}
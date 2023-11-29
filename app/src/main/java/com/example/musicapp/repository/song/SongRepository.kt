package com.example.musicapp.repository.song

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.musicapp.model.MusicFiles

interface SongRepository {
    suspend fun getAudioAll(): ArrayList<MusicFiles>
    suspend fun deleteFile(
        position: Int,
        currentList: MutableList<MusicFiles>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    )

    suspend fun querySongs(content: String, songs: ArrayList<MusicFiles>): ArrayList<MusicFiles>
    suspend fun getMusic(action: (MusicFiles?) -> Unit)
    fun getContentSongs(newText: String): String
}
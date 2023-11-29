package com.example.musicapp.repository.albumdetail

import com.example.musicapp.model.MusicFiles

interface AlbumDetailRepository {

    suspend fun addAlbumSong(position: Int, musicFiles: ArrayList<MusicFiles>?): ArrayList<MusicFiles>
}
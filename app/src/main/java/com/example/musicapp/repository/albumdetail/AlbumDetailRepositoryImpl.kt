package com.example.musicapp.repository.albumdetail

import com.example.musicapp.model.MusicFiles

class AlbumDetailRepositoryImpl : AlbumDetailRepository {
    override suspend fun addAlbumSong(position: Int, musicFiles: ArrayList<MusicFiles>?): ArrayList<MusicFiles> {
        val albumsSong = ArrayList<MusicFiles>()
        var j = 0
        musicFiles?.let {
            for (element in it) {
                if (it[position].album == element.album) {
                    albumsSong.add(j, element)
                    j++
                }
            }
        }
        return albumsSong
    }
}
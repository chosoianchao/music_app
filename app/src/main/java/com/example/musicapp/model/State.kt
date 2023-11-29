package com.example.musicapp.model

data class State(
    var musicFiles: ArrayList<MusicFiles> = arrayListOf(),
    var position: Int = -1
)

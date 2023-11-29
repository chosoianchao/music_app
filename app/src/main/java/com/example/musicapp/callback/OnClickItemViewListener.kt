package com.example.musicapp.callback

import com.example.musicapp.model.MusicFiles

interface OnClickItemViewListener {
    fun onClick(position: Int, listMusicFiles: ArrayList<MusicFiles>)
}
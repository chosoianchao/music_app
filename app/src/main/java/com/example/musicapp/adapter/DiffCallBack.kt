package com.example.musicapp.adapter

import com.example.musicapp.base.BaseDiffCallBack
import com.example.musicapp.model.MusicFiles

class DiffCallBack : BaseDiffCallBack<MusicFiles>() {
    override fun areItemsTheSameImpl(oldItem: MusicFiles, newItem: MusicFiles): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSameImpl(oldItem: MusicFiles, newItem: MusicFiles): Boolean {
        return oldItem == newItem
    }
}
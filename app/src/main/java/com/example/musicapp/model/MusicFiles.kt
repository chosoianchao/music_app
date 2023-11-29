package com.example.musicapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicFiles(
    var path: String = "",
    var title: String = "",
    var artist: String = "",
    var album: String = "",
    var duration: String = "",
    var id: String = ""
) : Parcelable

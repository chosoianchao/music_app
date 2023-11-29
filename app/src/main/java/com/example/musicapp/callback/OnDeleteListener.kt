package com.example.musicapp.callback

import android.view.View

interface OnDeleteListener {
    fun delete(position: Int, view: View)
}
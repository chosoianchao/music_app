package com.example.musicapp.ui.albumdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicapp.Application
import com.example.musicapp.base.BaseViewModel
import com.example.musicapp.model.MusicFiles
import com.example.musicapp.repository.albumdetail.AlbumDetailRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlbumDetailViewModel(private val albumDetailRepositoryImpl: AlbumDetailRepositoryImpl) :
    BaseViewModel() {
    private var musicFiles: ArrayList<MusicFiles>? = arrayListOf()
    var albumsSong = MutableLiveData<ArrayList<MusicFiles>>()
    private var position = -1

    init {
        musicFiles = Application.state.musicFiles
        position = Application.state.position
    }

    fun addAlbumSong() {
        viewModelScope.launch(Dispatchers.IO) {
            albumsSong.postValue(albumDetailRepositoryImpl.addAlbumSong(position, musicFiles))
        }
    }
}
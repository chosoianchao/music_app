package com.example.musicapp.ui.listmusic

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicapp.base.BaseViewModel
import com.example.musicapp.model.MusicFiles
import com.example.musicapp.repository.song.SongsRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicViewModel(private val songsRepositoryImpl: SongsRepositoryImpl) : BaseViewModel() {
    val listAudio = MutableLiveData<ArrayList<MusicFiles>>()
    var musicFile = MusicFiles()
    val contentQuery = MutableLiveData<String>()
    fun getAudioAll() {
        viewModelScope.launch(Dispatchers.IO) {
            listAudio.postValue(songsRepositoryImpl.getAudioAll())
        }
    }

    fun deleteFile(
        position: Int,
        currentList: MutableList<MusicFiles>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            songsRepositoryImpl.deleteFile(position, currentList, intentSenderLauncher)
        }
    }

    fun getContentSongs(newText: String) {
        contentQuery.postValue(songsRepositoryImpl.getContentSongs(newText))
    }

    fun querySongs(content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            listAudio.postValue(listAudio.value?.let {
                songsRepositoryImpl.querySongs(
                    content,
                    it
                )
            })
        }
    }

    fun getMusic(action: (MusicFiles?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            songsRepositoryImpl.getMusic(action)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listAudio.removeObserver {
            it.clear()
        }
    }
}
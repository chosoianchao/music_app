package com.example.musicapp.ui.player

import androidx.lifecycle.viewModelScope
import com.example.musicapp.Application
import com.example.musicapp.base.BaseViewModel
import com.example.musicapp.model.MusicFiles
import com.example.musicapp.repository.player.PlayerRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random

class PlayerViewModel(private val playerRepositoryImpl: PlayerRepositoryImpl) : BaseViewModel() {
    var musicFiles: ArrayList<MusicFiles> = arrayListOf()
    var position = -1
    var isShuffle = false
    var isRepeat = false
    private var job: Job? = null

    init {
        musicFiles = Application.state.musicFiles
        position = Application.state.position
    }

    fun formattedTime(currentPosition: Int): String {
        return playerRepositoryImpl.formattedTime(currentPosition)
    }

    fun launchMusic(action: () -> Unit) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            playerRepositoryImpl.launchMusic(isActive) {
                action()
            }
        }
    }

    fun getRandomMusic(hasClickNext: Boolean) {
        val random = Random()
        val index = if (musicFiles.size == 1) {
            0
        } else {
            random.nextInt(musicFiles.size.minus(1))
        }
        if (isShuffle && !isRepeat) {
            position = index
        } else if (!isShuffle && !isRepeat) {
            if (hasClickNext) {
                position = (position + 1) % musicFiles.size
            } else {
                if ((position - 1) < 0) {
                    position = musicFiles.size.minus(1)
                } else {
                    position -= 1
                }
            }
        }
        Application.state.position = position
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}
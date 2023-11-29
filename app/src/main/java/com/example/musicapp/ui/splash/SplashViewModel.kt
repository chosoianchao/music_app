package com.example.musicapp.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicapp.SingleEvent
import com.example.musicapp.base.BaseViewModel
import com.example.musicapp.repository.splash.SplashRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashViewModel(private val splashRepository: SplashRepositoryImpl) : BaseViewModel() {
    val repoSelectedLiveData = MutableLiveData<SingleEvent<Boolean>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val status = splashRepository.getRepoSelectedStatus()
            repoSelectedLiveData.postValue(SingleEvent(status))
        }
    }
}
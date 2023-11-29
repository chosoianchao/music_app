package com.example.musicapp.repository.splash

import kotlinx.coroutines.delay

class SplashRepositoryImpl : SplashRepository {
    override suspend fun getRepoSelectedStatus(): Boolean {
        delay(1000)
        return false
    }
}
package com.example.musicapp.repository.splash

interface SplashRepository {
    suspend fun getRepoSelectedStatus(): Boolean
}
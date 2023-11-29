package com.example.musicapp.di

import com.example.musicapp.repository.albumdetail.AlbumDetailRepository
import com.example.musicapp.repository.albumdetail.AlbumDetailRepositoryImpl
import com.example.musicapp.repository.player.PlayerRepository
import com.example.musicapp.repository.player.PlayerRepositoryImpl
import com.example.musicapp.repository.song.SongRepository
import com.example.musicapp.repository.song.SongsRepositoryImpl
import com.example.musicapp.repository.splash.SplashRepository
import com.example.musicapp.repository.splash.SplashRepositoryImpl
import com.example.musicapp.ui.albumdetails.AlbumDetailViewModel
import com.example.musicapp.ui.listmusic.MusicViewModel
import com.example.musicapp.ui.player.PlayerViewModel
import com.example.musicapp.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::SplashRepositoryImpl) { bind<SplashRepository>() }
    viewModelOf(::SplashViewModel)

    singleOf(::SongsRepositoryImpl) { bind<SongRepository>() }
    viewModelOf(::MusicViewModel)

    singleOf(::PlayerRepositoryImpl) { bind<PlayerRepository>() }
    viewModelOf(::PlayerViewModel)

    singleOf(::AlbumDetailRepositoryImpl) { bind<AlbumDetailRepository>() }
    viewModelOf(::AlbumDetailViewModel)
}


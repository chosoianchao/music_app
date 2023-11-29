package com.example.musicapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.musicapp.Application
import com.example.musicapp.R
import com.example.musicapp.Utils
import com.example.musicapp.base.BaseAdapter
import com.example.musicapp.callback.OnClickItemViewListener
import com.example.musicapp.databinding.AlbumItemBinding
import com.example.musicapp.model.MusicFiles

class AlbumAdapter(private val onClickListener: OnClickItemViewListener) :
    BaseAdapter<MusicFiles, AlbumItemBinding>(DiffCallBack()) {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): AlbumItemBinding {
        return AlbumItemBinding.inflate(inflater, parent, false)
    }

    override fun clickView(position: Int) {
        val arrayMusicFiles = arrayListOf<MusicFiles>()
        arrayMusicFiles.addAll(currentList)
        onClickListener.onClick(position, arrayMusicFiles)
    }

    override fun bindItem(binding: AlbumItemBinding, position: Int) {
        val album = currentList[position]
        binding.tvAlbum.text = album.title
        Utils.getAlbumArt(album.path) { image ->
            Application.Instance?.applicationContext?.let {
                if (image != null) {
                    Glide.with(it).load(image).into(binding.ivAlbum)
                } else {
                    Glide.with(it).load(R.drawable.broken_image).into(binding.ivAlbum)
                }
            }
        }
    }
}
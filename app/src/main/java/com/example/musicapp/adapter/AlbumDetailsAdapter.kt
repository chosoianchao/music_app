package com.example.musicapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.example.musicapp.Application
import com.example.musicapp.R
import com.example.musicapp.Utils
import com.example.musicapp.base.BaseAdapter
import com.example.musicapp.callback.OnClickItemViewListener
import com.example.musicapp.callback.OnDeleteListener
import com.example.musicapp.databinding.MusicItemsBinding
import com.example.musicapp.model.MusicFiles

class AlbumDetailsAdapter(
    private val onClickItemViewListener: OnClickItemViewListener,
    private val onDeleteListener: OnDeleteListener
) :
    BaseAdapter<MusicFiles, MusicItemsBinding>(DiffCallBack()) {

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): MusicItemsBinding {
        return MusicItemsBinding.inflate(inflater, parent, false)
    }

    override fun clickView(position: Int) {
        val arrayMusicFiles = arrayListOf<MusicFiles>()
        arrayMusicFiles.addAll(currentList)
        onClickItemViewListener.onClick(position, arrayMusicFiles)
    }

    override fun bindItem(binding: MusicItemsBinding, position: Int) {
        val item = currentList[position]
        binding.tvMusic.text = item.title
        Utils.getAlbumArt(item.path) { image ->
            Application.Instance?.applicationContext?.let {
                if (image != null) {
                    Glide.with(it).load(image).into(binding.ivMusic)
                } else {
                    Glide.with(it).load(R.drawable.broken_image).into(binding.ivMusic)
                }
            }
        }
        binding.ivMenu.setOnClickListener {
            val popupMenu = PopupMenu(Application.Instance?.applicationContext, it)
            popupMenu.menuInflater.inflate(R.menu.popup, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.delete -> {
                        onDeleteListener.delete(position, it)
                    }
                }
                true
            }
        }
    }
}
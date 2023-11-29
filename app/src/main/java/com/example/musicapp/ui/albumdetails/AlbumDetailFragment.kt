package com.example.musicapp.ui.albumdetails

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.Application
import com.example.musicapp.Constants
import com.example.musicapp.Constants.POSITION
import com.example.musicapp.R
import com.example.musicapp.Utils
import com.example.musicapp.adapter.AlbumDetailsAdapter
import com.example.musicapp.base.BaseFragment
import com.example.musicapp.callback.OnClickItemViewListener
import com.example.musicapp.callback.OnDeleteListener
import com.example.musicapp.databinding.AlbumDetailFragmentBinding
import com.example.musicapp.model.MusicFiles
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumDetailFragment : BaseFragment<AlbumDetailFragmentBinding>() {
    override val viewModel: AlbumDetailViewModel by viewModel()
    override val layoutResource: Int
        get() = R.layout.album_detail_fragment

    private var albumDetailsAdapter: AlbumDetailsAdapter? = null

    override fun initViewBinding(view: View): AlbumDetailFragmentBinding {
        return AlbumDetailFragmentBinding.bind(view)
    }

    override fun initViews() {
        binding.rvAlbumSongs.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        albumDetailsAdapter = AlbumDetailsAdapter(object : OnClickItemViewListener {
            override fun onClick(position: Int, listMusicFiles: ArrayList<MusicFiles>) {
                val bundle = bundleOf(
                    POSITION to position,
                    Constants.LIST_MUSIC to listMusicFiles
                )
                findNavController().navigate(
                    R.id.action_albumDetailFragment_to_playerFragment,
                    bundle
                )
            }
        }, object : OnDeleteListener {
            override fun delete(position: Int, view: View) {
            }
        })
        binding.rvAlbumSongs.adapter = albumDetailsAdapter
        viewModel.addAlbumSong()
    }

    override fun observeViewModel() {
        viewModel.albumsSong.observe(viewLifecycleOwner) { albumSongs ->
            albumDetailsAdapter?.submitList(albumSongs)
            Application.state.musicFiles = albumSongs
            loadImage(albumSongs[0].path)
        }
    }

    private fun loadImage(path: String) {
        Utils.getAlbumArt(path) { image ->
            Application.Instance?.applicationContext?.let {
                if (image != null) {
                    Glide.with(it).load(image).into(binding.ivAlbumPhoto)
                } else {
                    Glide.with(it).load(R.drawable.broken_image).into(binding.ivAlbumPhoto)
                }
            }
        }
    }
}
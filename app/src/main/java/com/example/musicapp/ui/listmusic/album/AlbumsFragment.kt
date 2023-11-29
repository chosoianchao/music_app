package com.example.musicapp.ui.listmusic.album

import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicapp.Application
import com.example.musicapp.R
import com.example.musicapp.adapter.AlbumAdapter
import com.example.musicapp.base.BaseFragment
import com.example.musicapp.callback.OnClickItemViewListener
import com.example.musicapp.databinding.AlbumsFragmentBinding
import com.example.musicapp.model.MusicFiles
import com.example.musicapp.ui.listmusic.MusicViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class AlbumsFragment : BaseFragment<AlbumsFragmentBinding>() {
    override val viewModel by activityViewModel<MusicViewModel>()
    override val layoutResource: Int
        get() = R.layout.albums_fragment

    private var albumAdapter: AlbumAdapter? = null
    override fun initViewBinding(view: View): AlbumsFragmentBinding {
        return AlbumsFragmentBinding.bind(view)
    }

    override fun initViews() {
        binding.rcvAlbum.setHasFixedSize(true)
        binding.rcvAlbum.layoutManager = GridLayoutManager(context, 2)
        albumAdapter = AlbumAdapter(object : OnClickItemViewListener {
            override fun onClick(position: Int, listMusicFiles: ArrayList<MusicFiles>) {
                Application.state.position = position
                findNavController().navigate(R.id.action_listMusicFragment_to_albumDetailFragment)
            }

        })
        binding.rcvAlbum.adapter = albumAdapter
    }

    override fun observeViewModel() {
        viewModel.listAudio.observe(viewLifecycleOwner) {
            albumAdapter?.submitList(it)
        }
    }

    companion object {
        const val TAG = "Albums"
    }
}
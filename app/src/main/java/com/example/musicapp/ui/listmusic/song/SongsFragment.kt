package com.example.musicapp.ui.listmusic.song

import android.app.Activity.RESULT_OK
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.Application
import com.example.musicapp.R
import com.example.musicapp.adapter.MusicAdapter
import com.example.musicapp.base.BaseFragment
import com.example.musicapp.callback.OnClickItemViewListener
import com.example.musicapp.callback.OnDeleteListener
import com.example.musicapp.custom.LoadingDialog
import com.example.musicapp.databinding.SongsFragmentBinding
import com.example.musicapp.model.MusicFiles
import com.example.musicapp.ui.listmusic.MusicViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SongsFragment : BaseFragment<SongsFragmentBinding>() {
    override val viewModel by activityViewModel<MusicViewModel>()
    override val layoutResource: Int
        get() = R.layout.songs_fragment
    private var musicAdapter: MusicAdapter? = null
    private val intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.getAudioAll()
                Snackbar.make(requireView(), "File Deleted", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(requireView(), "Can't be Deleted", Snackbar.LENGTH_LONG).show()
            }
        }

    override fun initViewBinding(view: View): SongsFragmentBinding {
        return SongsFragmentBinding.bind(view)
    }

    override fun initViews() {
        binding.rvMusic.setHasFixedSize(true)
        binding.rvMusic.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        musicAdapter = MusicAdapter(object : OnClickItemViewListener {
            override fun onClick(position: Int, listMusicFiles: ArrayList<MusicFiles>) {
                Application.state.position = position
                findNavController().navigate(R.id.action_listMusicFragment_to_playerFragment)
            }
        }, object : OnDeleteListener {
            override fun delete(position: Int, view: View) {
                musicAdapter?.currentList?.let {
                    viewModel.deleteFile(
                        position,
                        it,
                        intentSenderLauncher
                    )
                }
            }
        })
        binding.rvMusic.adapter = musicAdapter
        viewModel.getAudioAll()
    }

    override fun observeViewModel() {
        viewModel.listAudio.observe(viewLifecycleOwner) {
            musicAdapter?.submitList(it)
            Application.state.musicFiles = it
        }
        viewModel.contentQuery.observe(viewLifecycleOwner) {
            viewModel.querySongs(it)
        }
    }

    companion object {
        const val TAG = "Songs"
    }
}
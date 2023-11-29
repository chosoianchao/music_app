package com.example.musicapp.ui.listmusic.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.musicapp.Application
import com.example.musicapp.Constants.SortByDate
import com.example.musicapp.Constants.SortByName
import com.example.musicapp.Constants.SortBySize
import com.example.musicapp.Constants.Sorting
import com.example.musicapp.R
import com.example.musicapp.SharedPreferencesHelper
import com.example.musicapp.Utils
import com.example.musicapp.adapter.ViewPagerAdapter
import com.example.musicapp.base.BaseFragment
import com.example.musicapp.databinding.ListMusicFragmentBinding
import com.example.musicapp.ui.listmusic.MusicViewModel
import com.example.musicapp.ui.listmusic.album.AlbumsFragment
import com.example.musicapp.ui.listmusic.song.SongsFragment
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class ListMusicFragment : BaseFragment<ListMusicFragmentBinding>(), SearchView.OnQueryTextListener {
    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    private var searchView: SearchView? = null

    override val viewModel by activityViewModel<MusicViewModel>()
    override val layoutResource: Int
        get() = R.layout.list_music_fragment

    private val requestPermissionLauncherStorage =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val isStorageAudioPermitted = permission[requiredPermissions[0]]
                val isStorageImagePermitted = permission[requiredPermissions[1]]
                val isStorageVideoPermitted = permission[requiredPermissions[2]]
                if (isStorageAudioPermitted == true && isStorageImagePermitted == true && isStorageVideoPermitted == true) {
                    logInfoPermission(true)
                    initViewPagerTab()
                } else {
                    logInfoPermission(false)
                    sendToSettingDialog()
                }
            } else {
                val writeGranted = permission[requiredPermissions[0]]
                val readGranted = permission[requiredPermissions[1]]
                if (writeGranted == true && readGranted == true) {
                    logInfoPermission(true)
                    initViewPagerTab()
                } else {
                    logInfoPermission(false)
                    sendToSettingDialog()
                }
            }
        }

    override fun initViewBinding(view: View): ListMusicFragmentBinding =
        ListMusicFragmentBinding.bind(view)

    override fun initViews() {
        requestPermissionStorage()
        binding.topAppBar.setNavigationOnClickListener {
            AlertDialog.Builder(context).setTitle(R.string.app_name)
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    activity?.finishAndRemoveTask()
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    private fun requestPermissionStorage() {
        if (allPermissionResultCheck()) {
            logInfoPermission(true)
            initViewPagerTab()
            val menu = binding.topAppBar.menu.findItem(R.id.search)
            searchView = menu.actionView as SearchView
            searchView?.setOnQueryTextListener(this)

            val menuSort = binding.topAppBar.menu.findItem(R.id.sort_options)
            val menuDate = binding.topAppBar.menu.findItem(R.id.by_date)
            val menuName = binding.topAppBar.menu.findItem(R.id.by_name)
            val menuSize = binding.topAppBar.menu.findItem(R.id.by_size)
            menuSort.title = setTextColorMenuItem(menuSort)
            menuDate.title = setTextColorMenuItem(menuDate)
            menuName.title = setTextColorMenuItem(menuName)
            menuSize.title = setTextColorMenuItem(menuSize)
            setOnClickMenuItem(menuDate, menuName, menuSize)
        } else {
            requestPermissionLauncherStorage.launch(requiredPermissions)
        }
    }

    private fun setOnClickMenuItem(menuDate: MenuItem?, menuName: MenuItem?, menuSize: MenuItem?) {
        menuName?.setOnMenuItemClickListener {
            SharedPreferencesHelper(requireContext()).putString(Sorting, SortByName)
            activity?.recreate()
            true
        }
        menuDate?.setOnMenuItemClickListener {
            SharedPreferencesHelper(requireContext()).putString(Sorting, SortByDate)
            activity?.recreate()
            true
        }
        menuSize?.setOnMenuItemClickListener {
            SharedPreferencesHelper(requireContext()).putString(Sorting, SortBySize)
            activity?.recreate()
            true
        }
    }

    private fun sendToSettingDialog() {
        AlertDialog.Builder(context).setTitle("Alert for Permission")
            .setMessage("Go to Settings for Permissions")
            .setPositiveButton(
                "Settings"
            ) { dialog, _ ->
                // code to go to setting of application
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                activity?.startActivity(intent)
            }
            .setNegativeButton(
                "Exit"
            ) { dialog, _ ->
                dialog.dismiss()
                activity?.finish()
            }.show()
    }

    private fun allPermissionResultCheck(): Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }

    private fun initViewPagerTab() {
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.addFragments(SongsFragment())
        viewPagerAdapter.addFragments(AlbumsFragment())
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if (position == 0) {
                tab.text = SongsFragment.TAG
            } else if (position == 1) {
                tab.text = AlbumsFragment.TAG
            }
        }.attach()
    }

    private fun logInfoPermission(isGranted: Boolean) {
        for (permission in requiredPermissions) {
            Log.i(TAG, "$permission: $isGranted")
        }
    }

    private fun setTextColorMenuItem(menu: MenuItem): SpannableString {
        // Define text and background colors
        val textColor = ContextCompat.getColor(requireContext(), R.color.black)
        val spannable = SpannableString(menu.title)
        spannable.setSpan(ForegroundColorSpan(textColor), 0, spannable.length, 0)
        // Set the modified SpannableString as the menu item title
        return spannable
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.getContentSongs(newText.toString())
        return true
    }

    override fun setOnClick() {
        binding.flPlayPauseMini.setOnClickListener {
            activity?.musicService?.let {
                if (it.isPlaying()) {
                    it.pause()
                    binding.flPlayPauseMini.setImageResource(R.drawable.ic_play)
                } else {
                    it.start()
                    binding.flPlayPauseMini.setImageResource(R.drawable.ic_pause)
                }
            }
        }
        binding.skipNextBottom.setOnClickListener {
            Application.state.position = Application.state.musicFiles.indexOf(viewModel.musicFile)
            findNavController().navigate(R.id.action_listMusicFragment_to_playerFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.rootLayout.requestFocus()
        viewModel.getMusic { musicFile ->
            if (musicFile == null) {
                binding.fragBottomPlayer.visibility = View.GONE
            } else {
                viewModel.musicFile = musicFile
                binding.fragBottomPlayer.visibility = View.VISIBLE
                Utils.getAlbumArt(musicFile.path) {
                    Glide.with(requireContext()).load(it).into(binding.ivBottomAlbum)
                }
                binding.tvSongNameMiniPlayer.text = musicFile.title
                binding.tvArtistMini.text = musicFile.artist
                if (activity?.musicService?.isPlaying() == true) {
                    binding.flPlayPauseMini.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.flPlayPauseMini.setImageResource(R.drawable.ic_play)
                }
            }
        }
    }


    override fun onDestroyView() {
        binding.viewPager.adapter = null
        super.onDestroyView()
    }

    companion object {
        private val TAG = ListMusicFragment::class.java.name
    }
}
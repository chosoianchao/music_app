package com.example.musicapp.ui.splash

import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.musicapp.base.BaseFragment
import com.example.musicapp.R
import com.example.musicapp.SingleEventObserver
import com.example.musicapp.databinding.SplashFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : BaseFragment<SplashFragmentBinding>() {
    override val viewModel: SplashViewModel by viewModel()
    override val layoutResource: Int
        get() = R.layout.splash_fragment

    override fun initViewBinding(view: View): SplashFragmentBinding =
        SplashFragmentBinding.bind(view)

    override fun initViews() {

    }

    override fun observeViewModel() {
        viewModel.repoSelectedLiveData.observe(this, SingleEventObserver {
            if (!it) {
                findNavController().navigate(R.id.action_splashFragment_to_listMusicFragment)
            }
        })
    }
}
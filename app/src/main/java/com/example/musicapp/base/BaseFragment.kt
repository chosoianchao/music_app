package com.example.musicapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.musicapp.activity.MainActivity

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    protected var _binding: B? = null
    protected val binding: B
        get() = requireNotNull(_binding)
    protected abstract val viewModel: BaseViewModel
    protected abstract val layoutResource: Int
    protected abstract fun initViewBinding(view: View): B
    protected abstract fun initViews()
    protected var activity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutResource, container, false)
        _binding = initViewBinding(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.activity = getActivity() as MainActivity?
        initViews()
        setOnClick()
        observeViewModel()
    }

    protected open fun observeViewModel() {
    }

    protected open fun setOnClick() {

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
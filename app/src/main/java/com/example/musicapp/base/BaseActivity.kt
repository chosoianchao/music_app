package com.example.musicapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding>(private val layoutId: Int) :
    AppCompatActivity() {
    protected var binding: VB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(this).inflate(layoutId, null)
        binding = initViewBinding(view)
        setContentView(view)
        initViews()
    }

    protected abstract fun initViews()
    protected abstract fun initViewBinding(view: View): VB
}
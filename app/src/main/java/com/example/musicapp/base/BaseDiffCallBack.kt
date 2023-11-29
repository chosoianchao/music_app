package com.example.musicapp.base

import androidx.recyclerview.widget.DiffUtil

abstract class BaseDiffCallBack<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return areItemsTheSameImpl(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return areContentsTheSameImpl(oldItem, newItem)
    }

    abstract fun areItemsTheSameImpl(oldItem: T, newItem: T): Boolean
    abstract fun areContentsTheSameImpl(oldItem: T, newItem: T): Boolean
}

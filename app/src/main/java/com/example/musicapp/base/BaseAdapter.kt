package com.example.musicapp.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, VB : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, BaseAdapter<T, VB>.BaseViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = createBinding(inflater, parent)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bindItem(holder.binding, position)
        holder.itemView.setOnClickListener {
            clickView(holder.adapterPosition)
        }
    }

    protected abstract fun createBinding(inflater: LayoutInflater, parent: ViewGroup): VB
    protected abstract fun bindItem(binding: VB, position: Int)
    protected abstract fun clickView(position: Int)

    inner class BaseViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}
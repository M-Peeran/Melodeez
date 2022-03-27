package com.peeranm.melodeez.common.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseHolder<T: ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    init { this.onInitializeViewHolder(binding.root) }
    abstract fun onInitializeViewHolder(rootView: View)
}
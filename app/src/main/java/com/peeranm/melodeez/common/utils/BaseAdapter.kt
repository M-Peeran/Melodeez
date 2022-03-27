package com.peeranm.melodeez.common.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>)
    : ListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {
    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    abstract override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
}
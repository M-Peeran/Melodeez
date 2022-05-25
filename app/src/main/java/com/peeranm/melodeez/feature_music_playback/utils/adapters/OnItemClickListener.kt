package com.peeranm.melodeez.feature_music_playback.utils.adapters

import android.view.View

interface OnItemClickListener<T> {
    fun onItemClick(view: View?, data: T, position: Int)
}
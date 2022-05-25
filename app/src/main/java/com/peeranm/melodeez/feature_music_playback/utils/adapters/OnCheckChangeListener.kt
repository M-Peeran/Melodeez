package com.peeranm.melodeez.feature_music_playback.utils.adapters

import android.widget.CompoundButton

interface OnCheckChangeListener<T> {
    fun onCheckChange(compButton: CompoundButton?, data: T, isSelected: Boolean, position: Int)
}
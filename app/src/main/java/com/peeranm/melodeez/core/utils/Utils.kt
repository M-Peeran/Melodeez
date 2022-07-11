package com.peeranm.melodeez.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream

val tabArray = arrayOf(
    "All",
    "Albums",
    "Artists",
    "Playlists"
)

fun getTimeStamp(milliSec: Long): String {
    val min = (milliSec / 1000) / 60
    val sec = (milliSec / 1000) % 60
    return buildString {
        append(if (min < 10) "0$min" else min)
        append(':')
        append(if (sec < 10) "0$sec" else sec)
    }
}

fun Context.getBitmap(directory: File = this.filesDir, bitmapName: String): Bitmap {
    val artFile = File(directory, "$bitmapName.png")
    return BitmapFactory.decodeStream(FileInputStream(artFile))
}

sealed class RepeatState {
    object RepeatOff : RepeatState()
    object RepeatAll : RepeatState()
    object RepeatOne : RepeatState()
}

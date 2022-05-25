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
    return when {
        min < 10 -> if (sec < 10) "0$min:0$sec" else "0$min:$sec"
        sec < 10 -> "$min:0$sec"
        else -> "$min:$sec"
    }
}

fun Context.getBitmap(directory: File = this.filesDir, bitmapName: String): Bitmap {
    val artFile = File(directory, "$bitmapName.png")
    return BitmapFactory.decodeStream(FileInputStream(artFile))
}

sealed class DataState<out R> {
    data class Success<out T>(val data: T) : DataState<T>()
    data class Failure(val message: String) : DataState<Nothing>()
    object Loading : DataState<Nothing>()
    object Synchronizing : DataState<Nothing>()
    object SynchronizationCompleted : DataState<Nothing>()
}

sealed class RepeatState {
    object RepeatOff : RepeatState()
    object RepeatAll : RepeatState()
    object RepeatOne : RepeatState()
}

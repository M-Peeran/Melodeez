package com.peeranm.melodeez.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

fun <T> LifecycleOwner.collectWithLifecycle(flow: Flow<T>, collect: FlowCollector<T>) =
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collect)
        }
    }

fun <T> LifecycleOwner.collectLatestWithLifecycle(flow: Flow<T>, collect: suspend (T) -> Unit) =
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }

fun Context.getImageRequest(albumArtRef: String, target: ImageView) : ImageRequest {
    return ImageRequest.Builder(this).apply {
        transformations(
            RoundedCornersTransformation(
                topLeft = 20f,
                topRight = 20f,
                bottomLeft = 20f,
                bottomRight = 20f,
            )
        )
        data(
            getBitmap(
                File(target.context.filesDir, "albumarts"),
                albumArtRef
            )
        )
        target(target)
    }.build()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message, duration).show()
}

fun getReleaseYearString(releaseYear: Int): String {
    return if (releaseYear == UNKNOWN_RELEASE_YEAR) "Unknown Release Year"
    else releaseYear.toString()
}

sealed class RepeatState {
    object RepeatOff : RepeatState()
    object RepeatAll : RepeatState()
    object RepeatOne : RepeatState()
}


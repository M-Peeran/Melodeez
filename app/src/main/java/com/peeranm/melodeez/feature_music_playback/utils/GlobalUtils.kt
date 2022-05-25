package com.peeranm.melodeez.feature_music_playback.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.peeranm.melodeez.core.utils.UNKNOWN_RELEASE_YEAR
import com.peeranm.melodeez.core.utils.getBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

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

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun getReleaseYearString(releaseYear: Int): String {
    return if (releaseYear == UNKNOWN_RELEASE_YEAR) "Unknown Release Year"
    else releaseYear.toString()
}
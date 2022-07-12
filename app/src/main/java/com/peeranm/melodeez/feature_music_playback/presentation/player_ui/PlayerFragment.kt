package com.peeranm.melodeez.feature_music_playback.presentation.player_ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadata
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.load
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.*
import com.peeranm.melodeez.databinding.PlayerFragmentBinding
import com.peeranm.melodeez.feature_music_playback.presentation.now_playing.NowPlayingDialog
import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackHelper
import com.peeranm.melodeez.feature_music_playback.utils.helpers.RepeatStateHelper
import com.peeranm.melodeez.feature_music_playback.utils.helpers.TrackInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    @Inject lateinit var trackInfo: TrackInfo
    @Inject lateinit var repeatStateHelper: RepeatStateHelper
    @Inject lateinit var playbackHelper: PlaybackHelper

    private var _binding: PlayerFragmentBinding? = null
    private val binding: PlayerFragmentBinding
    get() = _binding!!

    private val controller get() = requireActivity().mediaController
    private var progressJob: Job? = null

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                controller.transportControls.seekTo(progress.toLong())
                binding.textStartTime.text = getTimeStamp(progress.toLong())
            }
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnPlayPause.setOnClickListener {
                if (controller.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                    controller.transportControls.pause()
                } else {
                    controller.transportControls.play()
                }
            }

            btnRepeat.setOnClickListener {
                updateRepeatState(repeatStateHelper.toggleState())
            }

            btnNext.setOnClickListener {
                controller.transportControls.skipToNext()
            }

            btnPrevious.setOnClickListener {
                controller.transportControls.skipToPrevious()
            }
            btnNowPlaying.setOnClickListener {
                NowPlayingDialog().show(childFragmentManager, "NOW PLAYING")
            }
            seekbarProgress.setOnSeekBarChangeListener(seekBarChangeListener)
            setProperStateImage()
        }

        collectWithLifecycle(trackInfo.stateAsFlow) { state ->
            state?.let { binding.updateState(it) }
        }

        collectWithLifecycle(trackInfo.metadataAsFlow) { metadata ->
            metadata?.let { binding.updateMetadata(it) }
        }
    }

    private fun updateRepeatState(repeatState: RepeatState) {
        when(repeatState) {
            is RepeatState.RepeatOff -> {
                showToast("Repeat Off")
                setProperStateImage()
            }
            is RepeatState.RepeatAll -> {
                showToast("Repeat All")
                setProperStateImage()
            }
            is RepeatState.RepeatOne -> {
                showToast("Repeat Single")
                setProperStateImage()
            }
        }
    }

    private fun PlayerFragmentBinding.updateMetadata(metadata: MediaMetadataCompat) {
        textTitle.text = metadata.getText(MediaMetadata.METADATA_KEY_TITLE)
        textArtist.text = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST)
        textAlbum.text = metadata.getText(MediaMetadata.METADATA_KEY_ALBUM)
        textStartTime.text = getString(R.string.start_time)
        textEndTime.text = getTimeStamp(metadata.getLong(MediaMetadata.METADATA_KEY_DURATION))
        val albumArtRef = metadata.getText(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
        if (albumArtRef != null) {
            val albumArt = requireContext().getBitmap(
                File(requireContext().filesDir, "albumarts"),
                albumArtRef.toString()
            )
            val imageLoadRequest = requireContext().getImageRequest(albumArtRef.toString(), imageAlbumArt)
            lifecycleScope.launch { requireContext().imageLoader.execute(imageLoadRequest) }

            Palette.from(albumArt).generate {
                val swatch = it?.darkVibrantSwatch ?: it?.darkMutedSwatch ?: it?.mutedSwatch
                swatch?.run {
                    val prevColor = (root.background as ColorDrawable).color
                    ValueAnimator().apply {
                        setIntValues(prevColor, rgb)
                        setEvaluator(ArgbEvaluator())
                        addUpdateListener {
                            root.setBackgroundColor(animatedValue as Int)
                            activity?.run { window.statusBarColor = animatedValue as Int }
                        }
                    }.setDuration(1000).start()
                    textStartTime.setTextColor(titleTextColor)
                    textEndTime.setTextColor(titleTextColor)
                    textTitle.setTextColor(bodyTextColor)
                    textAlbum.setTextColor(titleTextColor)
                    textArtist.setTextColor(titleTextColor)
                    seekbarProgress.progressTintList = ColorStateList.valueOf(titleTextColor)
                }
            }
        } else {
            imageAlbumArt.load(R.drawable.album_art_none)
            seekbarProgress.progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_200
                )
            )
            activity?.run {
                window.statusBarColor = ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_800
                )
            }
            root.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_600
                )
            )
        }
    }

    private fun PlayerFragmentBinding.updateState(state: PlaybackStateCompat) {
        updateProgress(state.state)
        when (state.state) {
            PlaybackStateCompat.STATE_PAUSED -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            }
            PlaybackStateCompat.STATE_PLAYING -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
            PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            }
            PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            }
            PlaybackStateCompat.STATE_NONE -> {
                binding.textStartTime.text = getString(R.string.start_time)
                binding.seekbarProgress.progress = 0
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            }
            else -> Unit
        }
    }

    private fun setProperStateImage() {
        binding.apply {
            when(repeatStateHelper.getState()) {
                is RepeatState.RepeatOff -> {
                    btnRepeat.setImageResource(R.drawable.ic_repeat_off)
                }
                is RepeatState.RepeatAll -> {
                    btnRepeat.setImageResource(R.drawable.ic_repeat_all)
                }
                is RepeatState.RepeatOne -> {
                    btnRepeat.setImageResource(R.drawable.ic_repeat_one)
                }
                else -> Unit
            }
        }
    }

    private fun PlayerFragmentBinding.updateProgress(state: Int) {
        when (state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                cancelProgress()
                val endTime = trackInfo.metadataAsFlow.value?.getLong(MediaMetadata.METADATA_KEY_DURATION)!!
                progressJob = lifecycleScope.launch {
                    textEndTime.text = getTimeStamp(endTime)
                    seekbarProgress.max = endTime.toInt()
                    while (isActive) {
                        if (playbackHelper.isPlaying()) {
                            seekbarProgress.progress = playbackHelper.getPlaybackPosition()
                            textStartTime.text = getTimeStamp(seekbarProgress.progress.toLong())
                        } else cancelProgress()
                        delay(1000L)
                    }
                }
            }
            else -> cancelProgress()
        }
    }

    override fun onStop() {
        super.onStop()
        cancelProgress()
    }

    private fun cancelProgress() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
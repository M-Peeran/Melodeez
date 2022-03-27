package com.peeranm.melodeez.feature_music_playback.presentation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadata
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.peeranm.melodeez.R
import com.peeranm.melodeez.common.utils.RepeatState
import com.peeranm.melodeez.common.utils.getBitmap
import com.peeranm.melodeez.common.utils.getTimeStamp
import com.peeranm.melodeez.databinding.PlayerFragmentBinding
import com.peeranm.melodeez.feature_music_playback.presentation.now_playing.NowPlayingDialog
import com.peeranm.melodeez.feature_music_playback.utils.CurrentRepeatState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private var _binding: PlayerFragmentBinding? = null
    private val binding: PlayerFragmentBinding
    get() = _binding!!

    private val viewModel: PlayerViewModel by viewModels()
    private val controller get() = requireActivity().mediaController
    private var progressJob: Job? = null

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                controller.transportControls.seekTo(progress.toLong())
                binding.textviewStartTime.text = getTimeStamp(progress.toLong())
            }
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
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

        viewModel.state.observe(viewLifecycleOwner) { updateState(it) }
        viewModel.metadata.observe(viewLifecycleOwner) { updateMetadata(it) }

        binding.apply {
            imageviewPlayPause.setOnClickListener {
                if (controller.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                    controller.transportControls.pause()
                } else {
                    controller.transportControls.play()
                }
            }

            imageviewRepeat.setOnClickListener {
                val repeatState = viewModel.getRepeatState()
                when(repeatState) {
                    is RepeatState.RepeatOff -> {
                        viewModel.setRepeatState(RepeatState.RepeatAll)
                        Toast.makeText(requireContext(), "Repeat All", Toast.LENGTH_SHORT).show()
                        setProperStateImage()
                    }
                    is RepeatState.RepeatAll -> {
                        viewModel.setRepeatState(RepeatState.RepeatOne)
                        Toast.makeText(requireContext(), "Repeat Single", Toast.LENGTH_SHORT).show()
                        setProperStateImage()
                    }
                    is RepeatState.RepeatOne -> {
                        viewModel.setRepeatState(RepeatState.RepeatOff)
                        Toast.makeText(requireContext(), "Repeat Off", Toast.LENGTH_SHORT).show()
                        setProperStateImage()
                    }
                }
            }

            imageviewNext.setOnClickListener {
                controller.transportControls.skipToNext()
            }

            imageviewPrevious.setOnClickListener {
                controller.transportControls.skipToPrevious()
            }
            imageviewNowPlaying.setOnClickListener {
                NowPlayingDialog().show(childFragmentManager, "NOW PLAYING")
            }
            seekbarProgress.setOnSeekBarChangeListener(seekBarChangeListener)
            setProperStateImage()
        }
    }

    private fun updateMetadata(metadataCompat: MediaMetadataCompat) {
        Log.i("APP_LOGS", "New metadata received")
        binding.apply {
            textviewTrackTitle.text = metadataCompat.getText(MediaMetadata.METADATA_KEY_TITLE)
            textviewTrackArtist.text = metadataCompat.getText(MediaMetadata.METADATA_KEY_ARTIST)
            textviewTrackAlbum.text = metadataCompat.getText(MediaMetadata.METADATA_KEY_ALBUM)
            textviewStartTime.text = "00:00"
            textviewEndTime.text =
                getTimeStamp(metadataCompat.getLong(MediaMetadata.METADATA_KEY_DURATION))
            val albumArtRef = metadataCompat.getText(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
            if (albumArtRef != null) {
                val albumArt = requireContext().getBitmap(
                    File(requireContext().filesDir, "albumarts"),
                    albumArtRef.toString()
                )
                val imageLoadRequest = ImageRequest.Builder(requireContext()).apply {
                    transformations(
                        RoundedCornersTransformation(
                            topLeft = 10f,
                            topRight = 10f,
                            bottomLeft = 10f,
                            bottomRight = 10f,
                        )
                    )
                    data(albumArt)
                    target(imageviewAlbumArt)
                }.build()
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
                        textviewStartTime.setTextColor(titleTextColor)
                        textviewEndTime.setTextColor(titleTextColor)
                        textviewTrackTitle.setTextColor(bodyTextColor)
                        textviewTrackAlbum.setTextColor(titleTextColor)
                        textviewTrackArtist.setTextColor(titleTextColor)
                        seekbarProgress.progressTintList = ColorStateList.valueOf(titleTextColor)
                    }
                }
            } else {
                imageviewAlbumArt.load(R.drawable.album_art_none)
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
    }

    private fun updateState(stateCompat: PlaybackStateCompat) {
        Log.i("APP_LOGS", "New state received: ${stateCompat.state}")
        updateProgress(stateCompat.state)
        when (stateCompat.state) {
            PlaybackStateCompat.STATE_PAUSED -> {
                binding.imageviewPlayPause.setImageResource(R.drawable.ic_play)
            }
            PlaybackStateCompat.STATE_PLAYING -> {
                binding.imageviewPlayPause.setImageResource(R.drawable.ic_pause)
            }
            PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                binding.imageviewPlayPause.setImageResource(R.drawable.ic_play)
            }
            PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> {
                binding.imageviewPlayPause.setImageResource(R.drawable.ic_play)
            }
            PlaybackStateCompat.STATE_NONE -> {
                binding.textviewStartTime.text = "00:00"
                binding.seekbarProgress.progress = 0
                binding.imageviewPlayPause.setImageResource(R.drawable.ic_play)
            }
            else -> {}
        }
    }

    private fun setProperStateImage() {
        binding.apply {
            when(viewModel.getRepeatState()) {
                is RepeatState.RepeatOff -> {
                    imageviewRepeat.setImageResource(R.drawable.ic_repeat_off)
                }
                is RepeatState.RepeatAll -> {
                    imageviewRepeat.setImageResource(R.drawable.ic_repeat_all)
                }
                is RepeatState.RepeatOne -> {
                    imageviewRepeat.setImageResource(R.drawable.ic_repeat_one)
                }
            }
        }
    }

    private fun updateProgress(state: Int) {
        when (state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                cancelProgress()
                val endTime = viewModel.metadata.value?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: -1L
                progressJob = lifecycleScope.launch {
                    binding.apply {
                        textviewEndTime.text = getTimeStamp(endTime)
                        seekbarProgress.max = endTime.toInt()
                        while (isActive) {
                            Log.i("APP_LOGS", "Active")
                            if (viewModel.isPlaying()) {
                                seekbarProgress.progress = viewModel.getPlaybackPosition()
                                textviewStartTime.text = getTimeStamp(seekbarProgress.progress.toLong())
                            } else cancelProgress()
                            delay(1000L)
                        }
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

    override fun onResume() {
        super.onResume()
        viewModel.state.value?.run { updateProgress(this.state) }
    }

    private fun cancelProgress() {
        progressJob?.cancel()
        progressJob = null
        Log.i("APP_LOGS", "isActive = false")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
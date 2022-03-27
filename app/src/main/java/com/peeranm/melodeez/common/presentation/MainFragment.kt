package com.peeranm.melodeez.common.presentation

import android.media.MediaMetadata
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.google.android.material.tabs.TabLayoutMediator
import com.peeranm.melodeez.R
import com.peeranm.melodeez.common.utils.FragStateAdapter
import com.peeranm.melodeez.common.utils.getBitmap
import com.peeranm.melodeez.common.utils.tabArray
import com.peeranm.melodeez.databinding.MainFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding: MainFragmentBinding
    get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private val controller get() = requireActivity().mediaController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.run {
            window.statusBarColor = ContextCompat.getColor(
                requireContext(),
                R.color.grey_800
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.miniPlayer.root.visibility = View.GONE
        binding.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val adapter = FragStateAdapter(requireActivity())
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabArray[position]
            }.attach()

            miniPlayer.imageviewMiniPlayPause.setOnClickListener {
                if (controller.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                    controller.transportControls.pause()
                } else {
                    controller.transportControls.play()
                }
            }
            miniPlayer.imageviewNextTrack.setOnClickListener {
                controller.transportControls.skipToNext()
            }
            miniPlayer.root.setOnClickListener { onMiniPlayerClick() }
        }


        viewModel.state.observe(viewLifecycleOwner) {
            binding.miniPlayer.root.visibility = View.VISIBLE
            when (it.state) {
                PlaybackStateCompat.STATE_PAUSED -> {
                    binding.miniPlayer.imageviewMiniPlayPause.setImageResource(R.drawable.ic_play_copy)
                }
                PlaybackStateCompat.STATE_PLAYING -> {
                    binding.miniPlayer.imageviewMiniPlayPause.setImageResource(R.drawable.ic_pause_copy)
                }
                PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                    binding.miniPlayer.imageviewMiniPlayPause.setImageResource(R.drawable.ic_play_copy)
                }
                PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> {
                    binding.miniPlayer.imageviewMiniPlayPause.setImageResource(R.drawable.ic_play_copy)
                }
                PlaybackStateCompat.STATE_STOPPED -> {
                    binding.miniPlayer.root.visibility = View.GONE
                }
                else -> {}
            }
        }

        viewModel.metadata.observe(viewLifecycleOwner) {
            binding.apply {
                miniPlayer.textviewCurrentTrackTitle.text = it.getText(MediaMetadata.METADATA_KEY_TITLE)
                miniPlayer.textviewCurrentTrackArtist.text = it.getText(MediaMetadata.METADATA_KEY_ARTIST)
                val albumArtRef = it.getText(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
                if (albumArtRef != null) {
                    val imageLoadRequest = ImageRequest.Builder(requireContext()).apply {
                        transformations(
                            RoundedCornersTransformation(
                                topLeft = 20f,
                                topRight = 20f,
                                bottomLeft = 20f,
                                bottomRight = 20f,
                            )
                        )
                        data(
                            requireContext().getBitmap(
                                File(requireContext().filesDir, "albumarts"),
                                albumArtRef.toString()
                            )
                        )
                        target(miniPlayer.imageviewCurrentTrackAlbumart)
                    }.build()
                    lifecycleScope.launch { requireContext().imageLoader.execute(imageLoadRequest) }
                } else miniPlayer.imageviewCurrentTrackAlbumart.load(R.drawable.ic_music)
            }
        }
    }

    private fun onMiniPlayerClick() {
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToPlayerFragment()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
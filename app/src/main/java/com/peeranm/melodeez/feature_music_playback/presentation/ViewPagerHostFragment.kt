package com.peeranm.melodeez.feature_music_playback.presentation

import android.media.MediaMetadata
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.imageLoader
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import com.peeranm.melodeez.R
import com.peeranm.melodeez.feature_music_playback.utils.adapters.FragStateAdapter
import com.peeranm.melodeez.core.utils.tabArray
import com.peeranm.melodeez.databinding.ViewPagerHostFragmentBinding
import com.peeranm.melodeez.feature_music_playback.utils.collectWithLifecycle
import com.peeranm.melodeez.feature_music_playback.utils.getImageRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ViewPagerHostFragment : Fragment() {

    private var _binding: ViewPagerHostFragmentBinding? = null
    private val binding: ViewPagerHostFragmentBinding
    get() = _binding!!

    private val viewModel: PagerHostViewModel by viewModels()
    private val controller get() = requireActivity().mediaController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewPagerHostFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().run {
            window.statusBarColor = ContextCompat.getColor(
                requireContext(),
                R.color.grey_800
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setupViewPager()

        binding.apply {
            miniPlayer.btnPlayPause.setOnClickListener {
                if (controller.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                    controller.transportControls.pause()
                } else {
                    controller.transportControls.play()
                }
            }
            miniPlayer.btnNext.setOnClickListener {
                controller.transportControls.skipToNext()
            }
            miniPlayer.root.setOnClickListener {
                findNavController().navigate(
                    ViewPagerHostFragmentDirections.actionMainFragmentToPlayerFragment()
                )
            }
        }


        collectWithLifecycle(viewModel.state) {
            binding.miniPlayer.root.visibility = View.VISIBLE
            when (it?.state) {
                PlaybackStateCompat.STATE_PAUSED -> {
                    binding.miniPlayer.btnPlayPause.setImageResource(R.drawable.ic_play_copy)
                }
                PlaybackStateCompat.STATE_PLAYING -> {
                    binding.miniPlayer.btnPlayPause.setImageResource(R.drawable.ic_pause_copy)
                }
                PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                    binding.miniPlayer.btnPlayPause.setImageResource(R.drawable.ic_play_copy)
                }
                PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> {
                    binding.miniPlayer.btnPlayPause.setImageResource(R.drawable.ic_play_copy)
                }
                else -> binding.miniPlayer.root.visibility = View.GONE
            }
        }

        collectWithLifecycle(viewModel.metadata) {
            binding.miniPlayer.textTitle.text = it?.getText(MediaMetadata.METADATA_KEY_TITLE)
            binding.miniPlayer.textArtist.text = it?.getText(MediaMetadata.METADATA_KEY_ARTIST)
            val albumArtRef = it?.getText(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
            if (albumArtRef != null) {
                val imageLoadRequest = requireContext().getImageRequest(
                    albumArtRef.toString(),
                    binding.miniPlayer.imageAlbumArt
                )
                lifecycleScope.launch { requireContext().imageLoader.execute(imageLoadRequest) }
            } else {
                binding.miniPlayer.imageAlbumArt.load(R.drawable.ic_music)
            }
        }
    }

    private fun setupViewPager() {
        val adapter = FragStateAdapter(requireActivity())
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabArray[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
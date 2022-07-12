package com.peeranm.melodeez.feature_music_playback.presentation.now_playing

import android.media.session.MediaController
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.KIND_NOW_PLAYING
import com.peeranm.melodeez.core.MEDIA_POSITION
import com.peeranm.melodeez.core.showToast
import com.peeranm.melodeez.databinding.NowPlayingDialogBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.utils.adapters.NowPlayingAdapter
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackSourceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NowPlayingDialog : BottomSheetDialogFragment(),OnItemClickListener<Track> {

    @Inject lateinit var playbackSourceHelper: PlaybackSourceHelper

    private var _binding: NowPlayingDialogBinding? = null
    private val binding: NowPlayingDialogBinding
    get() = _binding!!

    private var adapter: NowPlayingAdapter? = null

    private val controls: MediaController.TransportControls
    get() = requireActivity().mediaController.transportControls

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NowPlayingDialogBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NowPlayingAdapter(requireContext(), this)
        binding.listNowPlaying.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.listNowPlaying.layoutManager = layoutManager
        binding.listNowPlaying.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))

        playbackSourceHelper.getCurrentSource().let {
            if (it.isEmpty()) showToast("No tracks in queue")
            else adapter?.submitData(it)
        }
    }

    override fun onItemClick(view: View?, data: Track, position: Int) {
        when (view?.id) {
            R.id.btnClear -> {
                val currentSource = playbackSourceHelper.getCurrentSource() as MutableList
                currentSource.removeAt(position)
                playbackSourceHelper.setCurrentSource(currentSource)
                when {
                    position == playbackSourceHelper.getCurrentTrackPosition() -> {
                        if (playbackSourceHelper.getCurrentSourceSize() > 0) {
                            val keyBundle = Bundle()
                            keyBundle.putInt(MEDIA_POSITION, 0)
                            controls.playFromMediaId(KIND_NOW_PLAYING, keyBundle)
                        } else controls.stop()
                    }
                    position < playbackSourceHelper.getCurrentTrackPosition() -> {
                        val currentTrackPosition = playbackSourceHelper.getCurrentTrackPosition()
                        playbackSourceHelper.setTrackPosition(currentTrackPosition-1)
                    }
                }
                adapter?.notifyItemRemoved(position)
            }
            else -> {
                val keyBundle = Bundle()
                keyBundle.putInt(MEDIA_POSITION, position)
                controls.playFromMediaId(KIND_NOW_PLAYING, keyBundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        adapter = null
        _binding = null
    }
}
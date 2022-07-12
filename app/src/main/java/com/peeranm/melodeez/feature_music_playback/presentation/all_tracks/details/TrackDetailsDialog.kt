package com.peeranm.melodeez.feature_music_playback.presentation.all_tracks.details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.peeranm.melodeez.core.showToast
import com.peeranm.melodeez.databinding.TrackDetailsDialogBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.select_playlist_dialog.SelectPlaylistDialog
import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackSourceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackDetailsDialog(private val track: Track) : DialogFragment() {

    @Inject lateinit var playbackSourceHelper: PlaybackSourceHelper
    private var _binding: TrackDetailsDialogBinding? = null
    private val binding: TrackDetailsDialogBinding
    get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(track.title)
            setView(getDialogView())
        }.create()
    }

    private fun getDialogView(): View {
        _binding = TrackDetailsDialogBinding.inflate(layoutInflater)
        binding.bindTrack()
        binding.handleOnAddPlaylistClick()
        binding.handleOnAddQueueClick()
        return binding.root
    }

    private fun TrackDetailsDialogBinding.bindTrack() {
        textAlbum.text = track.album
        textArtist.text = track.artist
    }


    private fun TrackDetailsDialogBinding.handleOnAddPlaylistClick() {
        textAddToPlaylist.setOnClickListener {
            SelectPlaylistDialog.getInstance(track).show(childFragmentManager, "ADD_TO_PLAYLIST")
        }
    }

    private fun TrackDetailsDialogBinding.handleOnAddQueueClick() {
        textAddToPlaylist.setOnClickListener {
            val tracks = playbackSourceHelper.getCurrentSource()
            if (tracks.isEmpty()) {
                showToast("Failed: Queue is empty!")
                return@setOnClickListener
            }
            if (tracks.contains(track)) {
               showToast("Already added!")
                return@setOnClickListener
            }
            (tracks as MutableList).add(track)
            playbackSourceHelper.setCurrentSource(tracks)
            showToast("Added to queue!")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun getInstance(track: Track) = TrackDetailsDialog(track)
    }
}
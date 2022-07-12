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

        binding.apply {
            textAlbum.text = track.album
            textArtist.text = track.artist

            textAddToPlaylist.setOnClickListener {
                SelectPlaylistDialog.getInstance(track).show(childFragmentManager, "ADD_TO_PLAYLIST")
            }

            textAddToQueue.setOnClickListener {
                dismiss()
                val tracks = playbackSourceHelper.getCurrentSource()
                val isNotEmpty = (tracks.isNotEmpty())
                val trackNotExists = !tracks.contains(track)
                if (isNotEmpty && trackNotExists) {
                    (tracks as MutableList).add(track)
                    playbackSourceHelper.setCurrentSource(tracks)
                    showToast("Added to queue")
                    return@setOnClickListener
                }
                showToast("Failed : queue is empty")
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun getInstance(track: Track) = TrackDetailsDialog(track)
    }
}
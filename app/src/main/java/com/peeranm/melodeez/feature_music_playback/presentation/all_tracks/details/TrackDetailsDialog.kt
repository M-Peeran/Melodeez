package com.peeranm.melodeez.feature_music_playback.presentation.all_tracks.details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.peeranm.melodeez.databinding.TrackDetailsDialogBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.select_playlist_dialog.SelectPlaylistDialog
import com.peeranm.melodeez.feature_music_playback.utils.collectWithLifecycle
import com.peeranm.melodeez.feature_music_playback.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackDetailsDialog(private val track: Track) : DialogFragment() {

    private var _binding: TrackDetailsDialogBinding? = null
    private val binding: TrackDetailsDialogBinding
    get() = _binding!!

    private val viewModel: TrackDetailsViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(track.title)
            setView(getDialogView())
        }.create()
    }

    private fun getDialogView(): View {
        _binding = TrackDetailsDialogBinding.inflate(layoutInflater)

        collectWithLifecycle(viewModel.isSuccessful) { isSuccessful ->
            isSuccessful?.let {
                if (isSuccessful) {
                    requireContext().showToast("Added to queue")
                } else {
                    requireContext().showToast("Failed : queue is empty")
                }
            }
        }
        binding.apply {
            textAlbum.text = track.album
            textArtist.text = track.artist

            textAddToPlaylist.setOnClickListener {
                SelectPlaylistDialog.getInstance(track).show(childFragmentManager, "ADD_TO_PLAYLIST")
            }

            textAddToQueue.setOnClickListener {
                dismiss()
                viewModel.onEvent(Event.AddToQueue(track))
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
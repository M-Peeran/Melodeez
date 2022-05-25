package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.details_dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.peeranm.melodeez.databinding.PlaylistDetailsDialogBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.utils.collectWithLifecycle
import com.peeranm.melodeez.feature_music_playback.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailsDialog(
    private val track: Track,
    private val playlistId: Long,
) : DialogFragment() {

    private var _binding: PlaylistDetailsDialogBinding? = null
    private val binding: PlaylistDetailsDialogBinding
    get() = _binding!!

    private val viewModel: PlaylistDetailsDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(track.title)
            setView(getDialogView())
        }.create()
    }

    private fun getDialogView(): View {
        _binding = PlaylistDetailsDialogBinding.inflate(layoutInflater)
        binding.apply {
            textAlbum.text = track.album
            textArtist.text = track.artist

            textDeleteFromPlaylist.setOnClickListener {
                dismiss()
                viewModel.onEvent(Event.DeleteTrackFromPlaylist(playlistId, track.trackId))
                requireContext().showToast("Deleted successfully!")
            }

            textAddToQueue.setOnClickListener {
                dismiss()
                viewModel.onEvent(Event.AddToQueue(track))
            }

            collectWithLifecycle(viewModel.isSuccessful) { isSuccessful ->
                isSuccessful?.let {
                    if (it) {
                        requireContext().showToast("Added to queue")
                    } else {
                        requireContext().showToast("Failed : queue is empty")
                    }
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun getInstance(
            track: Track,
            playlistId: Long,
        ) = PlaylistDetailsDialog(track, playlistId)
    }
}
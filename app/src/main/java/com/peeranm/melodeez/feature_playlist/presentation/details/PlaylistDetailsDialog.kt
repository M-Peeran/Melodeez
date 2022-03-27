package com.peeranm.melodeez.feature_playlist.presentation.details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.peeranm.melodeez.common.utils.NavFromDestinations
import com.peeranm.melodeez.common.utils.PlaylistEvents
import com.peeranm.melodeez.databinding.PlaylistDetailsDialogBinding
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_music_playback.presentation.now_playing.NowPlayingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistDetailsDialog(
    private val track: Track,
    private val playlistKey: String,
    private val navFrom: NavFromDestinations
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
            textviewPlaylistTrackDetailAlbum.text = track.album
            textviewPlaylistTrackDetailArtist.text = track.artist

            textviewPlaylistTrackDetailAlbum.setOnClickListener {
                dismiss()
                when (navFrom) {
                    NavFromDestinations.PlaylistDetails -> findNavController().navigate(
                        PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToAlbumDetailsFragment(
                            track.album
                        )
                    )
                    else -> {}
                }
            }

            textviewPlaylistTrackDetailArtist.setOnClickListener {
                dismiss()
                when (navFrom) {
                    NavFromDestinations.PlaylistDetails -> findNavController().navigate(
                        PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToArtistDetailsFragment(
                            track.artist
                        )
                    )
                    else -> {}
                }
            }

            textviewDeleteFromPlaylist.setOnClickListener {
                dismiss()
                requireActivity().lifecycleScope.launch {
                    viewModel.deleteTrackFromPlaylist(playlistKey, track.uri.toString())
                    //viewModel.setEvent(PlaylistEvents.Deleted) ???
                    Toast.makeText(
                        requireContext(),
                        "Deleted successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            textviewPlaylistTrackAddToQueue.setOnClickListener {
                dismiss()
                if (viewModel.addToQueue(track)) {
                    Toast.makeText(requireContext(), "Added to queue", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed : queue is empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun getInstance(
            track: Track,
            playlistKey: String,
            navFrom: NavFromDestinations
        ) = PlaylistDetailsDialog(track, playlistKey, navFrom)
    }
}
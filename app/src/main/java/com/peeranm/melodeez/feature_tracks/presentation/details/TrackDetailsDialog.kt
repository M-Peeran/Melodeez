package com.peeranm.melodeez.feature_tracks.presentation.details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.peeranm.melodeez.common.presentation.MainFragmentDirections
import com.peeranm.melodeez.common.utils.NavFromDestinations
import com.peeranm.melodeez.databinding.TrackDetailsDialogBinding
import com.peeranm.melodeez.feature_playlist.presentation.details.PlaylistDetailsFragmentDirections
import com.peeranm.melodeez.feature_playlist.presentation.details.SelectPlaylistDialog
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks_by_album.presentation.details.AlbumDetailsFragmentDirections
import com.peeranm.melodeez.feature_tracks_by_artist.presentation.details.ArtistDetailsFragmentDirections
import com.peeranm.melodeez.feature_music_playback.presentation.now_playing.NowPlayingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackDetailsDialog(
    private val track: Track,
    private val navFrom: NavFromDestinations
) : DialogFragment() {

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
        binding.apply {
            textviewTrackDetailAlbum.text = track.album
            textviewTrackDetailArtist.text = track.artist

            textviewTrackDetailAlbum.setOnClickListener {
                dismiss()
                when (navFrom) {
                    NavFromDestinations.Main -> findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToAlbumDetailsFragment(
                            track.album
                        )
                    )
                    NavFromDestinations.ArtistDetails -> findNavController().navigate(
                        ArtistDetailsFragmentDirections.actionArtistDetailsFragmentToAlbumDetailsFragment(
                            track.album
                        )
                    )
                    NavFromDestinations.PlaylistDetails -> findNavController().navigate(
                        PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToAlbumDetailsFragment(
                            track.album
                        )
                    )
                    else -> {}
                }
            }

            textviewTrackDetailArtist.setOnClickListener {
                dismiss()
                when (navFrom) {
                    NavFromDestinations.Main -> findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToArtistDetailsFragment(
                            track.artist
                        )
                    )
                    NavFromDestinations.AlbumDetails -> findNavController().navigate(
                        AlbumDetailsFragmentDirections.actionAlbumDetailsFragmentToArtistDetailsFragment(
                            track.artist
                        )
                    )
                    NavFromDestinations.PlaylistDetails -> findNavController().navigate(
                        PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToArtistDetailsFragment(
                            track.artist
                        )
                    )
                    else -> {}
                }
            }

            textiewTrackAddToPlaylist.setOnClickListener {
                SelectPlaylistDialog.getInstance(track).show(
                    childFragmentManager, "ADD_TO_PLAYLIST"
                )
            }

            textviewTrackAddToQueue.setOnClickListener {
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
        fun getInstance(track: Track, navFrom: NavFromDestinations) = TrackDetailsDialog(track, navFrom)
    }
}
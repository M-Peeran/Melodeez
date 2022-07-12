package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.select_playlist_dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.peeranm.melodeez.core.collectWithLifecycle
import com.peeranm.melodeez.core.showToast
import com.peeranm.melodeez.databinding.SelectPlaylistDialogBinding
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.utils.adapters.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectPlaylistDialog(private val track: Track) : DialogFragment(), OnCheckChangeListener<Playlist> {

    private var _binding: SelectPlaylistDialogBinding? = null
    private val binding: SelectPlaylistDialogBinding
    get() = _binding!!

    private var adapter: SelectPlaylistAdapter? = null

    private val viewModel: SelectPlaylistDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Select Playlist")
            .setView(getDialogView())
            .create()
    }

    private fun getDialogView(): View {
        _binding = SelectPlaylistDialogBinding.inflate(layoutInflater)
        adapter = SelectPlaylistAdapter(this)
        binding.listPlaylists.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.listPlaylists.layoutManager = layoutManager
        binding.listPlaylists.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        collectWithLifecycle(viewModel.playlists) {
            if (it != null) {
                if (it.isEmpty()) {
                    dismiss()
                    showToast("No playlists available, create one!")
                } else adapter?.submitData(it)
            }
        }
        viewModel.onEvent(Event.GetPlaylists)
    }

    override fun onCheckChange(
        compButton: CompoundButton?,
        data: Playlist,
        isSelected: Boolean,
        position: Int
    ) {
        if (isSelected) {
            dismiss()
            viewModel.onEvent(Event.InsertTrackToPlaylist(data.playlistId, track.trackId))
            showToast("Added ${track.title} to ${data.name} successfully!")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        adapter = null
        _binding = null
    }

    companion object {
        fun getInstance(track: Track) = SelectPlaylistDialog(track)
    }
}
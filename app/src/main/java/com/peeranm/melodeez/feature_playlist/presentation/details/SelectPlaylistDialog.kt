package com.peeranm.melodeez.feature_playlist.presentation.details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.common.utils.BaseAdapter
import com.peeranm.melodeez.common.utils.BaseHolder
import com.peeranm.melodeez.databinding.SelectPlaylistDialogBinding
import com.peeranm.melodeez.databinding.SelectPlaylistItemBinding
import com.peeranm.melodeez.feature_playlist.model.Playlist
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_playlist.presentation.PlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectPlaylistDialog(private val track: Track) : DialogFragment() {

    private var _binding: SelectPlaylistDialogBinding? = null
    private val binding: SelectPlaylistDialogBinding
    get() = _binding!!

    private val viewModel: PlaylistDetailsDialogViewModel by viewModels()

    private val diffCallback = object : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist)
                = oldItem == newItem
        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.noOfTracks == newItem.noOfTracks
        }
    }

    private val adapter = object: BaseAdapter<Playlist>(diffCallback) {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return object: BaseHolder<SelectPlaylistItemBinding>(
                SelectPlaylistItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            ) {
                override fun onInitializeViewHolder(rootView: View) {
                    binding.radiobuttonSelect.setOnCheckedChangeListener { compoundButton, isSelected ->
                        if (isSelected) {
                            dismiss()
                            requireActivity().lifecycleScope.launch {
                                val playlist = getItem(adapterPosition)
                                viewModel.insertTrackToPlaylist(playlist, track)
                                Toast.makeText(
                                    requireContext(),
                                    "Added ${track.title} to ${playlist.name} successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as BaseHolder<SelectPlaylistItemBinding>
            val playlist = getItem(position)
            holder.binding.textviewPlaylistName.text = playlist.name
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle("Select Playlist")
            setView(getDialogView())
        }.create()
    }

    private fun getDialogView(): View {
        _binding = SelectPlaylistDialogBinding.inflate(layoutInflater)
        binding.recyclerviewSelectPlaylist.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val playlists = viewModel.getPlaylists()
            if (playlists.isEmpty()) {
                dismiss()
                Toast.makeText(
                    requireContext(),
                    "No playlists available, create one!",
                    Toast.LENGTH_SHORT
                ).show()
            } else adapter.submitList(playlists)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun getInstance(track: Track) = SelectPlaylistDialog(track)
    }
}
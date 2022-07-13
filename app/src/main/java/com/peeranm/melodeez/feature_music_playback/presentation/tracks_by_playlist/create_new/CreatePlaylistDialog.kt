package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.create_new

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.peeranm.melodeez.core.collectWithLifecycle
import com.peeranm.melodeez.databinding.CreatePlaylistDialogBinding
import com.peeranm.melodeez.feature_music_playback.presentation.ViewPagerHostFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePlaylistDialog : DialogFragment() {

    private val viewModel: CreatePlaylistViewModel by viewModels()

    private var _binding: CreatePlaylistDialogBinding? = null
    private val binding: CreatePlaylistDialogBinding
    get() = _binding!!

    private val enteredText: String
    get() = binding.etextEnterTitle.text.toString()

    private val positiveButton: Button
    get() = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

    private val negativeButton: Button
    get() = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle("Create New Playlist")
            setView(getDialogView())
            setPositiveButton("OK", null)
            setNegativeButton("Cancel", null)
        }.create()
    }

    override fun onResume() {
        super.onResume()

        positiveButton.setOnClickListener {
            if (enteredText.isNotEmpty() && enteredText.isNotBlank()) {
                viewModel.createPlaylist(enteredText)
            } else binding.etextEnterTitle.error = "Name cannot be empty!"
        }

        negativeButton.setOnClickListener { dismiss() }
    }

    private fun getDialogView(): View {
        _binding = CreatePlaylistDialogBinding.inflate(layoutInflater)

        collectWithLifecycle(viewModel.uiAction) { uiAction ->
            when (uiAction) {
                is UiAction.NavigateWithPlaylistId -> {
                    dismiss()
                    findNavController().navigate(
                        ViewPagerHostFragmentDirections.actionMainFragmentToAddTracksFragment(uiAction.playlistId)
                    )
                }
                else -> Unit
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun getInstance() = CreatePlaylistDialog()
    }
}
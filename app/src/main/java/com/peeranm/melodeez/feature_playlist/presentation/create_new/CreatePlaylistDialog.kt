package com.peeranm.melodeez.feature_playlist.presentation.create_new

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.peeranm.melodeez.common.presentation.MainFragmentDirections
import com.peeranm.melodeez.databinding.CreatePlaylistDialogBinding
import com.peeranm.melodeez.feature_playlist.model.Playlist
import com.peeranm.melodeez.feature_playlist.presentation.PlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePlaylistDialog : DialogFragment() {

    private val viewModel: CreatePlaylistViewModel by activityViewModels()
    private lateinit var binding: CreatePlaylistDialogBinding

    private val enteredText: String
    get() = binding.editTextEnterTitle.text.toString()

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
                dismiss()
                requireActivity().lifecycleScope.launch { viewModel.insertPlaylist(Playlist(enteredText)) }
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToAddTracksFragment(enteredText)
                )
            } else binding.editTextEnterTitle.error = "Name cannot be empty!"
        }

        negativeButton.setOnClickListener { dismiss() }
    }

    private fun getDialogView(): View {
        binding = CreatePlaylistDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        fun getInstance() = CreatePlaylistDialog()
    }
}
package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.add_track

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.peeranm.melodeez.core.collectWithLifecycle
import com.peeranm.melodeez.core.showToast
import com.peeranm.melodeez.databinding.AddTracksFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.utils.adapters.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTracksFragment : Fragment(), OnCheckChangeListener<Track> {

    private var _binding: AddTracksFragmentBinding? = null
    private val binding: AddTracksFragmentBinding
    get() = _binding!!

    private val viewModel: AddTracksViewModel by viewModels()
    private val args: AddTracksFragmentArgs by navArgs()

    private var adapter: SelectableTrackAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddTracksFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setupToolbar()
            bindList()
            toggleProgressbarVisibility(true)
            handleOnBtnAddSelectedClick()
            handleOnBtnCancelClick()
        }

        collectWithLifecycle(viewModel.isAnyTrackSelected) {
            binding.btnAddSelected.isEnabled = it
        }

        collectWithLifecycle(viewModel.tracksState) { tracks ->
            binding.toggleProgressbarVisibility()
            if (tracks.isNotEmpty()) adapter?.submitData(tracks)
        }
    }

    private fun AddTracksFragmentBinding.handleOnBtnAddSelectedClick() {
        btnAddSelected.setOnClickListener {
            viewModel.onEvent(Event.AddSelectedTracksToPlaylist(args.playlistId))
            showToast("Added successfully")
            findNavController().navigateUp()
        }
    }

    private fun AddTracksFragmentBinding.handleOnBtnCancelClick() {
        btnCancel.setOnClickListener {
            viewModel.onEvent(Event.Clear)
            findNavController().navigateUp()
        }
    }

    private fun AddTracksFragmentBinding.bindList() {
        adapter = SelectableTrackAdapter(requireContext(), this@AddTracksFragment)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        listTracks.adapter = adapter
        listTracks.layoutManager = layoutManager
        listTracks.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
    }

    private fun AddTracksFragmentBinding.setupToolbar() {
        findNavController().let { navController ->
            val appConfig = AppBarConfiguration(navController.graph)
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            toolbar.setupWithNavController(navController, appConfig)
            toolbar.setNavigationOnClickListener { navController.navigateUp() }
        }
        toolbar.title = "Select Tracks"
    }

    private fun AddTracksFragmentBinding.toggleProgressbarVisibility(showNow: Boolean = false) {
        progressBar.visibility = if (showNow) View.VISIBLE else View.GONE
    }

    override fun onCheckChange(
        compButton: CompoundButton?,
        data: Track,
        isSelected: Boolean,
        position: Int
    ) { viewModel.onEvent(Event.ToggleTrackSelection(data.trackId, isSelected)) }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        adapter = null
        _binding = null
    }
}
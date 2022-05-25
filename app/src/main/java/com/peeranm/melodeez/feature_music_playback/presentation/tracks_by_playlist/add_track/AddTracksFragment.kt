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
import com.peeranm.melodeez.core.utils.DataState
import com.peeranm.melodeez.databinding.AddTracksFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.utils.adapters.*
import com.peeranm.melodeez.feature_music_playback.utils.collectWithLifecycle
import com.peeranm.melodeez.feature_music_playback.utils.showToast
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
            adapter = SelectableTrackAdapter(requireContext(), this@AddTracksFragment)
            findNavController().let { navController ->
                val appConfig = AppBarConfiguration(navController.graph)
                (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
                toolbar.setupWithNavController(navController, appConfig)
                toolbar.setNavigationOnClickListener { navController.navigateUp() }
            }
            toolbar.title = "Select Tracks"
            listTracks.adapter = adapter
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            listTracks.layoutManager = layoutManager
            listTracks.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
            binding.progressBar.visibility = View.VISIBLE

            btnAddSelected.setOnClickListener {
                viewModel.onEvent(Event.AddSelectedTracksToPlaylist(args.playlistId))
                requireContext().showToast("Added successfully")
                findNavController().navigateUp()
            }

            btnCancel.setOnClickListener {
                viewModel.onEvent(Event.Clear)
                findNavController().navigateUp()
            }
        }

        collectWithLifecycle(viewModel.isAnyTrackSelected) {
            binding.btnAddSelected.isEnabled = it
        }

        collectWithLifecycle(viewModel.tracksState) { tracks ->
            if (tracks.isNotEmpty()) {
                binding.progressBar.visibility = View.GONE
                adapter?.submitData(tracks)
            }
        }
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
package com.peeranm.melodeez.feature_playlist.presentation.add_track

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.common.utils.BaseAdapter
import com.peeranm.melodeez.common.utils.BaseHolder
import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.databinding.AddTracksFragmentBinding
import com.peeranm.melodeez.databinding.TracksListSelectableItemBinding
import com.peeranm.melodeez.feature_tracks.model.Track
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddTracksFragment : Fragment() {

    private var _binding: AddTracksFragmentBinding? = null
    private val binding: AddTracksFragmentBinding
    get() = _binding!!

    private val viewModel: AddTracksViewModel by viewModels()
    private val args: AddTracksFragmentArgs by navArgs()

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
        setOkButtonState(false)

        val diffCallback = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.uri == newItem.uri &&
                        oldItem.title == newItem.title &&
                        oldItem.album == newItem.album &&
                        oldItem.artist == newItem.artist &&
                        oldItem.duration == newItem.duration
            }
        }

        val adapter = object : BaseAdapter<Track>(diffCallback) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : BaseHolder<TracksListSelectableItemBinding>(
                    TracksListSelectableItemBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                ) {
                    override fun onInitializeViewHolder(rootView: View) {
                        binding.checkboxSelection.setOnCheckedChangeListener { buttonView, isSelected ->
                            val track = getItem(adapterPosition)
                            if (!viewModel.isAlreadyExists(track.uri) && isSelected) {
                                viewModel.addToSelectedTracks(track.uri, true)
                            } else if (!isSelected) {
                                viewModel.removeFromSelectedTracks(track.uri)
                            }
                            setOkButtonState(viewModel.isAnyTrackSelected())
                        }
                    }
                }
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder as BaseHolder<TracksListSelectableItemBinding>
                val track = getItem(position)
                holder.binding.apply {
                    checkboxSelection.isChecked = viewModel.isAlreadyExists(track.uri)
                    textviewTitle.text = track.title
                    textviewAlbumArtist.text = "${track.artist} - ${track.album}"
                }
            }
        }
        binding.apply {
            findNavController().let { navController ->
                val appConfig = AppBarConfiguration(navController.graph)
                (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarAddTracks)
                toolbarAddTracks.setupWithNavController(navController, appConfig)
                toolbarAddTracks.setNavigationOnClickListener { navController.navigateUp() }
            }
            toolbarAddTracks.title = "Select Tracks"
            recyclerviewAllTracks.adapter = adapter
            buttonAddAll.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.addAll(args.playlistKey)
                    Toast.makeText(
                        requireContext(),
                        "Added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
            }

            buttonCancelSelection.setOnClickListener {
                viewModel.removeAll()
                findNavController().navigateUp()
            }
        }
        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Loading -> {}
                is DataState.Success -> adapter.submitList(dataState.data)
                else -> Log.i("APP_LOGS", "SOMETHING ELSE HAPPENED : $dataState")
            }
        }
        viewModel.loadTracks()
    }

    private fun setOkButtonState(isEnabled: Boolean) {
        binding.buttonAddAll.isEnabled = isEnabled
    }
}
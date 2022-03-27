package com.peeranm.melodeez.feature_tracks.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.media.session.MediaController
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import coil.request.ImageRequest
import com.peeranm.melodeez.R
import com.peeranm.melodeez.common.presentation.MainFragmentDirections
import com.peeranm.melodeez.common.utils.*
import com.peeranm.melodeez.databinding.AllTracksFragmentBinding
import com.peeranm.melodeez.databinding.TracksListItemBinding
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.presentation.details.TrackDetailsDialog
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class AllTracksFragment : Fragment() {

    private lateinit var binding: AllTracksFragmentBinding
    private val viewModel: AllTracksViewModel by viewModels()

    private val controls: MediaController.TransportControls
    get() = requireActivity().mediaController.transportControls

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AllTracksFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            viewModel.setStateEvent(StateEvent.Synchronize)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            val permissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    viewModel.setStateEvent(StateEvent.Synchronize)
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
            }
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

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
                return object : BaseHolder<TracksListItemBinding>(
                    TracksListItemBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                ) {
                    override fun onInitializeViewHolder(rootView: View) {
                        rootView.setOnClickListener {
                            val keyBundle = Bundle()
                            keyBundle.putInt(MEDIA_POSITION, adapterPosition)
                            controls.playFromMediaId(KIND_TRACKS_COLLECTION, keyBundle)
                            findNavController().navigate(
                                MainFragmentDirections.actionMainFragmentToPlayerFragment()
                            )
                        }
                        binding.imageviewOptions.setOnClickListener {
                            val track = getItem(adapterPosition)
                            TrackDetailsDialog.getInstance(track, NavFromDestinations.Main).show(
                                childFragmentManager, "TRACK"
                            )
                        }
                    }
                }
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder as BaseHolder<TracksListItemBinding>
                val track = getItem(position)
                holder.binding.apply {
                    textviewTitle.text = track.title
                    textviewArtistAndAlbum.text = "${track.artist} - ${track.album}"
                    if (track.isAlbumArtAvailable) {
                        val imageLoadRequest = ImageRequest.Builder(requireContext()).apply {
                            data(
                                requireContext().getBitmap(
                                    File(requireContext().filesDir, "albumarts"),
                                    track.albumArtRef
                                )
                            )
                            target(imageviewAlbumart)
                        }.build()
                        lifecycleScope.launch { requireContext().imageLoader.execute(imageLoadRequest) }
                    } else imageviewAlbumart.load(R.drawable.ic_music)
                }
            }
        }

        binding.recyclerviewTracks.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }

                is DataState.Success -> {
                    binding.progressbar.visibility = View.GONE
                    adapter.submitList(dataState.data)
                }
                is DataState.SynchronizationCompleted -> {
                    viewModel.setStateEvent(StateEvent.Get)
                }

                is DataState.Synchronizing -> {
                    binding.progressbar.visibility = View.VISIBLE
                }

                is DataState.Failure -> {
                    binding.progressbar.visibility = View.GONE
                }
            }
        }
    }
}
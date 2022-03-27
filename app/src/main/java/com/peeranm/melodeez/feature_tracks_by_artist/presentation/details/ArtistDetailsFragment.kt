package com.peeranm.melodeez.feature_tracks_by_artist.presentation.details

import android.media.session.MediaController
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.common.utils.*
import com.peeranm.melodeez.databinding.ArtistDetailsFragmentBinding
import com.peeranm.melodeez.databinding.TracksListItemSimpleBinding
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.presentation.details.TrackDetailsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtistDetailsFragment : Fragment() {

    private val viewModel: ArtistDetailsViewModel by viewModels()
    private val args: ArtistDetailsFragmentArgs by navArgs()

    private var _binding: ArtistDetailsFragmentBinding? = null
    private val binding: ArtistDetailsFragmentBinding
    get() = _binding!!

    private val controls: MediaController.TransportControls
    get() = requireActivity().mediaController.transportControls


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ArtistDetailsFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                return object : BaseHolder<TracksListItemSimpleBinding>(
                    TracksListItemSimpleBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                ) {
                    override fun onInitializeViewHolder(rootView: View) {
                        rootView.setOnClickListener {
                            val keyBundle = Bundle()
                            keyBundle.putInt(MEDIA_POSITION, adapterPosition)
                            keyBundle.putString(MEDIA_KEY, args.artistKey)
                            controls.playFromMediaId(KIND_ARTIST, keyBundle)
                            findNavController().navigate(
                                ArtistDetailsFragmentDirections.actionArtistDetailsFragmentToPlayerFragment()
                            )
                        }
                        binding.imageviewOptions.setOnClickListener {
                            val track = getItem(adapterPosition)
                            TrackDetailsDialog.getInstance(track, NavFromDestinations.ArtistDetails)
                                .show(
                                childFragmentManager, "TRACK"
                            )
                        }
                    }
                }
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder as BaseHolder<TracksListItemSimpleBinding>
                val track = getItem(position)
                holder.binding.apply {
                    textviewTitle.text = track.title
                    textviewArtistAndAlbum.text = "${track.artist} - ${track.album}"
                }
            }
        }

        lifecycleScope.launch {
            val albumKey = args.artistKey
            val (artist, tracks) = viewModel.getArtistWithTracks(albumKey)
            binding.apply {
                findNavController().let {
                    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarArtistDetails)
                    toolbarArtistDetails.setupWithNavController(
                        it,
                        AppBarConfiguration.Builder()
                            .setFallbackOnNavigateUpListener {
                                it.navigateUp()
                                true
                            }.build()
                    )
                }
                toolbarArtistDetails.title = ""
                recyclerviewArtistTracks.adapter = adapter
                textviewDetailArtistName.text = artist.name
                textviewDetailNoOfTracks.text = "Tracks : ${tracks.size}"
            }
            adapter.submitList(tracks)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
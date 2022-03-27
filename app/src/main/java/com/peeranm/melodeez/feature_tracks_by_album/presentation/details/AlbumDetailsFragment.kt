package com.peeranm.melodeez.feature_tracks_by_album.presentation.details

import android.media.session.MediaController
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.peeranm.melodeez.R
import com.peeranm.melodeez.common.utils.*
import com.peeranm.melodeez.databinding.AlbumDetailsFragmentBinding
import com.peeranm.melodeez.databinding.TracksListItemSimpleBinding
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.presentation.details.TrackDetailsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class AlbumDetailsFragment : Fragment() {

    private val viewModel: AlbumDetailsViewModel by viewModels()
    private val args: AlbumDetailsFragmentArgs by navArgs()

    private var _binding: AlbumDetailsFragmentBinding? = null
    private val binding: AlbumDetailsFragmentBinding
    get() = _binding!!

    private val controls: MediaController.TransportControls
    get() = requireActivity().mediaController.transportControls

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AlbumDetailsFragmentBinding.inflate(
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
                            keyBundle.putString(MEDIA_KEY, args.albumKey)
                            controls.playFromMediaId(KIND_ALBUM, keyBundle)
                            findNavController().navigate(
                                AlbumDetailsFragmentDirections.actionAlbumDetailsFragmentToPlayerFragment()
                            )
                        }
                        binding.imageviewOptions.setOnClickListener {
                            val track = getItem(adapterPosition)
                            TrackDetailsDialog.getInstance(track, NavFromDestinations.AlbumDetails).show(
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
            val albumKey = args.albumKey
            val (album, tracks) = viewModel.getAlbumWithTracks(albumKey)
            binding.apply {
                findNavController().let {
                    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarAlbumDetails)
                    toolbarAlbumDetails.setupWithNavController(
                        it,
                        AppBarConfiguration.Builder()
                            .setFallbackOnNavigateUpListener {
                                it.navigateUp()
                                true
                            }.build()
                    )
                }
                recyclerviewAlbumTracks.adapter = adapter
                toolbarAlbumDetails.title = ""
                textviewDetailTitle.text = album.name
                textviewNoOfTracks.text = "Tracks : ${tracks.size}"
                textviewDetailReleaseYear.text = "Release Year : ${
                    if (album.releaseYear == UNKNOWN_RELEASE_YEAR) "Unknown Release Year" 
                    else album.releaseYear.toString()
                }"
                if (album.isAlbumArtAvailable) {
                    val imageLoadRequest = ImageRequest.Builder(requireContext()).apply {
                        transformations(
                            RoundedCornersTransformation(
                                topLeft = 20f,
                                topRight = 20f,
                                bottomLeft = 20f,
                                bottomRight = 20f,
                            )
                        )
                        data(
                            requireContext().getBitmap(
                                File(requireContext().filesDir, "albumarts"),
                                album.albumArtRef
                            )
                        )
                        target(imageviewDetailArt)
                    }.build()
                    lifecycleScope.launch { requireContext().imageLoader.execute(imageLoadRequest) }
                } else imageviewDetailArt.load(R.drawable.ic_music)
            }
            adapter.submitList(tracks)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
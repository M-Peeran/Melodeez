package com.peeranm.melodeez.feature_playlist.presentation.details

import android.media.session.MediaController
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.R
import com.peeranm.melodeez.common.utils.*
import com.peeranm.melodeez.databinding.PlaylistDetailsFragmentBinding
import com.peeranm.melodeez.databinding.TracksListItemSimpleBinding
import com.peeranm.melodeez.feature_tracks.model.Track
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistDetailsFragment : Fragment() {

    private val viewModel: PlaylistDetailsViewModel by viewModels()

    private var _binding: PlaylistDetailsFragmentBinding? = null
    private  val binding: PlaylistDetailsFragmentBinding
    get() = _binding!!

    private val controls: MediaController.TransportControls
    get() = requireActivity().mediaController.transportControls

    private val args: PlaylistDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlaylistDetailsFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

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
                            keyBundle.putString(MEDIA_KEY, args.playlistKey)
                            controls.playFromMediaId(KIND_PLAYLIST, keyBundle)
                            findNavController().navigate(
                                PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToPlayerFragment()
                            )
                        }
                        binding.imageviewOptions.setOnClickListener {
                            lifecycleScope.launch {
                                val track = getItem(adapterPosition)
                                PlaylistDetailsDialog.getInstance(
                                    track,
                                    args.playlistKey,
                                    NavFromDestinations.PlaylistDetails
                                ).show(childFragmentManager, "TRACK")
                            }
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
            val playlistKey = args.playlistKey
            val (playlist, tracks) = viewModel.getPlaylistWithTracks(playlistKey)
            binding.apply {
                findNavController().let { navController ->
                    val appConfig = AppBarConfiguration(navController.graph)
                    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarPlaylistDetails)
                    toolbarPlaylistDetails.setupWithNavController(navController, appConfig)
                    toolbarPlaylistDetails.setNavigationOnClickListener { navController.navigateUp() }
                }
                toolbarPlaylistDetails.title = ""
                recyclerviewPlaylistTracks.adapter = adapter
                textviewPlaylistDetailName.text = playlist.name
                textviewPlaylistDetailTracks.text = "Tracks : ${playlist.noOfTracks}"
                imageviewPlaylistDetailArt.setImageResource(R.drawable.ic_playlist)
            }
            adapter.submitList(tracks)
        }

//        viewModel.playlistEvent.observe(viewLifecycleOwner) { event ->
//            when (event) {
//                is PlaylistEvents.Deleted -> {
//                    lifecycleScope.launch {
//                        binding.apply {
//                            val playlistKey = args.playlistKey
//                            val (playlist, tracks) = viewModel.getPlaylistWithTracks(playlistKey)
//                            textviewPlaylistDetailName.text = playlist.name
//                            textviewPlaylistDetailTracks.text = "Tracks : ${playlist.noOfTracks}"
//                            adapter.submitList(tracks)
//                        }
//                        viewModel.setEvent(PlaylistEvents.Done)
//                    }
//                }
//                else -> {}
//            }
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_playlist_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_more -> {
                findNavController().navigate(
                    PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToAddTracksFragment(
                        args.playlistKey
                    )
                )
                true
            }
            R.id.action_delete_playlist -> {
                lifecycleScope.launch {
                    if (viewModel.deletePlaylist(args.playlistKey) != 0) {
                        Toast.makeText(
                            requireContext(),
                            "Deleted ${args.playlistKey} successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details

import android.media.session.MediaController
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.utils.*
import com.peeranm.melodeez.databinding.PlaylistDetailsFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.details_dialog.PlaylistDetailsDialog
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import com.peeranm.melodeez.feature_music_playback.utils.adapters.SimpleTrackAdapter
import com.peeranm.melodeez.feature_music_playback.utils.collectWithLifecycle
import com.peeranm.melodeez.feature_music_playback.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailsFragment : Fragment(), OnItemClickListener<Track> {

    private val viewModel: PlaylistDetailsViewModel by viewModels()

    private var _binding: PlaylistDetailsFragmentBinding? = null
    private  val binding: PlaylistDetailsFragmentBinding
    get() = _binding!!

    private var adapter: SimpleTrackAdapter? = null

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

        findNavController().let { navController ->
            val appConfig = AppBarConfiguration(navController.graph)
            (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
            binding.toolbar.setupWithNavController(navController, appConfig)
            binding.toolbar.setNavigationOnClickListener { navController.navigateUp() }
            binding.toolbar.title = ""
        }

        adapter = SimpleTrackAdapter(requireContext(), this@PlaylistDetailsFragment)

        collectWithLifecycle(viewModel.playlistWithTracks) {
            it?.let {
                val (playlist, tracks) = it
                binding.apply {
                    listTracksInPlaylist.adapter = adapter
                    val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.listTracksInPlaylist.layoutManager = layoutManager
                    binding.listTracksInPlaylist.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
                    textPlaylistName.text = playlist.name
                    textNoOfTracks.text = String.format(
                        getString(R.string.no_of_tracks),
                        playlist.noOfTracks
                    )
                    imageAlbumArt.setImageResource(R.drawable.ic_playlist)
                    adapter?.submitData(tracks)
                }
            }
        }

        collectWithLifecycle(viewModel.isDeletionSuccess) { isSuccess ->
            if (isSuccess != null && isSuccess) {
                showToast("Deleted successfully!")
                findNavController().navigateUp()
            }
        }

        viewModel.onEvent(Event.GetPlaylistWithTracks(args.playlistId))
    }

    override fun onItemClick(view: View?, data: Track, position: Int) {
        when (view?.id) {
            R.id.btnOptions -> {
                PlaylistDetailsDialog.getInstance(data, args.playlistId,).show(childFragmentManager, "TRACK")
            }
            else -> {
                val keyBundle = Bundle()
                keyBundle.putInt(MEDIA_POSITION, position)
                keyBundle.putLong(MEDIA_KEY, args.playlistId)
                controls.playFromMediaId(KIND_PLAYLIST, keyBundle)
                findNavController().navigate(
                    PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToPlayerFragment()
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_playlist_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionAddMore -> {
                findNavController().navigate(
                    PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToAddTracksFragment(args.playlistId)
                )
                true
            }
            R.id.actionDeletePlaylist -> {
                viewModel.onEvent(Event.DeletePlaylist(args.playlistId))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.onClear()
        adapter = null
        _binding = null
    }

}
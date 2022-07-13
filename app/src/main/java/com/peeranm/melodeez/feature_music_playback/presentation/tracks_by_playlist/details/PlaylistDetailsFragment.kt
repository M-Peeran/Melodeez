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
import com.peeranm.melodeez.core.*
import com.peeranm.melodeez.databinding.PlaylistDetailsFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.details_dialog.PlaylistDetailsDialog
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import com.peeranm.melodeez.feature_music_playback.utils.adapters.SimpleTrackAdapter
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
        binding.setupToolbar()
        binding.bindList()

        collectWithLifecycle(viewModel.playlistWithTracks) { playlistWithTracks ->
            binding.bindPlaylistAndTracks(playlistWithTracks.playlist, playlistWithTracks.tracks)
        }

        collectWithLifecycle(viewModel.isDeletionSuccess) { isSuccess ->
            if (isSuccess != null && isSuccess) {
                showToast("Deleted successfully!")
                findNavController().navigateUp()
            }
        }

        collectWithLifecycle(viewModel.message) { message ->
            if (message.isNotEmpty()) showToast(message)
        }
    }

    override fun onItemClick(view: View?, data: Track, position: Int) {
        when (view?.id) {
            R.id.btnOptions -> PlaylistDetailsDialog.getInstance(data, args.playlistId,).show(childFragmentManager, "TRACK")
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
                viewModel.deletePlaylist(args.playlistId)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun PlaylistDetailsFragmentBinding.setupToolbar() {
        findNavController().let { navController ->
            val appConfig = AppBarConfiguration(navController.graph)
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            toolbar.setupWithNavController(navController, appConfig)
            toolbar.setNavigationOnClickListener { navController.navigateUp() }
            toolbar.title = ""
        }
    }

    private fun PlaylistDetailsFragmentBinding.bindList() {
        adapter = SimpleTrackAdapter(requireContext(), this@PlaylistDetailsFragment)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        listTracksInPlaylist.adapter = adapter
        listTracksInPlaylist.layoutManager = layoutManager
        listTracksInPlaylist.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
    }

    private fun PlaylistDetailsFragmentBinding.bindPlaylistAndTracks(playlist: Playlist, tracks: List<Track>) {
        textPlaylistName.text = playlist.name
        textNoOfTracks.text = String.format(getString(R.string.no_of_tracks), playlist.noOfTracks)
        imageAlbumArt.setImageResource(R.drawable.ic_playlist)
        adapter?.submitData(tracks)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.onClear()
        adapter = null
        _binding = null
    }

}
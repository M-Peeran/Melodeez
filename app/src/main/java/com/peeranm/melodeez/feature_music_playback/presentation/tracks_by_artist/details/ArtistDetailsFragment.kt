package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_artist.details

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
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.*
import com.peeranm.melodeez.databinding.ArtistDetailsFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Artist
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.presentation.all_tracks.details.TrackDetailsDialog
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import com.peeranm.melodeez.feature_music_playback.utils.adapters.SimpleTrackAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtistDetailsFragment : Fragment(), OnItemClickListener<Track> {

    private val viewModel: ArtistDetailsViewModel by viewModels()
    private val args: ArtistDetailsFragmentArgs by navArgs()

    private var adapter: SimpleTrackAdapter? = null

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
        binding.setupToolbar()
        binding.bindList()

        collectWithLifecycle(viewModel.artistWithTracks) {
            binding.bindArtistAndTracks(it.artist, it.tracks)
        }

        collectWithLifecycle(viewModel.message) { message ->
            if (message.isNotEmpty()) showToast(message)
        }

    }

    override fun onItemClick(view: View?, data: Track, position: Int) {
        when (view?.id) {
            R.id.btnOptions -> TrackDetailsDialog.getInstance(data).show(childFragmentManager, "TRACK")
            else -> {
                val keyBundle = Bundle()
                keyBundle.putInt(MEDIA_POSITION, position)
                keyBundle.putLong(MEDIA_KEY, args.artistId)
                controls.playFromMediaId(KIND_ARTIST, keyBundle)
                findNavController().navigate(
                    ArtistDetailsFragmentDirections.actionArtistDetailsFragmentToPlayerFragment()
                )
            }
        }
    }

    private fun ArtistDetailsFragmentBinding.setupToolbar() {
        findNavController().let { navController ->
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            toolbar.setupWithNavController(
                navController,
                AppBarConfiguration.Builder().setFallbackOnNavigateUpListener {
                    navController.navigateUp()
                    true
                }.build()
            )
            toolbar.title = ""
        }
    }

    private fun ArtistDetailsFragmentBinding.bindList() {
        adapter = SimpleTrackAdapter(requireContext(), this@ArtistDetailsFragment)
        listTracksOfArtist.adapter = adapter
    }

    private fun ArtistDetailsFragmentBinding.bindArtistAndTracks(artist: Artist, tracks: List<Track>) {
        textArtistName.text = artist.name
        textNoOfTracks.text = String.format(getString(R.string.no_of_tracks), tracks.size)
        adapter?.submitData(tracks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        adapter = null
        _binding = null
    }
}
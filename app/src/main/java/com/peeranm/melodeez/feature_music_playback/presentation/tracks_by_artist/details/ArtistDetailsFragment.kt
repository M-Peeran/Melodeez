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
import com.peeranm.melodeez.core.KIND_ARTIST
import com.peeranm.melodeez.core.MEDIA_KEY
import com.peeranm.melodeez.core.MEDIA_POSITION
import com.peeranm.melodeez.databinding.ArtistDetailsFragmentBinding
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

        lifecycleScope.launch {
            adapter = SimpleTrackAdapter(requireContext(), this@ArtistDetailsFragment)
            val artistId = args.artistId
            val (artist, tracks) = viewModel.getArtistWithTracks(artistId)
            binding.apply {
                findNavController().let {
                    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
                    toolbar.setupWithNavController(
                        it,
                        AppBarConfiguration.Builder()
                            .setFallbackOnNavigateUpListener {
                                it.navigateUp()
                                true
                            }.build()
                    )
                }
                toolbar.title = ""
                listTracksOfArtist.adapter = adapter
                textArtistName.text = artist.name
                textNoOfTracks.text = String.format(getString(R.string.no_of_tracks), tracks.size)
                adapter?.submitData(tracks)
            }
        }
    }

    override fun onItemClick(view: View?, data: Track, position: Int) {
        when (view?.id) {
            R.id.btnOptions -> {
                TrackDetailsDialog.getInstance(data)
                    .show(
                        childFragmentManager, "TRACK"
                    )
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        adapter = null
        _binding = null
    }
}
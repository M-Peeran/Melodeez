package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_album.details

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.imageLoader
import coil.load
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.*
import com.peeranm.melodeez.databinding.AlbumDetailsFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.presentation.all_tracks.details.TrackDetailsDialog
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import com.peeranm.melodeez.feature_music_playback.utils.adapters.SimpleTrackAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlbumDetailsFragment : Fragment(), OnItemClickListener<Track> {

    private val viewModel: AlbumDetailsViewModel by viewModels()
    private val args: AlbumDetailsFragmentArgs by navArgs()

    private var _binding: AlbumDetailsFragmentBinding? = null
    private val binding: AlbumDetailsFragmentBinding
    get() = _binding!!

    private var adapter: SimpleTrackAdapter? = null

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

        findNavController().let {
            (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
            binding.toolbar.setupWithNavController(
                it,
                AppBarConfiguration.Builder().setFallbackOnNavigateUpListener {
                    it.navigateUp()
                    true
                }.build()
            )
            binding.toolbar.title = ""
        }
        adapter = SimpleTrackAdapter(requireContext(), this@AlbumDetailsFragment)
        lifecycleScope.launch {
            val albumId = args.albumId
            val (album, tracks) = viewModel.getAlbumWithTracks(albumId)
            binding.apply {
                listTracksInAlbum.adapter = adapter
                val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                listTracksInAlbum.layoutManager = layoutManager
                listTracksInAlbum.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
                textAlbumName.text = album.name
                textNoOfTracks.text = String.format(
                    getString(R.string.no_of_tracks),
                    tracks.size
                )
                textReleaseYear.text = String.format(
                    getString(R.string.release_year),
                    getReleaseYearString(album.releaseYear)
                )

                if (album.isAlbumArtAvailable) {
                    val imageLoadRequest = requireContext().getImageRequest(album.albumArtRef, imageAlbumArt)
                    lifecycleScope.launch { requireContext().imageLoader.execute(imageLoadRequest) }
                } else imageAlbumArt.load(R.drawable.ic_music)
            }
            adapter?.submitData(tracks)
        }
    }

    override fun onItemClick(view: View?, data: Track, position: Int) {
        when (view?.id) {
            R.id.btnOptions -> {
                TrackDetailsDialog.getInstance(data).show(childFragmentManager, "TRACK")
            }
            else -> {
                val keyBundle = Bundle()
                keyBundle.putInt(MEDIA_POSITION, position)
                keyBundle.putLong(MEDIA_KEY, args.albumId)
                controls.playFromMediaId(KIND_ALBUM, keyBundle)
                findNavController().navigate(
                    AlbumDetailsFragmentDirections.actionAlbumDetailsFragmentToPlayerFragment()
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
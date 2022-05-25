package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_artist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.peeranm.melodeez.databinding.ArtistsFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Artist
import com.peeranm.melodeez.feature_music_playback.presentation.ViewPagerHostFragmentDirections
import com.peeranm.melodeez.feature_music_playback.utils.adapters.ArtistAdapter
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import com.peeranm.melodeez.feature_music_playback.utils.collectLatestWithLifecycle
import com.peeranm.melodeez.feature_music_playback.utils.collectWithLifecycle
import com.peeranm.melodeez.feature_music_playback.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistsFragment : Fragment(), OnItemClickListener<Artist> {

    private var _binding: ArtistsFragmentBinding? = null
    private val binding: ArtistsFragmentBinding
    get() = _binding!!

    private var adapter: ArtistAdapter? = null

    private val viewModel: ArtistsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ArtistsFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ArtistAdapter(this)
        binding.listArtists.adapter = adapter

        collectLatestWithLifecycle(viewModel.artists) { artists ->
           if (artists.isNotEmpty()) {
               binding.progressbar.visibility = View.GONE
               adapter?.submitData(artists)
           }
        }
        // Kick off
        binding.progressbar.visibility = View.VISIBLE
        viewModel.setStateEvent(Event.Synchronize)
    }

    override fun onItemClick(view: View?, data: Artist, position: Int) {
        findNavController().navigate(
            ViewPagerHostFragmentDirections.actionMainFragmentToArtistDetailsFragment(data.artistId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        adapter = null
        _binding = null
    }
}
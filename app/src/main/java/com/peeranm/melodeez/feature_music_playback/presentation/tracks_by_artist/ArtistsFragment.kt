package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_artist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.peeranm.melodeez.core.collectLatestWithLifecycle
import com.peeranm.melodeez.databinding.ArtistsFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Artist
import com.peeranm.melodeez.feature_music_playback.presentation.ViewPagerHostFragmentDirections
import com.peeranm.melodeez.feature_music_playback.utils.adapters.ArtistAdapter
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
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
        binding.bindList()

        collectLatestWithLifecycle(viewModel.artists) { artists ->
            binding.toggleProgressbarVisibility()
           if (artists.isNotEmpty()) adapter?.submitData(artists)
        }

        binding.toggleProgressbarVisibility(showNow = true)
        viewModel.synchronizeArtists()
    }

    override fun onItemClick(view: View?, data: Artist, position: Int) {
        findNavController().navigate(
            ViewPagerHostFragmentDirections.actionMainFragmentToArtistDetailsFragment(data.artistId)
        )
    }

    private fun ArtistsFragmentBinding.bindList() {
        adapter = ArtistAdapter(this@ArtistsFragment)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        listArtists.adapter = adapter
        listArtists.layoutManager = layoutManager
        listArtists.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
    }

    private fun ArtistsFragmentBinding.toggleProgressbarVisibility(showNow: Boolean = false) {
        progressbar.visibility = if (showNow) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        adapter = null
        _binding = null
    }
}
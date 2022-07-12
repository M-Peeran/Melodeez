package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_album

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.peeranm.melodeez.core.collectLatestWithLifecycle
import com.peeranm.melodeez.databinding.AlbumsFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Album
import com.peeranm.melodeez.feature_music_playback.presentation.ViewPagerHostFragmentDirections
import com.peeranm.melodeez.feature_music_playback.utils.adapters.AlbumAdapter
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumsFragment : Fragment(), OnItemClickListener<Album> {

    private val viewModel: AlbumsViewModel by viewModels()

    private var _binding: AlbumsFragmentBinding? = null
    private val binding: AlbumsFragmentBinding
    get() = _binding!!

    private var adapter: AlbumAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AlbumsFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AlbumAdapter(requireContext(), lifecycleScope, this)
        binding.listAlbums.adapter = adapter
        collectLatestWithLifecycle(viewModel.albums) { albums ->
            if (albums.isNotEmpty()) {
                binding.progressbar.visibility = View.GONE
                adapter?.submitData(albums)
            }
        }
        // Kick off
        binding.progressbar.visibility = View.VISIBLE
        viewModel.setStateEvent(Event.Synchronize)
    }

    override fun onItemClick(view: View?, data: Album, position: Int) {
        findNavController().navigate(
            ViewPagerHostFragmentDirections.actionMainFragmentToAlbumDetailsFragment(data.albumId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        adapter = null
        _binding = null
    }
}
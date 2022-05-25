package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.utils.ITEM_TYPE_CREATE_NEW_PLAYLIST
import com.peeranm.melodeez.databinding.PlaylistsFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.presentation.ViewPagerHostFragmentDirections
import com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.create_new.CreatePlaylistDialog
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import com.peeranm.melodeez.feature_music_playback.utils.adapters.PlaylistAdapter
import com.peeranm.melodeez.feature_music_playback.utils.collectWithLifecycle
import com.peeranm.melodeez.feature_music_playback.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistsFragment : Fragment(), OnItemClickListener<Any> {

    private val viewModel: PlaylistsViewModel by viewModels()

    private var _binding: PlaylistsFragmentBinding? = null
    private val binding: PlaylistsFragmentBinding
    get() = _binding!!

    private var adapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlaylistsFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter(requireContext(), this)
        binding.listPlaylists.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.listPlaylists.layoutManager = layoutManager
        binding.listPlaylists.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))

        collectWithLifecycle(viewModel.playlists) {
            val mutableList = mutableListOf<Any>()
            mutableList.add(ITEM_TYPE_CREATE_NEW_PLAYLIST)
            mutableList.addAll(it)
            adapter?.submitData(mutableList)
        }
        viewModel.onEvent(Event.GetPlaylists)
    }

    override fun onItemClick(view: View?, data: Any, position: Int) {
        if (position == 0) {
            CreatePlaylistDialog.getInstance().show(
                childFragmentManager, "MAIN"
            )
        } else {
            data as Playlist
            when (view?.id) {
                R.id.btnOptions -> showPopupMenu(view, data)
                else -> {
                    findNavController().navigate(
                        ViewPagerHostFragmentDirections.actionMainFragmentToPlaylistDetailsFragment(data.playlistId)
                    )
                }
            }
        }
    }

    private fun showPopupMenu(anchorView: View?, data: Playlist) {
        PopupMenu(requireContext(), anchorView).apply {
            inflate(R.menu.menu_playlist_options)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.actionAddMore -> {
                        findNavController().navigate(
                            ViewPagerHostFragmentDirections.actionMainFragmentToAddTracksFragment(data.playlistId)
                        )
                    }
                    R.id.actionDeletePlaylist -> {
                        viewModel.onEvent(Event.DeletePlaylist(data.playlistId))
                        requireContext().showToast("Deleted ${data.name} successfully!")
                    }
                }
                true
            }
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        adapter = null
        _binding = null
    }
}
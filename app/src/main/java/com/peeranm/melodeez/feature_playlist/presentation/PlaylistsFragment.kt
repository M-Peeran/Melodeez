package com.peeranm.melodeez.feature_playlist.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.R
import com.peeranm.melodeez.common.presentation.MainFragmentDirections
import com.peeranm.melodeez.common.utils.BaseAdapter
import com.peeranm.melodeez.common.utils.BaseHolder
import com.peeranm.melodeez.common.utils.ITEM_TYPE_CREATE_NEW_PLAYLIST
import com.peeranm.melodeez.common.utils.ITEM_TYPE_PLAYLIST
import com.peeranm.melodeez.databinding.CreateNewPlaylistItemBinding
import com.peeranm.melodeez.databinding.PlaylistItemBinding
import com.peeranm.melodeez.databinding.PlaylistsFragmentBinding
import com.peeranm.melodeez.feature_playlist.model.Playlist
import com.peeranm.melodeez.feature_playlist.presentation.create_new.CreatePlaylistDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModels()

    private var _binding: PlaylistsFragmentBinding? = null
    private val binding: PlaylistsFragmentBinding
    get() = _binding!!

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

        val diffCallback = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return when(newItem) {
                    is Playlist -> {
                        oldItem as Playlist
                        newItem.name == oldItem.name
                    }
                    else -> false
                }
            }
        }

        val adapter = object : BaseAdapter<Any>(diffCallback) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when (viewType) {
                    ITEM_TYPE_PLAYLIST -> {
                        object : BaseHolder<PlaylistItemBinding>(
                            PlaylistItemBinding.inflate(
                                layoutInflater,
                                parent,
                                false
                            )
                        ) {
                            override fun onInitializeViewHolder(rootView: View) {
                                rootView.setOnClickListener {
                                    val playlist = getItem(adapterPosition) as Playlist
                                    findNavController().navigate(
                                        MainFragmentDirections.actionMainFragmentToPlaylistDetailsFragment(
                                            playlist.name
                                        )
                                    )
                                }
                                binding.imageviewPlaylistOptions.setOnClickListener {
                                    val playlist = getItem(adapterPosition) as Playlist
                                    PopupMenu(requireContext(), it).apply {
                                        inflate(R.menu.menu_playlist_options)
                                        setOnMenuItemClickListener { menuItem ->
                                            when (menuItem.itemId) {
                                                R.id.action_add_more -> {
                                                    findNavController().navigate(
                                                        MainFragmentDirections.actionMainFragmentToAddTracksFragment(
                                                                playlist.name
                                                        )
                                                    )
                                                }
                                                R.id.action_delete_playlist -> {
                                                    lifecycleScope.launch {
                                                        if (viewModel.deletePlaylist(playlist.name) != 0) {
                                                            Toast.makeText(
                                                                requireContext(),
                                                                "Deleted ${playlist.name} successfully!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }
                                            }
                                            true
                                        }
                                    }.show()
                                }
                            }
                        }
                    }
                    else -> {
                        object: BaseHolder<CreateNewPlaylistItemBinding>(
                            CreateNewPlaylistItemBinding.inflate(
                                layoutInflater,
                                parent,
                                false
                            )
                        ) {
                            override fun onInitializeViewHolder(rootView: View) {
                                rootView.setOnClickListener {
                                    CreatePlaylistDialog.getInstance().show(
                                        childFragmentManager, "MAIN"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                when (holder.itemViewType) {
                    ITEM_TYPE_PLAYLIST -> {
                        holder as BaseHolder<PlaylistItemBinding>
                        val playlist = getItem(position) as Playlist
                        holder.binding.apply {
                            textviewName.text = playlist.name
                            textviewPlaylistNoOfTracks.text = "${playlist.noOfTracks} Tracks"
                        }
                    }
                }
            }

            override fun getItemViewType(position: Int): Int {
                return if (position == 0) ITEM_TYPE_CREATE_NEW_PLAYLIST
                else ITEM_TYPE_PLAYLIST
            }
        }

        binding.recyclerviewPlaylists.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner) {
            val mutableList = mutableListOf<Any>()
            mutableList.add(ITEM_TYPE_CREATE_NEW_PLAYLIST)
            mutableList.addAll(it)
            adapter.submitList(mutableList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
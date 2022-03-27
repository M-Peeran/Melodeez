package com.peeranm.melodeez.feature_tracks_by_album.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.peeranm.melodeez.R
import com.peeranm.melodeez.common.presentation.MainFragmentDirections
import com.peeranm.melodeez.common.utils.*
import com.peeranm.melodeez.databinding.AlbumsFragmentBinding
import com.peeranm.melodeez.databinding.AlbumsListItemBinding
import com.peeranm.melodeez.feature_tracks_by_album.model.Album
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class AlbumsFragment : Fragment() {

    private val viewModel: AlbumsViewModel by viewModels()

    private var _binding: AlbumsFragmentBinding? = null
    private val binding: AlbumsFragmentBinding
    get() = _binding!!

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

    override fun onStart() {
        super.onStart()
        viewModel.setStateEvent(StateEvent.Synchronize)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diffCallback = object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem.name == newItem.name
            }
        }

        val adapter = object : BaseAdapter<Album>(diffCallback) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : BaseHolder<AlbumsListItemBinding>(
                    AlbumsListItemBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                ) {
                    override fun onInitializeViewHolder(rootView: View) {
                        rootView.setOnClickListener {
                            val album = getItem(adapterPosition)
                            findNavController().navigate(
                                MainFragmentDirections.actionMainFragmentToAlbumDetailsFragment(album.name)
                            )
                        }
                    }
                }
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder as BaseHolder<AlbumsListItemBinding>
                holder.binding.apply {
                    val album = getItem(position)
                    textviewAlbumTitle.text = album.name
                    if (album.isAlbumArtAvailable) {
                        val imageLoadRequest = ImageRequest.Builder(requireContext()).apply {
                            data(
                                requireContext().getBitmap(
                                    File(requireContext().filesDir, "albumarts"),
                                    album.albumArtRef
                                )
                            )
                            target(imageviewItemArt)
                        }.build()
                        lifecycleScope.launch { requireContext().imageLoader.execute(imageLoadRequest) }
                    } else imageviewItemArt.load(R.drawable.ic_music)
                }
            }
        }

        binding.recyclerviewAlbums.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    // Show progress bar
                    binding.progressbarAlbums.visibility = View.VISIBLE
                }

                is DataState.Success -> {
                    binding.progressbarAlbums.visibility = View.GONE
                    adapter.submitList(dataState.data)
                }
                is DataState.SynchronizationCompleted -> {
                    viewModel.setStateEvent(StateEvent.Get)
                }

                is DataState.Synchronizing -> {
                    binding.progressbarAlbums.visibility = View.VISIBLE
                }

                is DataState.Failure -> {
                    binding.progressbarAlbums.visibility = View.GONE

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
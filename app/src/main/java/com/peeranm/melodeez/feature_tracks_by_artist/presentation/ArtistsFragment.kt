package com.peeranm.melodeez.feature_tracks_by_artist.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.common.presentation.MainFragmentDirections
import com.peeranm.melodeez.common.utils.BaseAdapter
import com.peeranm.melodeez.common.utils.BaseHolder
import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.common.utils.StateEvent
import com.peeranm.melodeez.databinding.ArtistListItemBinding
import com.peeranm.melodeez.databinding.ArtistsFragmentBinding
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistsFragment : Fragment() {

    private var _binding: ArtistsFragmentBinding? = null
    private val binding: ArtistsFragmentBinding
    get() = _binding!!

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

    override fun onStart() {
        super.onStart()
        viewModel.setStateEvent(StateEvent.Synchronize)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val diffCallback = object : DiffUtil.ItemCallback<Artist>() {
            override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem.name == newItem.name
            }
        }

        val adapter = object : BaseAdapter<Artist>(diffCallback) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : BaseHolder<ArtistListItemBinding>(
                    ArtistListItemBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                ){
                    override fun onInitializeViewHolder(rootView: View) {
                        rootView.setOnClickListener {
                            val artist = getItem(adapterPosition)
                            findNavController().navigate(
                                MainFragmentDirections.actionMainFragmentToArtistDetailsFragment(artist.name)
                            )
                        }
                    }
                }
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder as BaseHolder<ArtistListItemBinding>
                val artist = getItem(position)
                holder.binding.textviewAlbumTitle.text = artist.name
            }
        }

        binding.recyclerviewArtists.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                   binding.progressbarArtists.visibility = View.VISIBLE
                }

                is DataState.Success -> {
                    binding.progressbarArtists.visibility = View.GONE
                    adapter.submitList(dataState.data)
                }
                is DataState.SynchronizationCompleted -> {
                    viewModel.setStateEvent(StateEvent.Get)
                }

                is DataState.Synchronizing -> {
                    binding.progressbarArtists.visibility = View.VISIBLE
                }

                is DataState.Failure -> {
                    binding.progressbarArtists.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
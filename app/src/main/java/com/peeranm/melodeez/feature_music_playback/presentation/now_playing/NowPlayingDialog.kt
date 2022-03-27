package com.peeranm.melodeez.feature_music_playback.presentation.now_playing

import android.media.session.MediaController
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.peeranm.melodeez.common.utils.BaseAdapter
import com.peeranm.melodeez.common.utils.BaseHolder
import com.peeranm.melodeez.common.utils.KIND_NOW_PLAYING
import com.peeranm.melodeez.common.utils.MEDIA_POSITION
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.databinding.NowPlayingDialogBinding
import com.peeranm.melodeez.databinding.NowPlayingListItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NowPlayingDialog : BottomSheetDialogFragment() {

    private val viewModel: NowPlayingViewModel by viewModels()

    private var _binding: NowPlayingDialogBinding? = null
    private val binding: NowPlayingDialogBinding
    get() = _binding!!

    private val controls: MediaController.TransportControls
    get() = requireActivity().mediaController.transportControls

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NowPlayingDialogBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diffCallback = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.uri == newItem.uri &&
                        oldItem.title == newItem.title &&
                        oldItem.album == newItem.album &&
                        oldItem.artist == newItem.artist &&
                        oldItem.duration == newItem.duration
            }
        }

        val adapter = object : BaseAdapter<Track>(diffCallback) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : BaseHolder<NowPlayingListItemBinding>(
                    NowPlayingListItemBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                ) {
                    override fun onInitializeViewHolder(rootView: View) {
                        rootView.setOnClickListener {
                            val keyBundle = Bundle()
                            keyBundle.putInt(MEDIA_POSITION, adapterPosition)
                            controls.playFromMediaId(KIND_NOW_PLAYING, keyBundle)
                        }
                        binding.imageviewClear.setOnClickListener {
                            viewModel.removeFromQueueAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            if (viewModel.getSource().isEmpty()) {
                                dismiss()
                                findNavController().navigateUp()
                            }
                        }
                    }
                }
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder as BaseHolder<NowPlayingListItemBinding>
                val track = getItem(position)
                holder.binding.apply {
                    textviewTitle.text = track.title
                    textviewArtistAndAlbum.text = "${track.artist} - ${track.album}"
                }
            }
        }
        binding.recyclerviewNowPlaying.adapter = adapter

        if (viewModel.getSource().isEmpty()) {
            Toast.makeText(requireContext(), "No tracks in queue", Toast.LENGTH_SHORT).show()
        } else adapter.submitList(viewModel.getSource())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
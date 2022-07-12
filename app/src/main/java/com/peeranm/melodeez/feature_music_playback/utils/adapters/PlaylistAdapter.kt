package com.peeranm.melodeez.feature_music_playback.utils.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.ITEM_TYPE_CREATE_NEW_PLAYLIST
import com.peeranm.melodeez.core.ITEM_TYPE_PLAYLIST
import com.peeranm.melodeez.databinding.CreateNewPlaylistItemBinding
import com.peeranm.melodeez.databinding.PlaylistItemBinding
import com.peeranm.melodeez.feature_music_playback.model.Playlist

class PlaylistAdapter(
    context: Context,
    listener: OnItemClickListener<Any>
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>() {

    private var context: Context? = null
    private var listener: OnItemClickListener<Any>? = null

    private val data = AsyncListDiffer(this,
        object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return oldItem.toString() === newItem.toString()
            }
            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return oldItem == newItem
            }
        }
    )

    init {
        this.context = context
        this.listener = listener
    }

    inner class PlaylistHolder(private val binding: ViewBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
            if (binding is PlaylistItemBinding) {
                binding.btnOptions.setOnClickListener(this)
            }
        }

        fun bind(position: Int) {
            val item = data.currentList[position]
            if (binding is PlaylistItemBinding && item is Playlist) {
                binding.apply {
                    textName.text = item.name
                    textNoOfTracks.text = String.format(
                        context?.getString(R.string.no_of_tracks)!!, item.noOfTracks
                    )
                }
            }
        }

        override fun onClick(view: View?) {
            listener?.onItemClick(
                view = view,
                data = data.currentList[adapterPosition],
                position = adapterPosition
            )
        }
    }

    fun submitData(data: List<Any>) {
        this.data.submitList(data)
    }

    fun onClear() {
        context = null
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        return PlaylistHolder(
            if (viewType == ITEM_TYPE_PLAYLIST) {
                PlaylistItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            } else CreateNewPlaylistItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = data.currentList.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ITEM_TYPE_CREATE_NEW_PLAYLIST
        else ITEM_TYPE_PLAYLIST
    }
}
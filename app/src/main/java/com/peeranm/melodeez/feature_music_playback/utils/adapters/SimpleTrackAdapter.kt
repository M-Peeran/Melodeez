package com.peeranm.melodeez.feature_music_playback.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.R
import com.peeranm.melodeez.databinding.SimpleTrackItemBinding
import com.peeranm.melodeez.feature_music_playback.model.Track

class SimpleTrackAdapter(
    context: Context,
    listener: OnItemClickListener<Track>
) : RecyclerView.Adapter<SimpleTrackAdapter.SimpleTrackHolder>() {

    private var context: Context? = null
    private var listener: OnItemClickListener<Track>? = null

    private val differ = AsyncListDiffer(this,
        object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.trackId == newItem.trackId
            }
            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem == newItem
            }
        }
    )

    init {
        this.context = context
        this.listener = listener
    }

    inner class SimpleTrackHolder(private val binding: SimpleTrackItemBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.apply {
                root.setOnClickListener(this@SimpleTrackHolder)
                btnOptions.setOnClickListener(this@SimpleTrackHolder)
            }

        }

        fun bind(position: Int) {
            val track = differ.currentList[position]
            binding.apply {
                textTitle.text = track.title
                textArtistAndAlbum.text = String.format(
                    context?.getString(R.string.track_artist_and_album)!!,
                    track.artist,
                    track.album
                )
            }
        }

        override fun onClick(view: View?) {
            listener?.onItemClick(
                view = view,
                data = differ.currentList[adapterPosition],
                position = adapterPosition
            )
        }
    }

    fun submitData(data: List<Track>) {
        differ.submitList(data)
    }

    fun onClear() {
        context = null
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTrackHolder {
        return SimpleTrackHolder(
            SimpleTrackItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SimpleTrackHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = differ.currentList.size
}
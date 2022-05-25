package com.peeranm.melodeez.feature_music_playback.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import com.peeranm.melodeez.R
import com.peeranm.melodeez.databinding.TracksListItemBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.utils.getImageRequest
import kotlinx.coroutines.launch

class TrackAdapter(
    context: Context,
    scope: LifecycleCoroutineScope,
    listener: OnItemClickListener<Track>
) : RecyclerView.Adapter<TrackAdapter.TrackHolder>() {

    private var context: Context? = null
    private var listener: OnItemClickListener<Track>? = null
    private var scope: LifecycleCoroutineScope? = null

    private val differ = AsyncListDiffer(this,
        object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.uri == newItem.uri
            }
            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem == newItem
            }
        }
    )

    init {
        this.context = context
        this.listener = listener
        this.scope = scope
    }

    inner class TrackHolder(private val binding: TracksListItemBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.apply {
                root.setOnClickListener(this@TrackHolder)
                btnOptions.setOnClickListener(this@TrackHolder)
            }

        }

        fun bind(position: Int) {
            val track = differ.currentList[position]
            val context = context!!
            binding.apply {
                textTitle.text = track.title
                textArtistAndAlbum.text = String.format(
                    context.getString(R.string.track_artist_and_album),
                    track.artist,
                    track.album
                )

                if (track.isAlbumArtAvailable) {
                    val imageLoadRequest = context.getImageRequest(track.albumArtRef, imageAlbumArt)
                    scope?.launch { context.imageLoader.execute(imageLoadRequest) }
                } else imageAlbumArt.load(R.drawable.ic_music)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        return TrackHolder(
            TracksListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = differ.currentList.size
}
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
import com.peeranm.melodeez.databinding.AlbumsListItemBinding
import com.peeranm.melodeez.feature_music_playback.model.Album
import com.peeranm.melodeez.feature_music_playback.utils.getImageRequest
import kotlinx.coroutines.launch

class AlbumAdapter(
    context: Context,
    scope: LifecycleCoroutineScope,
    listener: OnItemClickListener<Album>
) : RecyclerView.Adapter<AlbumAdapter.AlbumHolder>() {

    private var context: Context? = null
    private var scope: LifecycleCoroutineScope? = null
    private var listener: OnItemClickListener<Album>? = null
    private val differ = AsyncListDiffer(this,
        object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem.name == newItem.name
            }
            override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem == newItem
            }
        }
    )

    init {
        this.context = context
        this.scope = scope
        this.listener = listener
    }

    inner class AlbumHolder(private val binding: AlbumsListItemBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this@AlbumHolder)
        }

        fun bind(position: Int) {
            val context = context!!
            val album = differ.currentList[position]
            binding.apply {
                textAlbumTitle.text = album.name
                if (album.isAlbumArtAvailable) {
                    val imageLoadRequest = context.getImageRequest(album.albumArtRef, imageAlbumArt)
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

    fun submitData(data: List<Album>) {
        differ.submitList(data)
    }

    fun onClear() {
        context = null
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        return AlbumHolder(
            AlbumsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = differ.currentList.size
}
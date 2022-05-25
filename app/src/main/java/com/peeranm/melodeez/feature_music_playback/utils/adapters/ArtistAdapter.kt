package com.peeranm.melodeez.feature_music_playback.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.databinding.ArtistListItemBinding
import com.peeranm.melodeez.feature_music_playback.model.Artist

class ArtistAdapter(
    listener: OnItemClickListener<Artist>
) : RecyclerView.Adapter<ArtistAdapter.ArtistHolder>() {

    private var listener: OnItemClickListener<Artist>? = null

    private val differ = AsyncListDiffer(this,
        object : DiffUtil.ItemCallback<Artist>() {
            override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem.name == newItem.name
            }
            override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem == newItem
            }
        }
    )

    init {
        this.listener = listener
    }

    inner class ArtistHolder(private val binding: ArtistListItemBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this@ArtistHolder)
        }

        fun bind(position: Int) {
            val artist = differ.currentList[position]
            binding.textAlbumTitle.text = artist.name
        }

        override fun onClick(view: View?) {
            listener?.onItemClick(
                view = view,
                data = differ.currentList[adapterPosition],
                position = adapterPosition
            )
        }
    }

    fun submitData(data: List<Artist>) {
        differ.submitList(data)
    }

    fun onClear() {
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistHolder {
        return ArtistHolder(
            ArtistListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArtistHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = differ.currentList.size
}
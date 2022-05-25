package com.peeranm.melodeez.feature_music_playback.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.databinding.SelectPlaylistItemBinding
import com.peeranm.melodeez.feature_music_playback.model.Playlist

class SelectPlaylistAdapter(
    listener: OnCheckChangeListener<Playlist>
) : RecyclerView.Adapter<SelectPlaylistAdapter.PlaylistHolder>() {

    private var listener: OnCheckChangeListener<Playlist>? = null

    private val playlists = mutableListOf<Playlist>()

    init { this.listener = listener }

    inner class PlaylistHolder(private val binding: SelectPlaylistItemBinding)
        : RecyclerView.ViewHolder(binding.root), CompoundButton.OnCheckedChangeListener {

        init {
            binding.radiobuttonSelect.setOnCheckedChangeListener(this)
        }

        fun bind(position: Int) {
            val playlist = playlists[position]
            binding.textviewPlaylistName.text = playlist.name
        }

        override fun onCheckedChanged(compButton: CompoundButton?, isSelected: Boolean) {
            listener?.onCheckChange(
                compButton = compButton,
                data = playlists[adapterPosition],
                isSelected = isSelected,
                position = adapterPosition
            )
        }
    }

    fun submitData(data: List<Playlist>) {
        playlists.addAll(data)
        notifyItemRangeInserted(0, data.size)
    }

    fun onClear() {
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        return PlaylistHolder(
            SelectPlaylistItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = playlists.size
}
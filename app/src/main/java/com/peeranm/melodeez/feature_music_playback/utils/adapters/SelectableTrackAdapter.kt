package com.peeranm.melodeez.feature_music_playback.utils.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peeranm.melodeez.R
import com.peeranm.melodeez.databinding.TracksListSelectableItemBinding
import com.peeranm.melodeez.feature_music_playback.model.Track

class SelectableTrackAdapter(
    context: Context,
    listener: OnCheckChangeListener<Track>
) : RecyclerView.Adapter<SelectableTrackAdapter.TrackHolder>() {

    private var context: Context? = null
    private var listener: OnCheckChangeListener<Track>? = null
    private val selections = mutableMapOf<String, Boolean>()

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
    }

    inner class TrackHolder(private val binding: TracksListSelectableItemBinding)
        : RecyclerView.ViewHolder(binding.root), CompoundButton.OnCheckedChangeListener {

        init {
            binding.checkboxSelection.setOnCheckedChangeListener(this@TrackHolder)
        }

        fun bind(position: Int) {
            val track = differ.currentList[position]
            binding.apply {
                checkboxSelection.isChecked = selections[track.uri] ?: false
                textTitle.text = track.title

                textviewAlbumArtist.text = String.format(
                    context?.getString(R.string.track_artist_and_album)!!,
                    track.artist,
                    track.album
                )
            }
        }

        override fun onCheckedChanged(compButton: CompoundButton?, isSelected: Boolean) {
            val track = differ.currentList[adapterPosition]
            selections[track.uri] = isSelected
            listener?.onCheckChange(
                compButton = compButton,
                data = track,
                isSelected = isSelected,
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
            TracksListSelectableItemBinding.inflate(
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
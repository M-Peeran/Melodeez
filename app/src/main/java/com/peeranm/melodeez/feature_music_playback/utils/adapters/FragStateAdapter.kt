package com.peeranm.melodeez.feature_music_playback.utils.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.peeranm.melodeez.core.utils.NO_OF_TABS
import com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_album.AlbumsFragment
import com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_artist.ArtistsFragment
import com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.PlaylistsFragment
import com.peeranm.melodeez.feature_music_playback.presentation.all_tracks.AllTracksFragment

class FragStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return NO_OF_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllTracksFragment()
            1 -> AlbumsFragment()
            2 -> ArtistsFragment()
            else -> PlaylistsFragment()
        }
    }
}
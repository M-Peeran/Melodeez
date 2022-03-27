package com.peeranm.melodeez.common.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.peeranm.melodeez.feature_tracks_by_album.presentation.AlbumsFragment
import com.peeranm.melodeez.feature_tracks_by_artist.presentation.ArtistsFragment
import com.peeranm.melodeez.feature_playlist.presentation.PlaylistsFragment
import com.peeranm.melodeez.feature_tracks.presentation.AllTracksFragment

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
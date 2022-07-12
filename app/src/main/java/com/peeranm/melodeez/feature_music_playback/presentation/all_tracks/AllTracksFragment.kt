package com.peeranm.melodeez.feature_music_playback.presentation.all_tracks

import android.Manifest
import android.content.pm.PackageManager
import android.media.session.MediaController
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.KIND_TRACKS_COLLECTION
import com.peeranm.melodeez.core.MEDIA_POSITION
import com.peeranm.melodeez.core.collectLatestWithLifecycle
import com.peeranm.melodeez.core.showToast
import com.peeranm.melodeez.databinding.AllTracksFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.presentation.Event
import com.peeranm.melodeez.feature_music_playback.presentation.ViewPagerHostFragmentDirections
import com.peeranm.melodeez.feature_music_playback.presentation.all_tracks.details.TrackDetailsDialog
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import com.peeranm.melodeez.feature_music_playback.utils.adapters.TrackAdapter

@AndroidEntryPoint
class AllTracksFragment : Fragment(), OnItemClickListener<Track> {

    private var _binding: AllTracksFragmentBinding? = null
    private val binding: AllTracksFragmentBinding
    get() = _binding!!

    private val viewModel: AllTracksViewModel by viewModels()

    private var adapter: TrackAdapter? = null
    private var isPermissionGranted = false

    private val controls: MediaController.TransportControls
    get() = requireActivity().mediaController.transportControls

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AllTracksFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.checkPermissions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.requestPermissionsIfNotGrantedAlready()
        binding.bindList()

        collectLatestWithLifecycle(viewModel.tracks) { tracks ->
            binding.toggleProgressBarVisibility()
            if (tracks.isNotEmpty()) adapter?.submitData(tracks)
        }
    }

    override fun onItemClick(view: View?, data: Track, position: Int) {
        when (view?.id) {
            R.id.btnOptions -> TrackDetailsDialog.getInstance(data).show(childFragmentManager, "TRACK")
            else -> {
                val keyBundle = Bundle()
                keyBundle.putInt(MEDIA_POSITION, position)
                controls.playFromMediaId(KIND_TRACKS_COLLECTION, keyBundle)
                findNavController().navigate(
                    ViewPagerHostFragmentDirections.actionMainFragmentToPlayerFragment()
                )
            }
        }
    }

    private fun AllTracksFragmentBinding.bindList() {
        adapter = TrackAdapter(requireContext(), lifecycleScope, this@AllTracksFragment)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        listTracks.adapter = adapter
        listTracks.layoutManager = layoutManager
        listTracks.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
    }

    private fun AllTracksFragmentBinding.checkPermissions() {
        toggleProgressBarVisibility(showNow = true)
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true
            toggleProgressBarVisibility()
            viewModel.onEvent(Event.Synchronize)
        }
    }

    private fun AllTracksFragmentBinding.requestPermissionsIfNotGrantedAlready() {
        if (isPermissionGranted) return
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            val permissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    toggleProgressBarVisibility(showNow = true)
                    viewModel.onEvent(Event.Synchronize)
                } else {
                    toggleProgressBarVisibility()
                    showToast("Permission Denied")
                    requireActivity().finish()
                }
            }
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun AllTracksFragmentBinding.toggleProgressBarVisibility(showNow: Boolean = false) {
        progressbar.visibility = if (showNow) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        _binding = null
        adapter = null
    }
}
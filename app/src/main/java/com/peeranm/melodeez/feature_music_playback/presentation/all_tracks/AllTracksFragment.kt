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
import com.peeranm.melodeez.core.utils.*
import com.peeranm.melodeez.databinding.AllTracksFragmentBinding
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.presentation.Event
import com.peeranm.melodeez.feature_music_playback.presentation.ViewPagerHostFragmentDirections
import com.peeranm.melodeez.feature_music_playback.presentation.all_tracks.details.TrackDetailsDialog
import com.peeranm.melodeez.feature_music_playback.utils.adapters.OnItemClickListener
import com.peeranm.melodeez.feature_music_playback.utils.adapters.TrackAdapter
import com.peeranm.melodeez.feature_music_playback.utils.collectLatestWithLifecycle
import com.peeranm.melodeez.feature_music_playback.utils.showToast

@AndroidEntryPoint
class AllTracksFragment : Fragment(), OnItemClickListener<Track> {

    private var _binding: AllTracksFragmentBinding? = null
    private val binding: AllTracksFragmentBinding
    get() = _binding!!

    private val viewModel: AllTracksViewModel by viewModels()

    private var adapter: TrackAdapter? = null

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
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            binding.progressbar.visibility = View.VISIBLE
            viewModel.onEvent(Event.Synchronize)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            val permissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    binding.progressbar.visibility = View.VISIBLE
                    viewModel.onEvent(Event.Synchronize)
                } else {
                    binding.progressbar.visibility = View.GONE
                    showToast("Permission Denied")
                    requireActivity().finish()
                }
            }
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        adapter = TrackAdapter(requireContext(), lifecycleScope, this)
        binding.listTracks.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.listTracks.layoutManager = layoutManager
        binding.listTracks.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))

        collectLatestWithLifecycle(viewModel.tracks) { tracks ->
            if (tracks.isNotEmpty()) {
                binding.progressbar.visibility = View.GONE
                adapter?.submitData(tracks)
            }
        }
    }

    override fun onItemClick(view: View?, data: Track, position: Int) {
        when (view?.id) {
            R.id.btnOptions -> {
                TrackDetailsDialog.getInstance(data).show(childFragmentManager, "TRACK")
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.onClear()
        _binding = null
        adapter = null
    }
}
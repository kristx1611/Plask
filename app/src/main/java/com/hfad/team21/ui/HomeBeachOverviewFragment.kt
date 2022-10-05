package com.hfad.team21.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.hfad.team21.R
import com.hfad.team21.domain.Beach
import com.hfad.team21.viewmodels.HomeBeachOverviewViewModel
import com.hfad.team21.viewmodels.HomeBeachOverviewViewModelFactory
import com.hfad.team21.viewmodels.SharedViewModel

class HomeBeachOverviewFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var viewModel: HomeBeachOverviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.home_beach_overview_fragment, container, false)
        val application = requireActivity().application
        val viewModelFactory = HomeBeachOverviewViewModelFactory(application, requireActivity())
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeBeachOverviewViewModel::class.java)

        val allRecycler: RecyclerView = root.findViewById(R.id.home_all_rv)
        val allAdapter = BeachAdapter(
            { viewModel.setFavorite(it) },
            { viewModel.removeFavorite(it) },
            { moveCamera(it) },
            this,
            R.layout.beach_recycler_item_home
        )
        allRecycler.adapter = allAdapter
        setupBeachObserver(allAdapter)

        val favRecycler: RecyclerView = root.findViewById(R.id.home_fav_rv)
        val favAdapter = BeachAdapter(
            { viewModel.setFavorite(it) },
            { viewModel.removeFavorite(it) },
            { moveCamera(it) },
            this,
            R.layout.beach_recycler_item_home
        )
        favRecycler.adapter = favAdapter

        val emptyIndicator: TextView = root.findViewById(R.id.favorites_empty_tv)
        viewModel.favoriteBeaches.observe(
            viewLifecycleOwner
        ) {
            if (it.isEmpty()) {
                emptyIndicator.visibility = View.VISIBLE
                favAdapter.beaches = it
            } else {
                favAdapter.beaches = it
                emptyIndicator.visibility = View.INVISIBLE
            }
        }

        root.findViewById<Button>(R.id.see_all_beaches_btn).setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.navigation_beach)
        }

        sharedViewModel.refreshOverview.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.beaches.removeObservers(viewLifecycleOwner)
                viewModel.recreateBeaches(requireActivity())
                setupBeachObserver(allAdapter)
                sharedViewModel.finishRefreshingOverview()
            }
        }

        return root
    }

    private fun moveCamera(beach: Beach) {
        val bundle = bundleOf("beachId" to beach.id)
        this.findNavController().navigate(R.id.action_navigation_home_beach_overview_fragment_to_navigation_beach_info_fragment, bundle)
        sharedViewModel.startMovingCamera(LatLng(beach.latitude, beach.longitude))
    }

    private fun setupBeachObserver(adapter: BeachAdapter) {
        viewModel.beaches.observe(viewLifecycleOwner) {
            adapter.beaches = it.take(10)
        }
    }

    override fun onStart() {
        super.onStart()
        sharedViewModel.overviewOnScreen = true
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.overviewOnScreen = false
    }
}

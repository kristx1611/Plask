package com.hfad.team21.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hfad.team21.R
import com.hfad.team21.database.BeachEntity
import com.hfad.team21.viewmodels.HomeViewModel
import com.hfad.team21.viewmodels.HomeViewModelFactory
import com.hfad.team21.viewmodels.SharedViewModel

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var map: GoogleMap
    private var mapReady = false

    private var markers: List<BeachEntity>? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.uiSettings.isMapToolbarEnabled = false
        mapReady = true
        updateMap()

        googleMap.setOnMarkerClickListener {
            if (it.tag != -1)
                navigateToInfo(bundleOf("beachId" to it.tag))
            false
        }

        setLastLocation()

        requireView().findViewById<ImageButton>(R.id.zoom_in_btn).setOnClickListener {
            map.animateCamera(CameraUpdateFactory.zoomIn())
        }

        requireView().findViewById<ImageButton>(R.id.zoom_out_btn).setOnClickListener {
            map.animateCamera(CameraUpdateFactory.zoomOut())
        }

        sharedViewModel.moveCamera.observe(viewLifecycleOwner) {
            if (it != null) {
                animateCamera(it)
                sharedViewModel.finishMovingCamera()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.home_fragment, container, false)

        val application = requireActivity().application
        val viewModelFactory = HomeViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        viewModel.beachEntities.observe(viewLifecycleOwner) {
            markers = it
            updateMap()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(root.findViewById(R.id.home_bottom_sheet))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val button: Button = root.findViewById(R.id.home_btn)
        button.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        root.findViewById<ImageView>(R.id.search_iv).setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.navigation_beach)
        }

        val spinner: Spinner = root.findViewById(R.id.home_sp)
        val spinnerListener = SpinnerInteractionListener()
        val city = viewModel.getCity(requireActivity())
        if (city == "Oslo")
            spinner.setSelection(0)
        else
            spinner.setSelection(1)
        spinner.setOnTouchListener(spinnerListener)
        spinner.onItemSelectedListener = spinnerListener

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.home_fragment_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        view.findViewById<ImageButton>(R.id.home_location_btn).setOnClickListener {
            animateCamera(viewModel.getLastLocation(requireActivity()))
        }

        arguments?.let {
            navigateToInfo(it)
        }
    }

    private fun updateMap() {
        if (mapReady && markers != null) {
            markers!!.forEach {
                val marker = LatLng(it.latitude, it.longitude)
                map.addMarker(MarkerOptions().position(marker).title(it.name))?.tag = it.id
            }

            val userPositionMarker = viewModel.getLastLocation(requireActivity())
            map.addMarker(
                MarkerOptions().position(userPositionMarker).title("Din posisjon").icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            )?.tag = -1
        }
    }

    // This method should only be called after onCreateView()
    private fun navigateToInfo(bundle: Bundle) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        requireView().findViewById<View>(R.id.home_fragment_beach).findNavController().navigate(
            R.id.navigation_beach_info_fragment,
            bundle
        )
    }

    private fun setLastLocation() {
        if (sharedViewModel.cameraPos == null)
            moveCamera(viewModel.getLastLocation(requireActivity()))
        else
            moveCamera(sharedViewModel.cameraPos!!)
    }

    private fun moveCamera(pos: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12.0f))
    }

    private fun animateCamera(pos: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f))
    }

    inner class SpinnerInteractionListener : AdapterView.OnItemSelectedListener, View.OnTouchListener {
        private var userSelect = false

        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            if (userSelect) {
                userSelect = false
                viewModel.setCity(parent.getItemAtPosition(pos).toString(), requireActivity())
                animateCamera(viewModel.getCityLocation(requireActivity()))

                if (sharedViewModel.overviewOnScreen) {
                    sharedViewModel.startRefreshingOverview()
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Do nothing
        }

        // Unnecessary to call view.performClick() from onTouch()
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            userSelect = true
            return false
        }
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.cameraPos = map.cameraPosition.target
    }
}

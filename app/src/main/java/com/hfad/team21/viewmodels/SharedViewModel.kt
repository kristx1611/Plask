package com.hfad.team21.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

// This viewmodel becomes a singleton in practice.
// Some of the comments in this file use pseudo code
class SharedViewModel : ViewModel() {
    private val _moveCamera = MutableLiveData<LatLng?>()

    // Observed in HomeFragment
    val moveCamera: LiveData<LatLng?> = _moveCamera

    // Set to true/false from HomeBeachOverviewFragment onStart()/onStop()
    // Used in HomeFragment.SpinnerInteractionListener.onItemSelected()
    var overviewOnScreen = true

    // Set to a value in HomeFragment.onStop()
    // Used in HomeFragment.setLastLocation
    var cameraPos: LatLng? = null

    private val _refreshOverview = MutableLiveData(false)

    // Observed in HomeBeachOverviewFragment
    val refreshOverview: LiveData<Boolean> = _refreshOverview

    // Called from BeachFragment.moveCamera() and HomeBeachOverviewFragment.moveCamera()
    fun startMovingCamera(pos: LatLng) {
        _moveCamera.value = pos
    }

    // Called from HomeFragment.sharedViewModel.moveCamera.observe
    fun finishMovingCamera() {
        _moveCamera.value = null
    }

    // Called from HomeFragment.SpinnerInteractionListener.onItemSelected()
    fun startRefreshingOverview() {
        _refreshOverview.value = true
    }

    // Called from HomeBeachOverviewFragment.sharedViewModel.refreshOverview.observe
    fun finishRefreshingOverview() {
        _refreshOverview.value = false
    }
}

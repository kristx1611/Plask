package com.hfad.team21

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.hfad.team21.database.DistanceEntity
import com.hfad.team21.viewmodels.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

// New phones have their location functionality 'completely' turned off. I don't know how to make the app turn it on.

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = this.application
        val viewModelFactory = MainViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        initLocation()
        initDatabase()
    }

    private fun initDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                viewModel.refresh()
            } catch (error: Exception) {
                Log.e("RememberToRemove", error.toString())
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Greide ikke å få tak i nye temperaturer", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initLocation() {
        if (checkPermission()) {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                getCurrentLocation()
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                        exception.startResolutionForResult(
                            this@MainActivity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        } else
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun checkPermission(): Boolean =
        (
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            )

    private fun updateDistance(start: Location) {
        viewModel.setLocation(start, this)

        CoroutineScope(Dispatchers.Default).launch {
            val distanceEntities: List<DistanceEntity> = viewModel.getBeachEntities().map {
                val end = Location("")
                end.latitude = it.latitude
                end.longitude = it.longitude
                DistanceEntity(it.id, start.distanceTo(end) / 1000)
            }

            viewModel.updateDistance(distanceEntities)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return

        if (isPermissionGranted(permissions, grantResults))
            initLocation()
        else
            Toast.makeText(this, "Distanse til strender vises nå ikke", Toast.LENGTH_LONG).show()
    }

    private fun isPermissionGranted(
        grantPermissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        for (i in grantPermissions.indices) {
            if (Manifest.permission.ACCESS_FINE_LOCATION == grantPermissions[i])
                return grantResults[i] == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    // Always call this method from within a location permission check.
    // Only reason why the check is not done inside the method is to avoid doing the check twice.
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        LocationServices.getFusedLocationProviderClient(this)
            .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(this, "Det oppsto en feil ved å hente din lokasjon", Toast.LENGTH_LONG).show()
                else
                    updateDistance(location)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CHECK_SETTINGS)
            return
        if (resultCode == RESULT_OK)
            initLocation()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onResume() {
        super.onResume()
        // May also need to use SYSTEM_UI_FLAG_LAYOUT_STABLE to help the app maintain a stable layout.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    override fun onRestart() {
        super.onRestart()

        if (checkPermission())
            getCurrentLocation()
        initDatabase()
    }
}

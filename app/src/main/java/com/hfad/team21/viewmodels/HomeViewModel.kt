package com.hfad.team21.viewmodels

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.hfad.team21.database.AppDatabase
import com.hfad.team21.database.BeachEntity
import com.hfad.team21.repository.Repository

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(AppDatabase.getInstance(application).appDatabaseDao)

    val beachEntities: LiveData<List<BeachEntity>> = repository.getAllBeachEntitiesLiveData()

    fun setCity(city: String, activity: Activity) {
        repository.setCity(city, activity)
    }

    fun getCity(activity: Activity): String = repository.getCity(activity)

    fun getLastLocation(activity: Activity): LatLng = repository.getLastLocation(activity)

    fun getCityLocation(activity: Activity): LatLng = repository.getCityLocation(activity)
}

package com.hfad.team21.viewmodels

import android.app.Activity
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import com.hfad.team21.database.AppDatabase
import com.hfad.team21.database.BeachEntity
import com.hfad.team21.database.DistanceEntity
import com.hfad.team21.repository.Repository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(AppDatabase.getInstance(application).appDatabaseDao)

    suspend fun refresh() {
        repository.refresh()
    }

    suspend fun getBeachEntities(): List<BeachEntity> {
        return repository.getAllBeachEntities()
    }

    suspend fun updateDistance(distances: List<DistanceEntity>) {
        repository.updateDistance(distances)
    }

    fun setLocation(pos: Location, activity: Activity) {
        repository.setLocation(pos, activity)
    }
}

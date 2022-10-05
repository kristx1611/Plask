package com.hfad.team21.viewmodels

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hfad.team21.database.AppDatabase
import com.hfad.team21.database.FavoriteEntity
import com.hfad.team21.domain.Beach
import com.hfad.team21.repository.Repository
import kotlinx.coroutines.launch

class BeachViewModel(application: Application, activity: Activity) : AndroidViewModel(application) {

    private val repository = Repository(AppDatabase.getInstance(application).appDatabaseDao)

    val beaches: LiveData<List<Beach>> = repository.getCompleteBeachData(activity)
    val favoriteBeaches: LiveData<List<Beach>> = repository.getCompleteFavoriteBeachData()

    private val _isSorting = MutableLiveData(false)
    val isSorting: LiveData<Boolean> = _isSorting

    fun setFavorite(beach: Beach) {
        viewModelScope.launch {
            repository.insertFavorite(FavoriteEntity(beach.id))
        }
    }

    fun removeFavorite(beach: Beach) {
        viewModelScope.launch {
            repository.deleteFavorite(beach.id)
        }
    }

    fun startSorting() {
        _isSorting.value = true
    }

    fun finishSorting() {
        _isSorting.value = false
    }
}

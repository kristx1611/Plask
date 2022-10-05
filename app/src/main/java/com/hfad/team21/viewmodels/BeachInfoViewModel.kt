package com.hfad.team21.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.hfad.team21.database.AppDatabase
import com.hfad.team21.database.FavoriteEntity
import com.hfad.team21.domain.BeachInfo
import com.hfad.team21.repository.Repository
import kotlinx.coroutines.launch

class BeachInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(AppDatabase.getInstance(application).appDatabaseDao)

    private val _beachInfo = MutableLiveData<BeachInfo?>()

    val beachInfo: LiveData<BeachInfo?> = _beachInfo

    var isFavorite = false

    fun getBeachInfo(id: Int) {
        viewModelScope.launch {
            _beachInfo.value = repository.getBeachInformation(id)
        }
    }

    fun setFavorite(info: BeachInfo) {
        viewModelScope.launch {
            repository.insertFavorite(FavoriteEntity(info.id))
        }
    }

    fun removeFavorite(info: BeachInfo) {
        viewModelScope.launch {
            repository.deleteFavorite(info.id)
        }
    }
}

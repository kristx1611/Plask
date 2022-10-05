package com.hfad.team21.repository

import android.app.Activity
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.hfad.team21.database.AppDatabaseDao
import com.hfad.team21.database.BeachEntity
import com.hfad.team21.database.DistanceEntity
import com.hfad.team21.database.FavoriteEntity
import com.hfad.team21.domain.Beach
import com.hfad.team21.network.FrostApi
import com.hfad.team21.network.NowcastAPI
import com.hfad.team21.network.asDatabaseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.*

class Repository(private val dao: AppDatabaseDao) {

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            val ddFormat = DecimalFormat("00")
            val year = calendar.get(Calendar.YEAR)
            val month = ddFormat.format(calendar.get(Calendar.MONTH) + 1) // Calendar.get counts months from 0, not 1
            val day = ddFormat.format(calendar.get(Calendar.DAY_OF_MONTH))
            val queryDate = "$year-$month-${day}T00%3A00%3A00Z%2F$year-$month-${day}T23%3A59%3A59Z"
            val newBuoys = FrostApi.retrofitService.getResponse(queryDate)
            dao.insertBuoys(newBuoys.asDatabaseEntity())

            for (beach in dao.getAllBeaches()) {
                val weatherData = NowcastAPI.retrofitService.getResponse(beach.latitude.toString(), beach.longitude.toString())
                weatherData.beachId = beach.id
                dao.insertWeatherData(weatherData.asDatabaseEntity())
            }
        }
    }

    suspend fun insertFavorite(favorite: FavoriteEntity) {
        withContext(Dispatchers.IO) {
            dao.insertFavorite(favorite)
        }
    }

    suspend fun deleteFavorite(favoriteId: Int) {
        withContext(Dispatchers.IO) {
            dao.deleteFavorite(favoriteId)
        }
    }

    fun getCompleteBeachData(activity: Activity): LiveData<List<Beach>> = dao.getCompleteBeachData(getCity(activity))

    fun getCompleteFavoriteBeachData(): LiveData<List<Beach>> = dao.getCompleteFavoriteBeachData()

    fun getAllBeachEntitiesLiveData(): LiveData<List<BeachEntity>> = dao.getAllBeachesLiveData()

    suspend fun getAllBeachEntities(): List<BeachEntity> = dao.getAllBeaches()

    suspend fun updateDistance(distances: List<DistanceEntity>) {
        withContext(Dispatchers.IO) {
            dao.insertDistances(distances)
        }
    }

    suspend fun getBeachInformation(beachId: Int) = dao.getBeachInformation(beachId)

    fun getCity(activity: Activity): String {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val city = sharedPref.getString("city", null)
        return city ?: "Oslo"
    }

    fun setCity(city: String, activity: Activity) {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("city", city)
            apply()
        }
    }

    fun setLocation(pos: Location, activity: Activity) {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("lat", pos.latitude.toString())
            putString("long", pos.longitude.toString())
            apply()
        }
    }

    fun getLastLocation(activity: Activity): LatLng {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val lat: String? = sharedPref.getString("lat", null)
        val long: String? = sharedPref.getString("long", null)

        return if (lat == null || long == null) {
            getCityLocation(activity)
        } else
            LatLng(lat.toDouble(), long.toDouble())
    }

    fun getCityLocation(activity: Activity): LatLng {
        return when (getCity(activity)) {
            "Oslo" -> LatLng(59.9139, 10.7522)
            else -> LatLng(60.3913, 5.3221)
        }
    }
}

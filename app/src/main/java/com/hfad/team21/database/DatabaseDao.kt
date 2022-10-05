package com.hfad.team21.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.team21.domain.Beach
import com.hfad.team21.domain.BeachInfo

@Dao
interface AppDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuoys(buoys: List<BuoyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite_table WHERE favorite_id = :id")
    suspend fun deleteFavorite(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherDataEntity)

    @Query("SELECT * FROM beach_table")
    suspend fun getAllBeaches(): List<BeachEntity>

    @Query("SELECT * FROM beach_table")
    fun getAllBeachesLiveData(): LiveData<List<BeachEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistances(distances: List<DistanceEntity>)

    @Query(
        """
            SELECT beach.id,
                beach.name,
                buoy.temp,
                favorite.favorite_id,
                weather.weather_data_type,
                weather.wind,
                weather.air_temp,
                beach.latitude,
                beach.longitude,
                distance.distance,
                info.description,
                info.image,
                info.has_toilets,
                info.has_diving,
                info.has_booths,
                info.has_jetty
            FROM beach_table AS beach 
            CROSS JOIN buoy_table AS buoy """ + """
            LEFT JOIN favorite_table AS favorite ON beach.id = favorite.favorite_id 
            JOIN weather_data_table AS weather ON beach.id = weather.weather_data_id 
            LEFT JOIN distance_table AS distance ON beach.id = distance.id 
            INNER JOIN beach_info_table AS info ON beach.id = info.id
            WHERE beach.id = :beachId AND buoy.buoy_id IN 
                (SELECT to_buoy.buoy_id 
                FROM buoy_table AS to_buoy 
                CROSS JOIN beach_table AS from_beach 
                WHERE from_beach.id = :beachId 
                ORDER BY (from_beach.longitude - to_buoy.longitude) * 
                    (from_beach.longitude - to_buoy.longitude) + 
                    (from_beach.latitude - to_buoy.latitude) * 
                    (from_beach.latitude - to_buoy.latitude) 
                LIMIT 1) 
            LIMIT 1
            """
    )
    suspend fun getBeachInformation(beachId: Int): BeachInfo

    @Query(
        """
        SELECT beach.id,
               beach.name,
               buoy.temp,
               favorite.favorite_id,
               weather.weather_data_type,
               weather.wind,
               weather.air_temp,
               beach.latitude,
               beach.longitude,
               distance.distance
        FROM beach_table AS beach,
             buoy_table AS buoy """ + """
        LEFT JOIN favorite_table AS favorite
            ON beach.id = favorite.favorite_id
        JOIN weather_data_table AS weather
            ON beach.id = weather.weather_data_id
        LEFT JOIN distance_table as distance
            ON beach.id = distance.id
        WHERE beach.city = :city AND buoy.buoy_id IN
        (
            SELECT to_buoy.buoy_id AS nearest_buoy
            FROM buoy_table AS to_buoy,
                 beach_table AS from_beach
            WHERE from_beach.id = beach.id
            ORDER BY (from_beach.longitude - to_buoy.longitude) *
                     (from_beach.longitude - to_buoy.longitude) +
                     (from_beach.latitude - to_buoy.latitude) *
                     (from_beach.latitude - to_buoy.latitude)
            LIMIT 1
        )
        ORDER BY distance.distance"""
    )
    fun getCompleteBeachData(city: String): LiveData<List<Beach>>

    @Query(
        """
        SELECT beach.id,
               beach.name,
               buoy.temp,
               favorite.favorite_id,
               weather.weather_data_type,
               weather.wind,
               weather.air_temp,
               beach.latitude,
               beach.longitude,
               distance.distance
        FROM beach_table AS beach,
             buoy_table AS buoy """ + """
        JOIN favorite_table AS favorite
            ON beach.id = favorite.favorite_id
        JOIN weather_data_table AS weather
            ON beach.id = weather.weather_data_id
        LEFT JOIN distance_table as distance
            ON beach.id = distance.id
        WHERE buoy.buoy_id IN
        (
            SELECT to_buoy.buoy_id AS nearest_buoy
            FROM buoy_table AS to_buoy,
                 beach_table AS from_beach
            WHERE from_beach.id = beach.id
            ORDER BY (from_beach.longitude - to_buoy.longitude) *
                     (from_beach.longitude - to_buoy.longitude) +
                     (from_beach.latitude - to_buoy.latitude) *
                     (from_beach.latitude - to_buoy.latitude)
            LIMIT 1
        )
        ORDER BY distance.distance"""
    )
    fun getCompleteFavoriteBeachData(): LiveData<List<Beach>>
}

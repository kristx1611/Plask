package com.hfad.team21.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beach_table")
data class BeachEntity(
    @PrimaryKey
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "longitude")
    var longitude: Double,

    @ColumnInfo(name = "latitude")
    var latitude: Double,

    @ColumnInfo
    var city: String
)

@Entity(tableName = "beach_info_table")
data class BeachInfoEntity(
    @PrimaryKey
    var id: Int, // Id to BeachEntity

    @ColumnInfo(name = "image")
    var image: String,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "has_toilets")
    var hasToilets: Boolean,

    @ColumnInfo(name = "has_diving")
    var hasDiving: Boolean,

    @ColumnInfo(name = "has_booths")
    var hasBooths: Boolean,

    @ColumnInfo(name = "has_jetty")
    var hasJetty: Boolean
)

@Entity(tableName = "distance_table")
data class DistanceEntity(
    @PrimaryKey
    var id: Int, // Id to BeachEntity

    @ColumnInfo(name = "distance")
    var distance: Float
)

@Entity(tableName = "favorite_table")
data class FavoriteEntity(
    @PrimaryKey
    var favorite_id: Int // Id to BeachEntity
)

@Entity(tableName = "buoy_table")
data class BuoyEntity(
    @PrimaryKey
    var buoy_id: String,

    @ColumnInfo(name = "temp")
    var temp: String,

    @ColumnInfo(name = "longitude")
    var longitude: Double,

    @ColumnInfo(name = "latitude")
    var latitude: Double
)

@Entity(tableName = "weather_data_table")
data class WeatherDataEntity(
    @PrimaryKey
    var weather_data_id: Int, // Id to BeachEntity

    @ColumnInfo(name = "weather_data_type")
    var type: String,

    @ColumnInfo(name = "wind")
    var wind: Float,

    @ColumnInfo(name = "air_temp")
    var air_temp: Float
)

package com.hfad.team21.domain

data class Beach(
    val id: Int,
    val name: String,
    val temp: String,
    val favorite_id: Int?,
    val weather_data_type: String,
    val wind: Float,
    val air_temp: Float,
    val latitude: Double,
    val longitude: Double,
    val distance: Float?
)

data class BeachInfo(
    val id: Int,
    val name: String,
    val temp: String,
    val favorite_id: Int?,
    val weather_data_type: String,
    val wind: Float,
    val air_temp: Float,
    val latitude: Double,
    val longitude: Double,
    val distance: Float?,
    val description: String?,
    val image: String,
    val has_toilets: Boolean,
    val has_diving: Boolean,
    val has_booths: Boolean,
    val has_jetty: Boolean
)

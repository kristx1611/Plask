package com.hfad.team21.network

import com.hfad.team21.database.BuoyEntity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://havvarsel-frost.met.no/api/v1/obs/badevann/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface FrostApiService {
    @GET("get")
    suspend fun getResponse(@Query("time", encoded = true) date: String, @Query("incobs") alwaysTrue: Boolean = true): FrostAPIResponse
}

/*interface FrostApiService {
    @GET("get?time=latest&latestmaxage=86400&latestlimit=1&itemlimit=100000&incobs=true")
    suspend fun getResponse(): FrostAPIResponse
}*/

object FrostApi {
    val retrofitService: FrostApiService by lazy {
        retrofit.create(FrostApiService::class.java)
    }
}

fun FrostAPIResponse.asDatabaseEntity(): List<BuoyEntity> {
    val beaches = data?.tseries ?: return emptyList()
    return beaches.mapNotNull {
        val id: String? = it.header?.id?.buoyID
        val long: String? = it.header?.extra?.pos?.lon
        val lat: String? = it.header?.extra?.pos?.lat
        val temp: String? = it.observations?.get(0)?.body?.value

        if (temp == null || id == null || long == null || lat == null)
            null
        else
            BuoyEntity(id, temp, long.toDouble(), lat.toDouble())
    }
}

// JSON to Kotlin classes
/*data class FrostAPIResponse(val data: FrostAPIData?)

data class FrostAPIBody(val value: String?)

data class FrostAPIData(val tstype: String?, val tseries: List<FrostAPITseries>?)

data class FrostAPIExtra(val name: String?, val pos: FrostAPIPos?)

data class FrostAPIHeader(val id: FrostAPIId?, val extra: FrostAPIExtra?)

data class FrostAPIId(val buoyID: String?, val parameter: String?)

data class FrostAPIObservations(val time: String?, val body: FrostAPIBody?)

data class FrostAPIPos(val lon: String?, val lat: String?)

data class FrostAPITseries(val header: FrostAPIHeader?, val observations: List<FrostAPIObservations>?)*/

data class FrostAPIResponse(val data: FrostData?)

data class Body(val value: String?)

data class FrostData(val tstype: String?, val tseries: List<Tseries>?)

data class Extra(val name: String?, val pos: Pos?, val source: String?)

data class Header(val id: Id?, val extra: Extra?)

data class Id(val buoyID: String?, val parameter: String?)

data class Observations(val time: String?, val body: Body?)

data class Pos(val lat: String?, val lon: String?)

data class Tseries(val header: Header?, val observations: List<Observations>?)

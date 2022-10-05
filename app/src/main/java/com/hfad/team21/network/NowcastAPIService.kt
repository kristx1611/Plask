package com.hfad.team21.network
import com.hfad.team21.database.WeatherDataEntity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://in2000-apiproxy.ifi.uio.no/weatherapi/nowcast/2.0/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface NowcastAPIService {
    @GET("complete?")
    suspend fun getResponse(@Query("lat") lat: String, @Query("lon") lon: String): NowcastAPIResponse
}

object NowcastAPI {
    val retrofitService: NowcastAPIService by lazy {
        retrofit.create(NowcastAPIService::class.java)
    }
}

fun NowcastAPIResponse.asDatabaseEntity(): WeatherDataEntity {
    val id: Int = beachId
    val type: String = properties?.timeseries?.get(0)?.data?.next_1_hours?.summary?.symbol_code ?: "clearsky_day"
    val wind: Float = properties?.timeseries?.get(0)?.data?.instant?.details?.wind_speed?.toFloat() ?: 0.0f
    val airTemp: Float = properties?.timeseries?.get(0)?.data?.instant?.details?.air_temperature?.toFloat() ?: 0.0f

    return WeatherDataEntity(id, type, wind, airTemp)
}

// JSON til Kotlin klasser
data class NowcastAPIResponse(var beachId: Int, val type: String?, val geometry: Geometry?, val properties: Properties?)

data class Data(val instant: Instant?, val next_1_hours: NextHour?)

data class Details(val air_temperature: Number?, val precipitation_rate: Number?, val relative_humidity: Number?, val wind_from_direction: Number?, val wind_speed: Number?, val wind_speed_of_gust: Number?)

data class Geometry(val coordinates: List<Number>?)

data class Instant(val details: Details?)

data class Meta(val updated_at: String?)

data class NextHour(val summary: Summary?, val details: Details?)

data class Properties(val meta: Meta?, val timeseries: List<Timeseries>)

data class Summary(val symbol_code: String?)

data class Timeseries(val time: String?, val data: Data?)

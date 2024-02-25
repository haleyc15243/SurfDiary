package com.halebop.network.noaaservice

import com.google.gson.Gson
import com.halebop.network.utility.HttpClientResponse
import com.halebop.network.utility.httpClientResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.Call
import okhttp3.HttpUrl
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.logging.Logger
import kotlin.text.Charsets.UTF_8

internal class NOAAService private constructor(
    private val retrofit: NOAAServiceRetrofit,
    private val log: Logger
) {
    suspend fun getAllStations(
        key: String
    ): HttpClientResponse<NOAAStationsResponse> = httpClientResponse {
        retrofit.allStations(key)
    }

    suspend fun getStation(
        key: String,
        station: String
    ): HttpClientResponse<NOAAStationsResponse> = httpClientResponse {
        retrofit.station(station, key)
    }

    class Factory(
        private val callFactory: Call.Factory,
        private val gson: Gson
    ) {
        companion object {
            const val BASE_URL = "https://mw.buoybay.noaa.gov/api/v1/json/"
            const val HOST = "mw.buoybay.noaa.gov"
            const val JSON_PATH = "api/v1/json/"
            const val STATION_PATH = "station"
            const val KEY_PARAM = "key"
            const val TESTING_KEY = "f159959c117f473477edbdf3245cc2a4831ac61f"
        }

        fun create(): NOAAService {
            val retrofit = Retrofit.Builder()
                .baseUrl(createBaseUrl())
                .callFactory(callFactory)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return NOAAService(retrofit.create(), Logger.getGlobal())
        }

        private fun createBaseUrl() = HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .build()
    }
}

internal interface NOAAServiceRetrofit {

    @GET("api/v1/json/station")
    suspend fun allStations(@Query("key") apiKey: String): Response<NOAAStationsResponse>

    @GET("api/v1/json/station")
    suspend fun station(
        @Query("station") station: String,
        @Query("key") apiKey: String
    ): Response<NOAAStationsResponse>
}

@Serializable
internal class NOAAStationsResponse(
    @SerialName("stations") val stations: List<NOAAStationResponse>
)

@Serializable
internal class NOAAStationResponse(
    @SerialName("stationShortName") val stationShortName: String?,
    @SerialName("stationLongName") val stationLongName: String?,
    @SerialName("active") val active: Boolean,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("variable") val variable: List<NOAAReportResponse>? = emptyList()
)

@Serializable
internal class NOAAReportResponse(
    @SerialName("reportName") val reportName: String,
    @SerialName("actualName") val actualName: String,
    @SerialName("interval") val interval: Int,
    @SerialName("units") val units: String,
    @SerialName("group") val group: String,
    @SerialName("elevation") val elevation: String,
    @SerialName("measurements") val measurements: List<MeasurementResponse>
)

@Serializable
internal class MeasurementResponse(
    @SerialName("time") val time: String,
    @SerialName("value") val value: Double,
    @SerialName("qa") val qa: String?
)
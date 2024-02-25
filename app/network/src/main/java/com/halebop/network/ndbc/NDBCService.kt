package com.halebop.network.ndbc

import com.halebop.network.utility.HttpClientResponse
import com.halebop.network.utility.httpClientResponse
import com.halebop.web_types.LatLng
import com.halebop.web_types.NDBC
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Streaming
import java.util.logging.Logger

internal interface NDBCServiceRetrofit {
    @Streaming
    @GET("activestations.xml")
    suspend fun activeStations(): Response<NDBCActiveStationsResponse>
}
internal class NDBCService private constructor(
    private val retrofit: NDBCServiceRetrofit,
    private val log: Logger
) {
    class Factory(
        private val callFactory: Call.Factory
    ) {
        companion object {
            const val HOST = "ndbc.noaa.gov"
        }

        fun create(): NDBCService {
            val retrofit = Retrofit.Builder()
                .baseUrl(createBaseUrl())
                .callFactory(callFactory)
                .addConverterFactory(XML.asConverterFactory("text/xml".toMediaType()))
                .build()
            return NDBCService(retrofit.create(), Logger.getGlobal())
        }

        private fun createBaseUrl() = HttpUrl.Builder()
            .scheme("https")
            .host(HOST)
            .build()
    }
}


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

    suspend fun getActiveStations(): HttpClientResponse<NDBCActiveStationsResponse> = httpClientResponse {
        retrofit.activeStations()
    }
}

@Serializable
@XmlSerialName("stations")
data class NDBCActiveStationsResponse @JvmOverloads constructor(
    @XmlElement(true)
    @XmlSerialName("station")
    val stations: List<NDBCActiveStationResponse> = emptyList(),
    @XmlElement(false)
    @XmlSerialName("created")
    @Serializable(with = NDBCInstantSerializer::class)
    val created: Instant,
    @XmlElement(false)
    @XmlSerialName("count")
    val count: Int
)

@Serializable
@XmlElement(true)
@XmlSerialName("station")
data class NDBCActiveStationResponse(
    @XmlElement(false)
    @XmlSerialName("id")
    val id: Id,
    @XmlElement(false)
    @XmlSerialName("lat")
    val lat: Double,
    @XmlElement(false)
    @XmlSerialName("lon")
    val lon: Double,
    @XmlElement(false)
    @XmlSerialName("elev")
    val elev: Double?,
    @XmlElement(false)
    @XmlSerialName("name")
    val name: String,
    @XmlElement(false)
    @XmlSerialName("owner")
    val owner: Owner,
    // Program to which the station belongs
    @XmlElement(false)
    @XmlSerialName("pgm")
    val pgm: Program,
    @XmlElement(false)
    @XmlSerialName("type")
    val type: Type,
    @XmlElement(false)
    @XmlSerialName("seq") // TODO how does this work? (https://tao.ndbc.noaa.gov/tao/data_download/search_map.shtml)
    val seq: String? = null,
    // indicates whether the station has reported meteorological data in the past eight hours
    @XmlElement(false)
    @XmlSerialName("met")
    val met: Boolean = false,
    // indicates whether the station has reported water current data in the past eight hours
    @XmlElement(false)
    @XmlSerialName("currents")
    val currents: Boolean = false,
    // indicates whether the station has reported ocean chemistry data in the past eight hours
    @XmlElement(false)
    @XmlSerialName("waterquality")
    val waterQuality: Boolean = false,
    //  indicates whether the station has reported water column height/tsunami data in the past 24 hours
    @XmlElement(false)
    @XmlSerialName("dart")
    val dart: Boolean = false
) {
    fun toAppStation() = NDBC.Station(
        NDBC.Id(id.value),
        LatLng(lat, lon),
        elev?.let { NDBC.Elevation(it) },
        name,
        NDBC.Owner(owner.value),
        NDBC.Program(pgm.value),
        NDBC.Type.valueOf(type.name),
        seq,
        met,
        currents,
        waterQuality,
        dart
    )
}

@Serializable
enum class Type {
    @SerialName("fixed")
    FIXED,
    @SerialName("buoy")
    BUOY,
    @SerialName("dart")
    DART,
    @SerialName("oilrig")
    OIL_RIG,
    @SerialName("usv")
    USV,
    @SerialName("tao")
    TAO,
    @SerialName("other")
    OTHER
}

@Serializable
@JvmInline
value class Id(val value: String)

@Serializable
@JvmInline
value class Program(val value: String)

@Serializable
@JvmInline
value class Owner(val value: String)

object NDBCInstantSerializer : KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant =
        decoder.decodeString().replace("UTC", "Z").let {
            return Instant.parse(it)
        }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

}


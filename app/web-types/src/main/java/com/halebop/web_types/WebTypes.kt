package com.halebop.web_types

data class LatLng(val lat: Double, val lng: Double) {
    override fun toString() = "($lat, $lng)"
}

data class Location(
    val id: Long,
    val name: String,
    val latLng: LatLng
) {
    fun toAndroidLocation() = android.location.Location("").apply {
        latitude = latLng.lat
        longitude = latLng.lng
    }
}

class NOAA {

    data class Station(
        val id: Long,
        val stationShortName: String?,
        val stationLongName: String?,
        val active: Boolean,
        val latitude: Double,
        val longitude: Double,
        val variable: List<Report>?
    )

    data class Report(
        val id: Long,
        val reportName: String,
        val actualName: String,
        val interval: Int,
        val units: String,
        val group: String,
        val elevation: String,
        val stationId: Long,
        val measurements: List<Measurement>
    )

    data class Measurement(
        val time: String,
        val value: Double,
        val QA: String?,
        val reportId: Long,
    )
}

class NDBC {
    data class Station(
        val id: Id,
        val latLng: LatLng,
        val elevation: Elevation?,
        val name: String,
        val owner: Owner,
        val program: Program,
        val type: Type,
        val seq: String? = null,
        val hasMeteorologicalData: Boolean,
        val hasCurrents: Boolean,
        val hasWaterQuality: Boolean,
        val hasDart: Boolean
    )
    
    @JvmInline
    value class Id(val value: String)
    
    @JvmInline
    value class Elevation(val value: Double)
    
    @JvmInline
    value class Owner(val value: String)

    @JvmInline
    value class Program(val value: String)

    enum class Type {
        FIXED,
        BUOY,
        DART,
        OIL_RIG,
        USV,
        TAO,
        OTHER
    }
}
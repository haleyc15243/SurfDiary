package com.halebop.web_types

data class LatLng(val lat: Double, val lng: Double) {
    override fun toString() = "($lat, $lng)"
}

data class Location(
    val id: Long,
    val name: String,
    val latLng: LatLng
)

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
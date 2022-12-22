package com.halebop.web_types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class NOAAStationResponse(
    @SerialName("stationShortName") val stationShortName: String,
    @SerialName("stationLongName") val stationLongName: String,
    @SerialName("active") val active: Boolean,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("variable") val variable: List<NOAAReportResponse>
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
    @SerialName("qa") val qa: String
)

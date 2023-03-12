package com.halebop.surfdiary

import com.halebop.surfdiary.database.Noaa_stationQueries
import com.halebop.web_types.Measurement
import com.halebop.web_types.Report
import com.halebop.web_types.Station
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface NOAADataSource {
    fun getStations(): Flow<List<Station>?>
    suspend fun insertStations(stations: List<Station>)
    suspend fun insertStation(station: Station)
    suspend fun deleteStation(id: Long)
    suspend fun deleteAllStations()
}

class NOAADataSourceImpl(
    private val stationQueries: Noaa_stationQueries
): NOAADataSource {

    // TODO replace dispatchers
    override fun getStations(): Flow<List<Station>?> {
        return stationQueries.getAllStations().asFlow().map { query ->
            query.executeAsList().let { stationList ->
                stationList.map { station ->
                    val reports = stationQueries.getReportsForStation(station.id).executeAsList()
                        .map { report ->
                            val measurements =
                                stationQueries.getMeasurementForReport(report.id).executeAsList()
                                    .map {
                                        Measurement(
                                            time = it.time,
                                            value = it.value_,
                                            QA = it.qa,
                                            reportId = it.report_id
                                        )
                                    }
                            Report(
                                id = report.id,
                                reportName = report.report_name,
                                actualName = report.actual_name,
                                interval = report.interval.toInt(),
                                units = report.units,
                                group = report.group_name,
                                elevation = report.elevation,
                                measurements = measurements,
                                stationId = station.id
                            )
                        }
                    Station(
                        id = station.id,
                        stationShortName = station.station_short_name,
                        stationLongName = station.station_long_name,
                        active = station.active,
                        latitude = station.latitude,
                        longitude = station.longitude,
                        variable = reports
                    )
                }
            }
        }
    }

    override suspend fun insertStations(stations: List<Station>) {
        stations.forEach {
            insertStation(it)
        }
    }

    override suspend fun insertStation(station: Station) {
        return withContext(Dispatchers.IO) {
            val reports = station.variable
            stationQueries.insertOrReplaceStation(
                station.id,
                station.stationShortName,
                station.stationLongName,
                station.active,
                station.latitude,
                station.longitude
            )
            reports?.forEach { report ->
                stationQueries.insertOrUpdateReports(
                    report.id,
                    report.reportName,
                    report.actualName,
                    report.interval.toLong(),
                    report.units,
                    report.group,
                    report.elevation,
                    station.id
                )
                report.measurements.forEach { measurement ->
                    stationQueries.insertOrUpdateMeasurement(
                        measurement.time,
                        measurement.value,
                        measurement.QA,
                        report.id
                    )
                }
            }
        }
    }

    override suspend fun deleteStation(id: Long) {
        withContext(Dispatchers.IO) {
            stationQueries.deleteStation(id)
        }
    }

    override suspend fun deleteAllStations() {
        withContext(Dispatchers.IO) {
            stationQueries.deleteAllStations()
        }
    }


}
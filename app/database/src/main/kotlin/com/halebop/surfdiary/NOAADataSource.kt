package com.halebop.surfdiary

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
    db: ConditionsDatabase
): NOAADataSource {

    private val stationsDatabase = db.noaa_stationQueries

    // TODO replace dispatchers
    override fun getStations(): Flow<List<Station>?> {
        return stationsDatabase.getAllStations().asFlow().map { query ->
            query.executeAsList().let { stationList ->
                stationList.map { station ->
                    val reports = stationsDatabase.getReportsForStation(station.id).executeAsList()
                        .map { report ->
                            val measurements =
                                stationsDatabase.getMeasurementForReport(report.id).executeAsList()
                                    .map {
                                        Measurement(
                                            id = it.id,
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
            stationsDatabase.insertOrReplaceStation(
                station.id,
                station.stationShortName,
                station.stationLongName,
                station.active,
                station.latitude,
                station.longitude
            )
            reports?.forEach { report ->
                stationsDatabase.insertOrUpdateReports(
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
                    stationsDatabase.insertOrUpdateMeasurement(
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
            stationsDatabase.deleteStation(id)
        }
    }

    override suspend fun deleteAllStations() {
        withContext(Dispatchers.IO) {
            stationsDatabase.deleteAllStations()
        }
    }


}
package com.halebop.network

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.FetcherResult
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.google.gson.Gson
import com.halebop.network.noaaservice.NOAAReportResponse
import com.halebop.network.noaaservice.NOAAService
import com.halebop.network.noaaservice.NOAAService.Factory.Companion.TESTING_KEY
import com.halebop.network.noaaservice.NOAAStationResponse
import com.halebop.network.noaaservice.NOAAStationsResponse
import com.halebop.network.utility.HttpClientResponse
import com.halebop.surfdiary.NOAADataSource
import com.halebop.web_types.Measurement
import com.halebop.web_types.Report
import com.halebop.web_types.Station
import okhttp3.Call

class NetworkServicesFactory(
    callFactory: Call.Factory,
    gson: Gson
) {

    private val noaaService = NOAAService.Factory(callFactory, gson).create()

    fun noaaStationsStore(
        dataStore: NOAADataSource
    ): Store<Long, List<Station>> {
        return StoreBuilder
            .from(
                fetcher = Fetcher.ofResult { getStations() },
                sourceOfTruth = SourceOfTruth.of<Long, List<Station>, List<Station>>(
                    reader = { dataStore.getStations() },
                    writer = { _, response -> dataStore.insertStations(response) },
                    delete = { dataStore.deleteStation(it) },
                    deleteAll = { dataStore.deleteAllStations() }
                )
            ).build()
    }

    private suspend fun getStations(): FetcherResult<List<Station>> {
        return when (val response = noaaService.getAllStations(TESTING_KEY)) {
            is HttpClientResponse.Success -> FetcherResult.Data(response.data.toStationsList())
            is HttpClientResponse.Exception -> FetcherResult.Error.Exception(response.e)
            is HttpClientResponse.Error -> FetcherResult.Error.Exception(Exception(response.errorBody))
        }
    }

    private fun NOAAStationsResponse.toStationsList() =
            stations.map {
                val stationId = it.stationId()
                Station(
                    id = stationId,
                    stationShortName = it.stationShortName,
                    stationLongName = it.stationLongName,
                    active = it.active,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    variable = it.variable?.map { report ->
                        val reportId = report.reportId()
                        Report(
                            id = reportId,
                            reportName = report.reportName,
                            actualName = report.actualName,
                            interval = report.interval,
                            units = report.units,
                            group = report.group,
                            elevation = report.elevation,
                            stationId = stationId,
                            measurements = report.measurements.map { measurement ->
                                Measurement(
                                    time = measurement.time,
                                    value = measurement.value,
                                    QA = measurement.qa,
                                    reportId = reportId
                                )
                            }
                        )
                    }
                )
            }

    private fun NOAAStationResponse.stationId(): Long { return (stationShortName + stationLongName).hashCode().toLong() }
    private fun NOAAReportResponse.reportId(): Long { return (reportName + actualName).hashCode().toLong() }
}
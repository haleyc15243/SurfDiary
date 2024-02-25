package com.halebop.surfdiary

import com.halebop.surfdiary.database.Ndbc_stationQueries
import com.halebop.web_types.NDBC
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface NDBCDataSource {
    fun updateLastUpdate(lastUpdate: Instant)
    fun getLastUpdated(): Instant?
    fun getActiveStations(): Flow<List<NDBC.Station>>
    fun insertStation(station: NDBC.Station)
}

class NDBCDataSourceImpl(
    private val stationQueries: Ndbc_stationQueries
) : NDBCDataSource {

    override fun updateLastUpdate(lastUpdate: Instant) {
        stationQueries.clearLastUpdated()
        stationQueries.updateLastUpdated(lastUpdate)
    }

    override fun getLastUpdated(): Instant? {
        return stationQueries.fetchLastUpdated().executeAsOneOrNull()
            ?.last_updated
    }
    override fun getActiveStations(): Flow<List<NDBC.Station>> {
        TODO("Not yet implemented")
    }

    override fun insertStation(station: NDBC.Station) {
        stationQueries.insertOrUpdateStation(
            station.id,
            station.latLng.lat,
            station.latLng.lng,
            station.elevation,
            station.name,
            station.owner,
            station.program,
            station.type,
            station.seq,
            station.hasMeteorologicalData,
            station.hasCurrents,
            station.hasWaterQuality,
            station.hasDart
        )
    }
}
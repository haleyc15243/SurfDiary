package com.halebop.surfdiary

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.halebop.surfdiary.database.Ndbc_last_update
import com.halebop.surfdiary.database.Ndbc_station
import com.halebop.web_types.NDBC

class DatabaseFactory(
    driver: SqlDriver
) {
    private fun createDatabase(driver: SqlDriver) = DiaryDatabase.invoke(
        driver = driver,
        ndbc_last_updateAdapter = ndbcLastUpdateAdapter,
        ndbc_stationAdapter = ndbcStationAdapter
    )

    private val ndbcLastUpdateAdapter = Ndbc_last_update.Adapter(
        kotlinxInstantAdapter
    )

    private val ndbcStationAdapter = Ndbc_station.Adapter(
        inlineValue<NDBC.Id, String>({ it.value }, { NDBC.Id(it) }),
        inlineValue<NDBC.Elevation, Double>({ it.value }, { NDBC.Elevation(it) }),
        inlineValue<NDBC.Owner, String>({ it.value }, { NDBC.Owner(it) }),
        inlineValue<NDBC.Program, String>({ it.value }, { NDBC.Program(it) }),
        EnumColumnAdapter()
    )

    private val database by lazy { createDatabase(driver) }

    fun noaaDataSource() = NOAADataSourceImpl(database.noaa_stationQueries)
    fun ndbcDataSource() = NDBCDataSourceImpl(database.ndbc_stationQueries)
    fun locationDataSource() = LocationDatasourceImpl(database.locationQueries)
}
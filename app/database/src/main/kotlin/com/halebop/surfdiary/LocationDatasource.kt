package com.halebop.surfdiary

import com.halebop.surfdiary.database.LocationQueries
import com.halebop.web_types.LatLng
import com.halebop.web_types.Location
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocationDatasource {
    fun insertLocation(name: String?, latLng: LatLng)
    fun locationsFlow(): Flow<List<Location>>
    fun selectLocation(id: Long): Location?
    fun deleteLocation(id: Long)
    fun deleteAllLocations()
}

class LocationDatasourceImpl(
    private val locationQueries: LocationQueries
): LocationDatasource {
    private fun com.halebop.surfdiary.database.Location.toLocation() = Location(id, name, LatLng(latitude, longitude))
    override fun insertLocation(name: String?, latLng: LatLng) {
        val nameString = name ?: ("Location" + locationQueries.getLocationCount())
        locationQueries.insertOrReplace(nameString, latLng.lat, latLng.lng)
    }

    override fun locationsFlow(): Flow<List<Location>> {
        return locationQueries.getAllLocations().asFlow().map { query ->
            query.executeAsList().map { it.toLocation() }
        }
    }

    override fun selectLocation(id: Long): Location? {
        return locationQueries.getLocation(id).executeAsOneOrNull()?.toLocation()
    }

    override fun deleteLocation(id: Long) {
        locationQueries.deleteLocation(id)
    }

    override fun deleteAllLocations() {
        locationQueries.deleteAll()
    }

}
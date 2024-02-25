package com.halebop.surfdiary.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.halebop.network.NetworkServicesFactory
import com.halebop.surfdiary.NDBCDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class NDBCActiveStationsWorker @AssistedInject constructor(
    private val dataSource: NDBCDataSource,
    private val networkServicesFactory: NetworkServicesFactory,
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val stationsResponse = networkServicesFactory.ndbcActiveStations() ?: return Result.failure()
        dataSource.updateLastUpdate(stationsResponse.created)
        for (station in stationsResponse.stations) {
            dataSource.insertStation(station.toAppStation())
        }
        return Result.success()
    }
}
package com.halebop.surfdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.halebop.surfdiary.ui.SurfDiaryAppState
import com.halebop.surfdiary.ui.SurfDiaryNavigation
import com.halebop.surfdiary.ui.rememberSurfDiaryAppState
import com.halebop.surfdiary.ui.theme.SurfDiaryTheme
import com.halebop.surfdiary.work.NDBCActiveStationsWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurfDiaryTheme {
                SurfDiaryApp()
            }
        }

        schedulePeriodicWork()
    }

    private fun schedulePeriodicWork() {
        WorkManager.getInstance(this).apply {
            for (request in onStartUpWorkers()) {
                enqueue(request)
            }
        }
    }

    private fun onStartUpWorkers() =
        listOf(
            ndbcActiveStationsWorkRequest()
        )

    private fun ndbcActiveStationsWorkRequest() =
        OneTimeWorkRequestBuilder<NDBCActiveStationsWorker>()
            .setConstraints(
                Constraints(requiredNetworkType = NetworkType.CONNECTED)
            )
            .build()
}

@Composable
private fun SurfDiaryApp(
    appState: SurfDiaryAppState = rememberSurfDiaryAppState()
) {
    SurfDiaryNavigation(appState)
}
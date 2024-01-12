package com.halebop.surfdiary

import SurfDiaryTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.halebop.surfdiary.ui.SurfDiaryAppState
import com.halebop.surfdiary.ui.SurfDiaryNavigation
import com.halebop.surfdiary.ui.rememberSurfDiaryAppState
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
    }
}

@Composable
private fun SurfDiaryApp(
    appState: SurfDiaryAppState = rememberSurfDiaryAppState()
) {
    SurfDiaryNavigation(appState)
}
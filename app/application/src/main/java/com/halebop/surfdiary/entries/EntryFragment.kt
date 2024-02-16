package com.halebop.surfdiary.entries

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.fresh
import com.halebop.location_services.LocationSensitivity
import com.halebop.location_services.LocationUtils
import com.halebop.surfdiary.LocationDatasource
import com.halebop.surfdiary.ui.Destination
import com.halebop.surfdiary.ui.ExpandableCard
import com.halebop.surfdiary.ui.LabeledValue
import com.halebop.surfdiary.ui.SurfDiaryAppState
import com.halebop.surfdiary.ui.SurfDiaryScaffold
import com.halebop.web_types.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

interface EntryPageUiEvent
sealed class EntryPageViewModelUiEvent: EntryPageUiEvent
sealed class EntryPageFragmentUiEvent: EntryPageUiEvent
class EntryPageStateHolder(
    val state: State<LocationEntryViewModel.EntryUiState>,
    val viewModelEventHandler: (EntryPageViewModelUiEvent) -> Unit,
    val fragmentEventHandler: (EntryPageFragmentUiEvent) -> Unit
)

@Composable
private fun rememberEntryState(
    viewModel: LocationEntryViewModel,
    lifecycle: Lifecycle,
    viewModelEventHandler: (EntryPageViewModelUiEvent) -> Unit,
    fragmentEventHandler: (EntryPageFragmentUiEvent) -> Unit
): EntryPageStateHolder {
    val state = viewModel.uiStateFlow.collectAsStateWithLifecycle(initialValue = LocationEntryViewModel.EntryUiState.Initial, lifecycle)
    return EntryPageStateHolder(state, viewModelEventHandler, fragmentEventHandler)
}

@Composable
fun EntryScreen(appState: SurfDiaryAppState) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val viewModel: LocationEntryViewModel = hiltViewModel()
    val stateHolder = rememberEntryState(
        viewModel = viewModel,
        lifecycle = lifecycle,
        viewModelEventHandler = { event ->
            //viewModel.onUiEvent(event)
        },
        fragmentEventHandler = { event ->
            //when (event) {
//                is EntryPageViewModelUiEvent
            //}
        }
    )
    EntryScreen(
        appState = appState,
        state = stateHolder.state.value,
    ) { event ->
        when (event) {
            is EntryPageFragmentUiEvent -> {
                stateHolder.fragmentEventHandler.invoke(event)
            }
            is EntryPageViewModelUiEvent -> {
                stateHolder.viewModelEventHandler.invoke(event)
            }
        }
    }
}

@Composable
private fun EntryScreen(
    appState: SurfDiaryAppState,
    state: LocationEntryViewModel.EntryUiState,
    onUiEvent: (EntryPageUiEvent) -> Unit
) {
    SurfDiaryScaffold(
        appState = appState,
        topAppBarActions = {

        }
    ) {
        when (state) {
            is LocationEntryViewModel.EntryUiState.Initial -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            is LocationEntryViewModel.EntryUiState.UiState -> {
                if (state.location == null) {
                    DiaryEntryListFragmentNoLocationContent()
                } else {
                    DiaryEntryListFragmentContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        state.location
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryEntryListFragmentNoLocationContent() {
    Text("No location information") // TODO
}

@Composable
private fun DiaryEntryListFragmentContent(
    modifier: Modifier = Modifier,
    station: Station
) {
    val title = station.stationLongName ?: station.stationShortName ?: station.id.toString()
    ExpandableCard(
        modifier = modifier.padding(8.dp),
        title = title
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.Start
        ) {
            LabeledValue(
                label = "ID",
                labelValue = station.id.toString()
            )
            station.stationShortName?.let {
                LabeledValue(
                    label = "Short Name",
                    labelValue = it
                )
            }
            station.stationLongName?.let {
                LabeledValue(
                    label = "Long Name",
                    labelValue = it
                )
            }
            LabeledValue(
                label = "Longitude",
                labelValue = station.longitude.toString()
            )
            LabeledValue(
                label = "Latitude",
                labelValue = station.latitude.toString()
            )
            LabeledValue(
                label = "Active",
                labelValue = station.active.toString()
            )

            val reports = station.variable
            if (reports != null) { // TODO variable should default to emptylist
                Spacer(modifier = Modifier.height(8.dp))
                for (report in reports) {
                    LabeledValue(
                        label = report.reportName,
                        labelValue = report.measurements.firstOrNull()?.value.toString()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DiaryEntryListFragmentContentPreview() {
    DiaryEntryListFragmentContent(
        station = Station(
            id = 12345L,
            stationShortName = "Short name",
            stationLongName = "Station long name",
            active = true,
            latitude = 38.9634,
            longitude = -76.4467,
            variable = emptyList()
        )
    )
}

@HiltViewModel
class LocationEntryViewModel @Inject constructor(
    locationDatasource: LocationDatasource,
    savedStateHandle: SavedStateHandle,
    locationUtils: LocationUtils,
    private val noaaDataDataStore: Store<Long, List<Station>>
) : ViewModel() {
    private val entryLocation = savedStateHandle.get<String>(Destination.LocationDetails.ID_KEY)?.let {
        locationDatasource.selectLocation(it.toLong())
    }
    sealed class EntryUiState {
        data object Initial: EntryUiState()
        data class UiState(
            val location: Station?
        ): EntryUiState()
    }
}
package com.halebop.surfdiary.locations

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.halebop.surfdiary.LocationDatasource
import com.halebop.surfdiary.application.R
import com.halebop.surfdiary.ui.AppTextEntry
import com.halebop.surfdiary.ui.SaveCancelDialog
import com.halebop.surfdiary.ui.SurfDiaryAppState
import com.halebop.surfdiary.ui.SurfDiaryScaffold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface AddLocationFragmentUiEvent

sealed class AddLocationFragmentFragmentUiEvent: AddLocationFragmentUiEvent {
    data object NavigateBack : AddLocationFragmentFragmentUiEvent()
}

sealed class AddLocationFragmentViewModelUiEvent: AddLocationFragmentUiEvent {
    data class SetSelectedLocation(val location: AddLocationViewModel.SelectedLocation): AddLocationFragmentViewModelUiEvent()
    data class SaveConfirmed(val location: AddLocationViewModel.SelectedLocation, val name: String?): AddLocationFragmentViewModelUiEvent()
    data object SavePressed: AddLocationFragmentViewModelUiEvent()
    data object CancelSavePressed: AddLocationFragmentViewModelUiEvent()
}

data class AddLocationFragmentStateHolder(
    val state: State<AddLocationViewModel.AddLocationFragmentUiState>,
    val viewModelEventHandler: (AddLocationFragmentViewModelUiEvent) -> Unit,
    val fragmentEventHandler: (AddLocationFragmentFragmentUiEvent) -> Unit
)

@Composable
private fun rememberAddLocationState(
    viewModel: AddLocationViewModel,
    lifecycle: Lifecycle,
    fragmentEventHandler: (AddLocationFragmentFragmentUiEvent) -> Unit,
    viewModelEventHandler: (AddLocationFragmentViewModelUiEvent) -> Unit
) = AddLocationFragmentStateHolder(
    viewModel.uiStateFlow().collectAsStateWithLifecycle(initialValue = AddLocationViewModel.AddLocationFragmentUiState.Initial, lifecycle),
    viewModelEventHandler, fragmentEventHandler
)

@Composable
fun AddLocationScreen(appState: SurfDiaryAppState) {
    val viewModel: AddLocationViewModel = hiltViewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val stateHolder = rememberAddLocationState(
        viewModel = viewModel,
        lifecycle = lifecycle,
        fragmentEventHandler = { event ->
            when (event) {
                AddLocationFragmentFragmentUiEvent.NavigateBack -> {
                    appState.navigateBack()
                }
            }
        },
        viewModelEventHandler = { event ->
            viewModel.onUiEvent(event)
        }
    )
    AddLocationScreen(
        appState = appState,
        state = stateHolder.state.value
    ) { event ->
        when (event) {
            is AddLocationFragmentViewModelUiEvent -> {
                stateHolder.viewModelEventHandler.invoke(event)
            }
            is AddLocationFragmentFragmentUiEvent -> {
                stateHolder.fragmentEventHandler.invoke(event)
            }
        }
    }
}

@Composable
private fun AddLocationScreen(
    appState: SurfDiaryAppState,
    state: AddLocationViewModel.AddLocationFragmentUiState,
    onUiEvent: (AddLocationFragmentUiEvent) -> Unit
) {
    SurfDiaryScaffold(
        appState = appState,
        topAppBarActions = {
            TextButton(onClick = { onUiEvent(AddLocationFragmentViewModelUiEvent.SavePressed) }) {
                Text(text = stringResource(id = R.string.button_title_save))
            }
        }
    ) {
        AddLocationFragmentContent(
            state = state,
            onUiEvent = onUiEvent
        )
    }
}

@Composable
private fun AddLocationFragmentContent(
    state: AddLocationViewModel.AddLocationFragmentUiState,
    onUiEvent: (AddLocationFragmentUiEvent) -> Unit
) {
    when (state) {
        AddLocationViewModel.AddLocationFragmentUiState.Initial -> CircularProgressIndicator()
        is AddLocationViewModel.AddLocationFragmentUiState.UiState -> {
            if (state.shouldShowSaveDialog) {
                SaveLocationDialog(
                    suggestedName = state.selectedLocation?.latLng?.toString(),
                    saveLocation = {
                        onUiEvent.invoke(AddLocationFragmentViewModelUiEvent.SaveConfirmed(state.selectedLocation!!, it))
                        onUiEvent.invoke(AddLocationFragmentFragmentUiEvent.NavigateBack)
                    },
                    onDismissRequest = { onUiEvent(AddLocationFragmentViewModelUiEvent.CancelSavePressed) }
                )
            }
            AddLocationFragmentMap(
                selectedLocation = state.selectedLocation,
                onLocationSelected = { location ->
                    onUiEvent(AddLocationFragmentViewModelUiEvent.SetSelectedLocation(location))
                }
            )
        }
    }
}

@Composable
private fun AddLocationFragmentMap(
    selectedLocation: AddLocationViewModel.SelectedLocation?,
    onLocationSelected: (AddLocationViewModel.SelectedLocation) -> Unit
) {
    val singapore = LatLng(1.35, 103.87)
    val startLocation = selectedLocation?.latLng ?: singapore
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = {
            onLocationSelected(AddLocationViewModel.SelectedLocation(it))
        }
    ) {
        selectedLocation?.latLng?.let {
            Marker(
                state = MarkerState(it)
            )
        }
    }
}

@Composable
fun SaveLocationDialog(
    suggestedName: String?,
    saveLocation: (String?) -> Unit,
    onDismissRequest: () -> Unit
) {
    val currentText = rememberSaveable { mutableStateOf("") }
    SaveCancelDialog(
        onSavePressed = { saveLocation(currentText.value.takeUnless { it.isEmpty() } ?: suggestedName) },
        onDismissRequest = onDismissRequest
    ) {
        AppTextEntry(
            currentText = currentText.value,
            onValueChange = {
                currentText.value = it
            },
            enabled = true,
            label = {
                Text(stringResource(R.string.label_location_name))
            },
            placeholder = {
                suggestedName?.let { Text(it) }
            }
        )
    }
}

@HiltViewModel
class AddLocationViewModel @Inject constructor(
    private val database: LocationDatasource
): ViewModel() {
    data class SelectedLocation(val latLng: LatLng)
    sealed class AddLocationFragmentUiState {
        data object Initial: AddLocationFragmentUiState()
        data class UiState(
            val selectedLocation: SelectedLocation?,
            val shouldShowSaveDialog: Boolean
        ): AddLocationFragmentUiState()
    }

    private val shouldShowSaveDialogStateFlow = MutableStateFlow(false)
    private val selectedLocation = MutableStateFlow<SelectedLocation?>(null)

    fun onUiEvent(event: AddLocationFragmentViewModelUiEvent) {
        when (event) {
            is AddLocationFragmentViewModelUiEvent.SaveConfirmed -> {
                database.insertLocation(
                    event.name,
                    event.location.latLng.let { com.halebop.web_types.LatLng(it.latitude, it.longitude) }
                )
            }
            AddLocationFragmentViewModelUiEvent.SavePressed -> {
                shouldShowSaveDialogStateFlow.value = true
            }
            AddLocationFragmentViewModelUiEvent.CancelSavePressed -> {
                shouldShowSaveDialogStateFlow.value = false
            }
            is AddLocationFragmentViewModelUiEvent.SetSelectedLocation -> {
                selectedLocation.value = event.location
            }
        }
    }

    fun uiStateFlow() = combine(selectedLocation, shouldShowSaveDialogStateFlow) { selectedLocation, shouldShowSaveDialog ->
        AddLocationFragmentUiState.UiState(selectedLocation, shouldShowSaveDialog)
    }
}
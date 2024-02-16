package com.halebop.surfdiary.locations

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.halebop.surfdiary.LocationDatasource
import com.halebop.surfdiary.application.R
import com.halebop.surfdiary.ui.AppCardListItem
import com.halebop.surfdiary.ui.AppTextEntry
import com.halebop.surfdiary.ui.CircularBackgroundIcon
import com.halebop.surfdiary.ui.SaveCancelDialog
import com.halebop.surfdiary.ui.SurfDiaryAppState
import com.halebop.surfdiary.ui.SurfDiaryScaffold
import com.halebop.web_types.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface LocationsFragmentUiEvent

sealed class LocationsFragmentFragmentUiEvent: LocationsFragmentUiEvent {
    data class LocationClicked(val id: Long): LocationsFragmentFragmentUiEvent()
}

class LocationsFragmentStateHolder(
    val state: State<LocationsViewModel.LocationsListUiState>,
    val viewModelEventHandler: (LocationsFragmentViewModelUiEvent) -> Unit,
    val fragmentEventHandler: (LocationsFragmentFragmentUiEvent) -> Unit
)

@Composable
private fun rememberLocationListState(
    viewModel: LocationsViewModel,
    lifecycle: Lifecycle,
    viewModelEventHandler: (LocationsFragmentViewModelUiEvent) -> Unit,
    fragmentEventHandler: (LocationsFragmentFragmentUiEvent) -> Unit
): LocationsFragmentStateHolder {
    val locationListState = viewModel.uiStateFlow.collectAsStateWithLifecycle(initialValue = LocationsViewModel.LocationsListUiState.Initial, lifecycle)
    return LocationsFragmentStateHolder(locationListState, viewModelEventHandler, fragmentEventHandler)
}

@Composable
fun LocationListScreen(appState: SurfDiaryAppState) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val viewModel: LocationsViewModel = hiltViewModel()
    val stateHolder = rememberLocationListState(
        viewModel = viewModel,
        lifecycle = lifecycle,
        viewModelEventHandler = { event ->
            viewModel.onUiEvent(event)
        },
        fragmentEventHandler = { event ->
            when (event) {
                is LocationsFragmentFragmentUiEvent.LocationClicked -> {
                    //TODO
                }
            }
        }
    )
    LocationsListScreen(
        appState = appState,
        state = stateHolder.state.value
    ) { event ->
        when (event) {
            is LocationsFragmentFragmentUiEvent -> {
                stateHolder.fragmentEventHandler.invoke(event)
            }
            is LocationsFragmentViewModelUiEvent -> {
                stateHolder.viewModelEventHandler.invoke(event)
            }
        }
    }
}

@Composable
private fun LocationsListScreen(
    appState: SurfDiaryAppState,
    state: LocationsViewModel.LocationsListUiState,
    onUiEvent: (LocationsFragmentUiEvent) -> Unit
) {
    SurfDiaryScaffold(
        appState = appState,
        topAppBarActions = {
            when (state) {
                LocationsViewModel.LocationsListUiState.Initial -> {
                    // No toolbar icons
                }
                is LocationsViewModel.LocationsListUiState.UiState -> {
                    val multiSelectMode = state.selectedLocationIds.isNotEmpty()
                    if (multiSelectMode) {
                        val showEditMenuItem = state.selectedLocationIds.size == 1
                        EditModeToolbarIcons(
                            showEdit = showEditMenuItem,
                            edit = {
                                onUiEvent.invoke(LocationsFragmentViewModelUiEvent.ShowLocationEditDialog)
                            },
                            delete = {
                                onUiEvent.invoke(LocationsFragmentViewModelUiEvent.DeleteLocations)
                            }
                        )
                    } else {
                        IconButton(onClick = { appState.navigateToAddLocation() }) {
                            Icon(
                                imageVector = Icons.Filled.AddCircleOutline,
                                contentDescription = "Add Location"
                            )
                        }
                    }
                }
            }
        }
    ) {
        LocationsListScreenContent(
            state = state,
            onUiEvent = onUiEvent
        )
    }
}

@Composable
private fun LocationsListScreenContent(
    state: LocationsViewModel.LocationsListUiState,
    onUiEvent: (LocationsFragmentUiEvent) -> Unit
) {
    when (state) {
        LocationsViewModel.LocationsListUiState.Initial -> CircularProgressIndicator()
        is LocationsViewModel.LocationsListUiState.UiState -> {
            if (state.showEditLocationDialog) {
                EditLocationDialog(
                    currentName = "", // TODO state.,
                    updateLocation = {
                        onUiEvent.invoke(LocationsFragmentViewModelUiEvent.UpdateLocationName(it))
                    },
                    onDismissRequest = {
                        onUiEvent.invoke(LocationsFragmentViewModelUiEvent.ShowLocationEditDialog)
                    }
                )
            }
            LocationItemList(
                locationList = state.locations,
                selectedLocationIds = state.selectedLocationIds,
                addSelectedLocation = { onUiEvent.invoke(LocationsFragmentViewModelUiEvent.SetSelected(true, it.id)) },
                removeSelectedLocation = { onUiEvent.invoke(LocationsFragmentViewModelUiEvent.SetSelected(false, it.id)) },
                onUiEvent = onUiEvent
            )
        }
    }
}

@Composable
private fun EditLocationDialog(
    currentName: String,
    updateLocation: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val currentText = rememberSaveable { mutableStateOf("") }
    SaveCancelDialog(
        onSavePressed = { updateLocation.invoke(currentText.value) },
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
                Text(currentName)
            }
        )
    }
}

@Composable
private fun LocationItemList(
    locationList: List<Location>,
    selectedLocationIds: List<Long>,
    addSelectedLocation: (Location) -> Unit,
    removeSelectedLocation: (Location) -> Unit,
    onUiEvent: (LocationsFragmentUiEvent) -> Unit
) {
    LazyColumn {
        items(locationList) { item ->
            LocationItem(
                showCheckBox = selectedLocationIds.isNotEmpty(),
                checked = selectedLocationIds.contains(item.id),
                setChecked = { selected -> if (selected) addSelectedLocation(item) else removeSelectedLocation(item) },
                location = item,
                onClick = {
                    onUiEvent(LocationsFragmentFragmentUiEvent.LocationClicked(it))
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LocationItem(
    showCheckBox: Boolean,
    checked: Boolean,
    setChecked: (Boolean) -> Unit,
    location: Location,
    distance: String? = null,
    onClick: (Long) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        setChecked(true)
                    },
                    onTap = {
                        if (showCheckBox) {
                            setChecked(!checked)
                        } else onClick(location.id)
                    },
                    onPress = {
                        if (showCheckBox) {
                            setChecked(!checked)
                        } else onClick(location.id)
                    }
                )
            }
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        AppCardListItem {
            if (showCheckBox) {
                Checkbox(checked = checked, onCheckedChange = setChecked)
            }
            CircularBackgroundIcon(
                icon = Icons.Filled.LocationOn
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(location.name, style = MaterialTheme.typography.h6)
                distance?.let { Text(it) }
            }
        }
    }
}

@Composable
private fun EditModeToolbarIcons(
    showEdit: Boolean,
    edit: () -> Unit,
    delete: () -> Unit
) {
    if (showEdit) {
        IconButton(onClick = edit) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Add Location"
            )
        }
    }
    IconButton(onClick = delete) {
        Icon(
            imageVector = Icons.Filled.DeleteForever,
            contentDescription = "Add Location"
        )
    }
}

sealed class LocationsFragmentViewModelUiEvent: LocationsFragmentUiEvent {
    data object ShowLocationEditDialog: LocationsFragmentViewModelUiEvent()
    data class UpdateLocationName(val newName: String): LocationsFragmentViewModelUiEvent()
    data object DeleteLocations: LocationsFragmentViewModelUiEvent()
    data class SetSelected(val selected: Boolean, val id: Long): LocationsFragmentViewModelUiEvent()
}

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val locationDatasource: LocationDatasource,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val showEditLocationDialogFlow = MutableStateFlow(false)
    private val selectedLocationIds = MutableStateFlow<List<Long>>(savedStateHandle["selectedLocationIds"] ?: emptyList())
    private val locationsFlow = locationDatasource.locationsFlow()

    fun onUiEvent(event: LocationsFragmentViewModelUiEvent) {
        when (event) {
            LocationsFragmentViewModelUiEvent.ShowLocationEditDialog -> {
                showEditLocationDialogFlow.value = !showEditLocationDialogFlow.value
            }
            is LocationsFragmentViewModelUiEvent.UpdateLocationName -> {
                // TODO locationDatasource.
            }
            is LocationsFragmentViewModelUiEvent.DeleteLocations -> {
                for (id in selectedLocationIds.value) {
                    selectedLocationIds.value = selectedLocationIds.value.minus(id)
                    locationDatasource.deleteLocation(id)
                }
            }
            is LocationsFragmentViewModelUiEvent.SetSelected -> {
                selectedLocationIds.value =
                    if (event.selected) selectedLocationIds.value.plus(event.id)
                    else selectedLocationIds.value.minus(event.id)
            }
        }
    }

    sealed class LocationsListUiState {
        data object Initial: LocationsListUiState()
        data class UiState(
            val locations: List<Location>,
            val selectedLocationIds: List<Long>,
            val showEditLocationDialog: Boolean
        ): LocationsListUiState()
    }

    val uiStateFlow = combine(
        locationsFlow,
        selectedLocationIds,
        showEditLocationDialogFlow
    ) { locationsList, selectedIds, showEditLocationDialog ->
        LocationsListUiState.UiState(locationsList, selectedIds, showEditLocationDialog)
    }
}
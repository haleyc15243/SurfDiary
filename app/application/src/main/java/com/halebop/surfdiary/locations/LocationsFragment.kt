package com.halebop.surfdiary.locations

import SurfDiaryTheme
import android.os.Bundle
import android.view.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.halebop.surfdiary.application.R
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.halebop.surfdiary.LocationDatasource
import com.halebop.surfdiary.ui.AppCardListItem
import com.halebop.surfdiary.ui.CircularBackgroundIcon
import com.halebop.web_types.Location
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class LocationsFragment: Fragment() {

    private val viewModel: LocationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SurfDiaryTheme {
                    val stateHolder = rememberLocationListState(
                        viewModel = viewModel,
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
                    LocationsFragmentScreen(stateHolder = stateHolder)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createMenu()
    }

    private fun createMenu() {
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_locations_fragment, menu)
                    viewModel.editModeLiveData.observe(viewLifecycleOwner) { editMode ->
                        val deleteButton = menu.findItem(R.id.menu_item_delete)
                        deleteButton.isVisible = editMode
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.menu_item_add -> {
                            true.also { navigateToAddLocationFragment() }
                        }
                        R.id.menu_item_delete -> {
                            true.also { viewModel.onUiEvent(LocationsFragmentViewModelUiEvent.DeleteLocations)}
                        }
                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun navigateToAddLocationFragment() {
        findNavController().navigate(LocationsFragmentDirections.actionLocationFragmentToAddLocationFragment())
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    private fun rememberLocationListState(
        viewModel: LocationsViewModel,
        viewModelEventHandler: (LocationsFragmentViewModelUiEvent) -> Unit,
        fragmentEventHandler: (LocationsFragmentFragmentUiEvent) -> Unit
    ): LocationsFragmentStateHolder {
        val locationListState = viewModel.uiStateFlow.collectAsStateWithLifecycle(initialValue = LocationsViewModel.LocationsListUiState.Initial, lifecycle)
        return LocationsFragmentStateHolder(locationListState, viewModelEventHandler, fragmentEventHandler)
    }
}

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
private fun LocationsFragmentScreen(
    stateHolder: LocationsFragmentStateHolder
) {
    val state = stateHolder.state
    LocationsListScreenContent(
        state = state.value,
        onUiEvent = { event ->
            when (event) {
                is LocationsFragmentFragmentUiEvent -> {
                    stateHolder.fragmentEventHandler.invoke(event)
                }
                is LocationsFragmentViewModelUiEvent -> {
                    stateHolder.viewModelEventHandler.invoke(event)
                }
            }
        }
    )
}

@Composable
private fun LocationsListScreenContent(
    state: LocationsViewModel.LocationsListUiState,
    onUiEvent: (LocationsFragmentUiEvent) -> Unit
) {
    Surface {
        when (state) {
            LocationsViewModel.LocationsListUiState.Initial -> CircularProgressIndicator()
            is LocationsViewModel.LocationsListUiState.UiState -> {
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
                iconRes = android.R.drawable.ic_menu_compass
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(location.name, style = MaterialTheme.typography.h6)
                distance?.let { Text(it) }
            }
        }
    }
}

sealed class LocationsFragmentViewModelUiEvent: LocationsFragmentUiEvent {
    object DeleteLocations: LocationsFragmentViewModelUiEvent()
    data class SetSelected(val selected: Boolean, val id: Long): LocationsFragmentViewModelUiEvent()
}

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val locationDatasource: LocationDatasource,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    fun onUiEvent(event: LocationsFragmentViewModelUiEvent) {
        when (event) {
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

    fun saveState() {
        savedStateHandle["selectedLocationIds"] = selectedLocationIds.value
    }

    sealed class LocationsListUiState {
        object Initial: LocationsListUiState()
        data class UiState(val locations: List<Location>, val selectedLocationIds: List<Long>): LocationsListUiState()
    }
    private val selectedLocationIds = MutableStateFlow<List<Long>>(savedStateHandle["selectedLocationIds"] ?: emptyList())
    private val locationsFlow = locationDatasource.locationsFlow()

    val editModeLiveData = selectedLocationIds.map { it.isNotEmpty() }.asLiveData()
    val uiStateFlow = combine(locationsFlow, selectedLocationIds) { locationsList, selectedIds ->
        LocationsListUiState.UiState(locationsList, selectedIds)
    }
}
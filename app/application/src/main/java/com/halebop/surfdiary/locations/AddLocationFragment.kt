package com.halebop.surfdiary.locations

import SurfDiaryTheme
import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
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
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@AndroidEntryPoint
class AddLocationFragment: Fragment() {

    @Inject
    lateinit var database: LocationDatasource

    private val viewModel: AddLocationViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SurfDiaryTheme {
                    val stateHolder = rememberAddLocationState(
                        viewModel = viewModel,
                        fragmentEventHandler = { event ->
                            when (event) {
                                is AddLocationFragmentFragmentUiEvent.SaveLocation -> {
                                    database.insertLocation(
                                        event.name,
                                        event.location.latLng.let { com.halebop.web_types.LatLng(it.latitude, it.longitude) }
                                    )
                                    findNavController().popBackStack()
                                }
                            }
                        },
                        viewModelEventHandler = { event ->
                            viewModel.onUiEvent(event)
                        }
                    )
                    AddLocationFragmentScreen(
                        stateHolder
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_save, menu)
                }
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.menu_item_save -> {
                            viewModel.onUiEvent(AddLocationFragmentViewModelUiEvent.SavePressed)
                            true
                        }
                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    private fun rememberAddLocationState(
        viewModel: AddLocationViewModel,
        fragmentEventHandler: (AddLocationFragmentFragmentUiEvent) -> Unit,
        viewModelEventHandler: (AddLocationFragmentViewModelUiEvent) -> Unit
    ) = AddLocationFragmentStateHolder(
        viewModel.uiStateFlow.collectAsStateWithLifecycle(initialValue = AddLocationViewModel.AddLocationFragmentUiState.Initial, lifecycle),
        viewModelEventHandler, fragmentEventHandler
    )
}

interface AddLocationFragmentUiEvent

sealed class AddLocationFragmentFragmentUiEvent: AddLocationFragmentUiEvent {
    data class SaveLocation(val location: AddLocationViewModel.SelectedLocation, val name: String?): AddLocationFragmentFragmentUiEvent()
}

sealed class AddLocationFragmentViewModelUiEvent: AddLocationFragmentUiEvent {
    data class SetSelectedLocation(val location: AddLocationViewModel.SelectedLocation): AddLocationFragmentViewModelUiEvent()
    object SavePressed: AddLocationFragmentViewModelUiEvent()
    object CancelSavePressed: AddLocationFragmentViewModelUiEvent()
}

data class AddLocationFragmentStateHolder(
    val state: State<AddLocationViewModel.AddLocationFragmentUiState>,
    val viewModelEventHandler: (AddLocationFragmentViewModelUiEvent) -> Unit,
    val fragmentEventHandler: (AddLocationFragmentFragmentUiEvent) -> Unit
)

@Composable
private fun AddLocationFragmentScreen(
    stateHolder: AddLocationFragmentStateHolder
) {
    AddLocationFragmentContent(
        state = stateHolder.state.value,
        onUiEvent = { event ->
            when (event) {
                is AddLocationFragmentViewModelUiEvent -> {
                    stateHolder.viewModelEventHandler.invoke(event)
                }
                is AddLocationFragmentFragmentUiEvent -> {
                    stateHolder.fragmentEventHandler.invoke(event)
                }
            }
        }
    )
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
                        onUiEvent.invoke(AddLocationFragmentFragmentUiEvent.SaveLocation(state.selectedLocation!!, it))
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
): ViewModel() {
    data class SelectedLocation(val latLng: LatLng)
    sealed class AddLocationFragmentUiState {
        object Initial: AddLocationFragmentUiState()
        data class UiState(
            val selectedLocation: SelectedLocation?,
            val shouldShowSaveDialog: Boolean
        ): AddLocationFragmentUiState()
    }

    fun onUiEvent(event: AddLocationFragmentViewModelUiEvent) {
        when (event) {
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

    private val shouldShowSaveDialogStateFlow = MutableStateFlow(false)
    private val selectedLocation = MutableStateFlow<SelectedLocation?>(null)
    val uiStateFlow = combine(selectedLocation, shouldShowSaveDialogStateFlow) { selectedLocation, shouldShowSaveDialog ->
        AddLocationFragmentUiState.UiState(selectedLocation, shouldShowSaveDialog)
    }
}
package com.halebop.surfdiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.halebop.surfdiary.theme.SurfDiaryTheme
import com.halebop.web_types.LatLng
import com.halebop.web_types.Station
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment: Fragment() {

    val viewModel: WelcomeFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SurfDiaryTheme {
                    val state = viewModel.stationsFlow.observeAsState()
                    NOAAStationsContent(state = state)
                }
            }
        }
    }
}

@Composable
private fun NOAAStationsContent(
    state: State<List<Station>?>
) {
    val data = state.value
    if (data != null && data.isNotEmpty()) {
        var dropDownShownState = rememberSaveable { mutableStateOf(false) }
        val stationNameList = data.mapNotNull { it.stationLongName ?: it.stationShortName }.sorted()
        var selectedStationId = rememberSaveable { mutableStateOf(0) }
        Column(modifier = Modifier.fillMaxSize()) {
            Dropdown(stationNameList[selectedStationId.value]) { dropDownShownState.value = true }
            DropdownMenu(
                expanded = dropDownShownState.value,
                onDismissRequest = { dropDownShownState.value = false })
            {
                stationNameList.minusElement(stationNameList[selectedStationId.value])
                    .forEachIndexed { index, name ->
                        DropdownMenuItem(
                            onClick = { selectedStationId.value = index },
                            text = { Text(name) }
                        )
                    }
            }
            NOAAStationInfo(station = data[selectedStationId.value])
        }
    }
}

@Composable
private fun Dropdown(
    selectedText: String,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .clickable { onClick() }
    ) {
        Text(selectedText, style = MaterialTheme.typography.headlineSmall)
        Icon(painter = painterResource(android.R.drawable.arrow_down_float), contentDescription = null)
    }
}

@Composable
private fun NOAAStationInfo(
    station: Station
) {
    Column {
        station.stationShortName?.let { LabeledValue(label = "Station short name", value = it) }
        station.stationLongName?.let { LabeledValue(label = "Station long name", value = it) }
        LabeledValue(label = "Active", value = station.active.toString())
        LabeledValue(label = "Coordinates", value = LatLng(station.latitude, station.longitude).toString())
    }
}

@Composable
fun LabeledValue(
    label: String,
    value: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("$label:", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(0.5f))
        Text(value, modifier = Modifier.weight(0.5f))
    }
}

@HiltViewModel
class WelcomeFragmentViewModel @Inject constructor(
    noaaDatastore: Store<Long, List<Station>>
): ViewModel() {

    val stationsFlow = noaaDatastore.stream(StoreRequest.fresh(1L))
        .map {
            when (it) {
                is StoreResponse.Data -> it.value
                else -> emptyList()
            }
        }.asLiveData()
}
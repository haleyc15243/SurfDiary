package com.halebop.surfdiary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.halebop.surfdiary.application.R
import com.halebop.surfdiary.entries.EntryScreen
import com.halebop.surfdiary.locations.AddLocationScreen
import com.halebop.surfdiary.locations.LocationListScreen

@Composable
fun SurfDiaryNavigation(appState: SurfDiaryAppState) {
    val startDestination = Destination.LocationList
    NavHost(
        navController = appState.navController,
        startDestination = startDestination.fullRoute,
    ) {
        composable(route = Destination.LocationList.fullRoute) {
            LocationListScreen(appState)
        }
        composable(route = Destination.AddLocation.fullRoute) {
            AddLocationScreen(appState)
        }
        composable(
            route = Destination.LocationDetails.fullRoute,
            arguments = listOf(navArgument(Destination.LocationDetails.ID_KEY) { NavType.LongType })
        ) {
            EntryScreen(appState = appState)
        }
    }
}

@Composable
fun SurfDiaryScaffold(
    appState: SurfDiaryAppState,
    topAppBarActions: @Composable RowScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        topBar = {
            SurfDiaryTopAppBar(appState = appState, actions = topAppBarActions)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurfDiaryTopAppBar(
    appState: SurfDiaryAppState,
    actions: @Composable RowScope.() -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            SurfDiaryNavigationTitle(appState.navController.currentBackStackEntry)
        },
        navigationIcon = {
            SurfDiaryNavigationIcon(appState)
        },
        actions = {
            actions()
        }
    )
}

@Composable
private fun SurfDiaryNavigationIcon(appState: SurfDiaryAppState) {
    val topBackStackEntry = appState.navController.currentBackStackEntry
    if (Destination.LocationList.route == topBackStackEntry?.id) {
        IconButton(onClick = { appState.navigateBack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Navigate Up"
            )
        }
    }
}

@Composable
private fun SurfDiaryNavigationTitle(currentBackStackEntry: NavBackStackEntry?) {
    val title = when (currentBackStackEntry?.destination?.route) {
        Destination.LocationList.route -> stringResource(id = R.string.app_name)
        Destination.AddLocation.route -> "Add Location"
        Destination.LocationDetails.route -> "Location Details"
        else -> null
    }
    title?.let {
        Text(
            title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
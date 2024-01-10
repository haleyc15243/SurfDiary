package com.halebop.surfdiary

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

sealed class Screen(val name: String, val route: String) {
    //    companion object {
//        val topLevelDestinations = listOf(LocationList.route)
//    }
    //TODO replace routes with string resources
    data object LocationList : Screen("Map", "LocationList")
    data object LocationDetails : Screen("Home", "LocationDetails")
    data object AddLocation : Screen("Messages", "AddLocation")
}

@Composable
fun rememberSurfDiaryAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(key1 = navController, key2 = context) {
    SurfDiaryAppState(navController, context)
}

class SurfDiaryAppState(
    val navController: NavHostController,
    private val context: Context
) {
    fun navigateBack() = navController.popBackStack()
    fun navigateToAddLocation() = navController.navigate(Screen.AddLocation.route)
}
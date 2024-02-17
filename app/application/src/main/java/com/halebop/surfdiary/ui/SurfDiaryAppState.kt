package com.halebop.surfdiary.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

sealed class Destination(val route: String, vararg arguments: String) {
    val fullRoute: String = StringBuilder(route).apply {
        arguments.forEach {
            append("/{$it}")
        }
    }.toString()
    sealed class NoArgumentsDestination(route: String) : Destination(route) {
        operator fun invoke(): String = route
    }
    data object LocationList : NoArgumentsDestination("LocationList")
    data object AddLocation : NoArgumentsDestination("AddLocation")
    data object LocationDetails : Destination("LocationDetails", "locationId") {
        const val ID_KEY = "locationId"
        operator fun invoke(locationId: Long) = appendParams(ID_KEY to locationId)
    }

    internal fun appendParams(vararg params: Pair<String, Any?>) = StringBuilder(route).apply {
        params.forEach {
            it.second?.toString()?.let { arg ->
                append("/$arg")
            }
        }
    }.toString()
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
    fun navigateToAddLocation() = navController.navigate(Destination.AddLocation.route)
    fun navigateToEntry(id: Long) = navController.navigate(Destination.LocationDetails(id))
}
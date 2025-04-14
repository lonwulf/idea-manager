package com.lonwulf.ideamanager.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

interface NavComposable {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Composable(
        navHostController: NavHostController,
        sheetState: SheetState,
        snackbarHostState: SnackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    composable: Map<String, NavComposable>,
    sheetState: SheetState,
    snackbarHostState: SnackbarHostState
) {
    val destination = TopLevelDestinations.HomeScreen.route


    NavHost(navController = navHostController, startDestination = destination) {
        composable.forEach { (route, composable) ->
            composable(route = route) {
                composable.Composable(
                    navHostController = navHostController,
                    sheetState = sheetState,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}
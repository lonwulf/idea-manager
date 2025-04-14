package com.lonwulf.ideamanager.presentation.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.lonwulf.ideamanager.navigation.NavComposable

class SettingsScreenComposable : NavComposable {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        sheetState: SheetState,
        snackbarHostState: SnackbarHostState
    ) {
        SettingsScreen()
    }
}

@Composable
fun SettingsScreen() {

}
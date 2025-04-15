package com.lonwulf.ideamanager.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lonwulf.ideamanager.R
import com.lonwulf.ideamanager.navigation.NavComposable
import com.lonwulf.ideamanager.util.LightModeManager
import org.koin.java.KoinJavaComponent.inject

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
fun SettingsScreen(modifier: Modifier = Modifier) {
    var isEnabled by remember { mutableStateOf(false) }
    val lightModeManager: LightModeManager by inject(LightModeManager::class.java)
    val isDark by remember { lightModeManager.isDark }
    val text by remember(isDark) {
        derivedStateOf {
            if (isDark) "Toggle light mode" else "Toggle night mode"
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.lightbulb),
                    contentDescription = "Toggle Icon",
                    tint = MaterialTheme.colorScheme.error.copy(0.9f),
                    modifier = Modifier
                        .size(35.dp)
                        .padding(end = 8.dp)
                )

                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = {
                    lightModeManager.toggleMode()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.background,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }

    }
}
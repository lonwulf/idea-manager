package com.lonwulf.ideamanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lonwulf.ideamanager.navigation.Destinations
import com.lonwulf.ideamanager.navigation.NavigationGraph
import com.lonwulf.ideamanager.navigation.TopLevelDestinations
import com.lonwulf.ideamanager.presentation.component.IdeaManagerFloatingActionButton
import com.lonwulf.ideamanager.presentation.screens.HomeScreenComposable
import com.lonwulf.ideamanager.presentation.screens.SettingsScreenComposable
import com.lonwulf.ideamanager.presentation.screens.StatisticsScreenComposable
import com.lonwulf.ideamanager.presentation.screens.TasksScreenComposable
import com.lonwulf.ideamanager.presentation.viewmodel.SharedViewModel
import com.lonwulf.ideamanager.taskmanager.presentation.screens.CreateTasksScreenComposable
import com.lonwulf.ideamanager.taskmanager.presentation.screens.TaskDetailsScreenComposable
import com.lonwulf.ideamanager.ui.theme.BlueLight
import com.lonwulf.ideamanager.ui.theme.BluePrimary
import com.lonwulf.ideamanager.ui.theme.IdeaManagerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IdeaManagerTheme {
                val sheetState = rememberModalBottomSheetState()
                val sharedViewModel = koinViewModel<SharedViewModel>()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val snackBarHostState = remember { SnackbarHostState() }
                var fabActionState by remember { mutableStateOf(false) }

                val hideAppBarsInScreen =
                    listOf(
                        Destinations.TaskDetailsScreen.route,
                        Destinations.CreateTasksScreen.route
                    )
                val showAppBars = currentDestination?.route !in hideAppBarsInScreen
                val screensToShowFAB = listOf(
                    TopLevelDestinations.HomeScreen.route,
                    TopLevelDestinations.AllTasksScreen.route
                )
                val showFAB = currentDestination?.route in screensToShowFAB

                Scaffold(
                    snackbarHost = { SnackbarHost(snackBarHostState) },
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showAppBars) {
                            BottomNavigation(
                                navHostController = navController,
                                currentDestination = currentDestination
                            )
                        }
                    },
                    topBar = {
                        Column {
                            if (showAppBars) {
                                Toolbar(currentDestination?.route ?: "")
                            }
                        }
                    })
                { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val composables = mapOf(
                            Destinations.TaskDetailsScreen.route to TaskDetailsScreenComposable(),
                            Destinations.CreateTasksScreen.route to CreateTasksScreenComposable(),
                            TopLevelDestinations.HomeScreen.route to HomeScreenComposable(
                                sharedViewModel
                            ),
                            TopLevelDestinations.AllTasksScreen.route to TasksScreenComposable(
//                                sharedViewModel
                            ),
                            TopLevelDestinations.SettingsScreen.route to SettingsScreenComposable(
//                                sharedViewModel
                            ),
                            TopLevelDestinations.StatisticsScreen.route to StatisticsScreenComposable(
                                sharedViewModel
                            )
                        )
                        NavigationGraph(
                            navHostController = navController,
                            composable = composables,
                            sheetState = sheetState,
                            snackbarHostState = snackBarHostState
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomNavigation(
        navHostController: NavHostController,
        currentDestination: NavDestination?
    ) {
        val screens = listOf(
            TopLevelDestinations.HomeScreen,
            TopLevelDestinations.AllTasksScreen,
            TopLevelDestinations.StatisticsScreen,
            TopLevelDestinations.SettingsScreen
        )
        Box {
            NavigationBar(
                containerColor = Color.White,
                contentColor = BluePrimary,
                tonalElevation = 5.dp
            ) {
                screens.forEachIndexed { index, screen ->
                    if (index == 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    AddItem(
                        screen = screen,
                        currentDestination = currentDestination,
                        navHostController = navHostController
                    )
                }
            }
            IdeaManagerFloatingActionButton(fabClicked = {
                navHostController.navigate(Destinations.CreateTasksScreen.route)
            })
        }
    }

    @Composable
    private fun RowScope.AddItem(
        screen: TopLevelDestinations,
        currentDestination: NavDestination?,
        navHostController: NavHostController
    ) {
        NavigationBarItem(
            label = { Text(text = screen.title) },
            colors = NavigationBarItemColors(
                selectedIconColor = BluePrimary,
                selectedTextColor = BluePrimary,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                selectedIndicatorColor = BlueLight,
                disabledIconColor = Color.Gray,
                disabledTextColor = Color.Gray
            ),
            selected = currentDestination?.route == screen.route,
            onClick = {
                navHostController.navigate(screen.route) {
                    popUpTo(navHostController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            },
            icon = {
                BadgedBox(badge = {}) {
                    Icon(imageVector = screen.icon, contentDescription = "bottom bar icon")
                }
            })
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Toolbar(title: String) {
        TopAppBar(
            title = { Text(text = title) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                scrolledContainerColor = colorResource(
                    id = R.color.white
                ),
                navigationIconContentColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.background,
                actionIconContentColor = colorResource(
                    id = R.color.white
                )
            )
        )
    }

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IdeaManagerTheme {
        Greeting("Android")
    }
}
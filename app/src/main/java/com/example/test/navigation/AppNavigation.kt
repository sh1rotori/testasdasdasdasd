package com.example.test.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.test.screens.EventDetailsScreen
import com.example.test.screens.EventsScreen
import com.example.test.screens.RecordsScreen
import com.example.test.screens.TicketsScreen
import com.example.test.screens.loadEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                listOfNavItems.forEach { navItem ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                        onClick = {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.EventsScreen.name,
            modifier = Modifier
                .padding(paddingValues)
        ) {
            composable(route = Screens.EventsScreen.name) {
                // Pass the events list to EventsScreen
                EventsScreen(navController = navController, events = loadEvents())
            }
            composable(route = Screens.TicketsScreen.name) {
                TicketsScreen()
            }
            composable(route = Screens.RecordsScreen.name) {
                RecordsScreen()
            }
            composable(
                route = "${Screens.EventDetailsScreen.name}/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val arguments = requireNotNull(backStackEntry.arguments)
                val eventId = arguments.getInt("eventId")
                EventDetailsScreen(
                    navController = navController,
                    eventId = eventId,
                    // Pass the events list to EventDetailsScreen
                    events = loadEvents()
                )
            }
        }
    }
}

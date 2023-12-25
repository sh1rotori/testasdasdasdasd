package com.example.test.navigation

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.test.screens.CreateTicketScreen
import com.example.test.screens.Event
import com.example.test.screens.EventDetailsScreen
import com.example.test.screens.EventViewModel
import com.example.test.screens.EventsScreen
import com.example.test.screens.RecordsScreen
import com.example.test.screens.TicketViewModel
import com.example.test.screens.TicketsScreen
import com.example.test.screens.readEventsData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.fragment.app.activityViewModels
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.screens.TicketDetailsScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val eventViewModel = remember { EventViewModel() }
    val ticketViewModel = remember { TicketViewModel() }

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
                val eventViewModel: EventViewModel = viewModel()
                EventsScreen(eventViewModel) { selectedEvent ->
                    // Log the selected eventId to check if it's correct
                    Log.d("EventDetailsScreen", "Selected EventId: $selectedEvent")

                    // Handle the event click and navigate to EventDetailsScreen with eventId parameter
                    navController.navigate("${Screens.EventDetailsScreen.name}/${selectedEvent.eventId}")

                }
            }

            composable(route = Screens.TicketsScreen.name) {
                TicketsScreen(navController = navController, ticketViewModel = ticketViewModel)
            }

            composable(route = Screens.RecordsScreen.name) {
                RecordsScreen()
            }
            val eventsData: List<Event> = readEventsData()

            composable(route = Screens.EventDetailsScreen.name + "/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                val event = readEventsData().find { it.eventId == eventId }

                if (event != null) {
                    EventDetailsScreen(event = event) {
                        // Handle any interaction or back navigation from EventDetailsScreen
                    }
                } else {
                    // Display an error message or navigate back
                    Text("Событие не найдено")
                }
            }


            composable(route = Screens.CreateTicketScreen.name) {
                CreateTicketScreen(navController = navController, ticketViewModel = ticketViewModel)
            }

            composable(route = Screens.TicketDetailsScreen.name + "/{ticketId}") { backStackEntry ->
                val ticketId = backStackEntry.arguments?.getString("ticketId")
                val ticket = ticketViewModel.tickets.find { it.id == ticketId }

                if (ticket != null) {
                    TicketDetailsScreen(
                        ticket = ticket,
                        onSaveImageClick = {
                            // Логика сохранения изображения в галерее
                            // Показать системное уведомление об успешном сохранении
                        }
                    )
                } else {
                    // Обработка ситуации, когда билет не найден
                    Text("Билет не найден")
                }
            }
        }
        // Получаем контекст из Compose
        val context = LocalContext.current
        DisposableEffect(Unit) {
            // Создаем ярлыки для экранов Events, Tickets, Records
            createShortcutForScreen(context, Screens.RecordsScreen.name, "Records", Icons.Default.Send)
            createShortcutForScreen(context, Screens.TicketsScreen.name, "Tickets", Icons.Default.MailOutline)
            createShortcutForScreen(context, Screens.EventsScreen.name, "Events", Icons.Default.List)


            onDispose { }
        }
    }
}

fun createShortcutForScreen(
    context: Context,
    screenName: String,
    label: String,
    icon: ImageVector
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)

        val shortcut = ShortcutInfo.Builder(context, screenName)
            .setShortLabel(label)
            .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
            .setIntent(
                Intent(context, MainActivity::class.java)
                    .setAction(Intent.ACTION_VIEW)
                    .putExtra("shortcut", screenName)
            )
            .build()

        shortcutManager?.dynamicShortcuts = listOf(shortcut)
    }
}

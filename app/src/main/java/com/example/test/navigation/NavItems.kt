package com.example.test.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val listOfNavItems = listOf(
    NavItem(
        label = "Events",
        icon = Icons.Default.List,
        route = Screens.EventsScreen.name
    ),
    NavItem(
        label = "Tickets",
        icon = Icons.Default.MailOutline,
        route = Screens.TicketsScreen.name
    ),
    NavItem(
        label = "Records",
        icon = Icons.Default.Phone,
        route = Screens.RecordsScreen.name
    )
)
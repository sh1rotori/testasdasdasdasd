package com.example.test.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.test.navigation.Screens
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

// EventType.kt
enum class EventType {
    OPENING_CEREMONY,
    CLOSING_CEREMONY
}
// TicketViewModel.kt
class TicketViewModel : ViewModel() {
    private val _tickets = mutableStateListOf<Ticket>()
    val tickets: List<Ticket> get() = _tickets

    // Функция для получения билетов по типу
    fun getTicketsByType(type: EventType): List<Ticket> {
        return _tickets.filter { it.type == type }
    }

    fun addTicket(ticket: Ticket) {
        _tickets.add(ticket)
        Log.d("TicketViewModel", "Ticket added: $ticket")
    }
}

// Ticket.kt
data class Ticket(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: EventType,
    val imageUri: Uri? = null,
    val seatLocation: String = generateRandomPlace(), // Добавьте это свойство
    // Добавьте другие свойства билета, если необходимо
)





// В TicketsScreen
@Composable
fun TicketsScreen(navController: NavController, ticketViewModel: TicketViewModel) {
    val tickets by remember { mutableStateOf(ticketViewModel.tickets) }

    LaunchedEffect(tickets.size) {
        Log.d("TicketsScreen", "Tickets changed: $tickets")
    }

    val openingCeremonyTickets = ticketViewModel.getTicketsByType(EventType.OPENING_CEREMONY)
    val closingCeremonyTickets = ticketViewModel.getTicketsByType(EventType.CLOSING_CEREMONY)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Список из билетов",
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        item {
            Button(
                onClick = {
                    navController.navigate(Screens.CreateTicketScreen.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Создать новый билет")
            }
        }

        item {
            Text(
                text = "Билеты церемоний открытия:",
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            OpeningCeremonyTicketGroup(tickets = openingCeremonyTickets) { ticket ->
                Log.d("TicketsScreen", "Opening ceremony ticket clicked: $ticket")
                if (ticket.imageUri != null) {
                    Log.d("TicketsScreen", "Image URI: ${ticket.imageUri}")
                    navController.navigate("${Screens.TicketDetailsScreen.name}/${ticket.id}")
                } else {
                    Log.e("TicketsScreen", "Image URI is null. Ticket ID: ${ticket.id}")
                    // Добавьте этот код, чтобы убедиться, что URI действительно null
                    ticketViewModel.tickets.indexOfFirst { it.id == ticket.id }.takeIf { it != -1 }?.let { index ->
                        Log.e("TicketsScreen", "Ticket at index $index: ${ticketViewModel.tickets[index]}")
                    }
                }
            }

        }

//        navController.navigate("${Screens.TicketDetailsScreen.name}/${ticket.id}")

        item {
            Text(
                text = "Билеты церемоний закрытия:",
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            ClosingCeremonyTicketGroup(tickets = closingCeremonyTickets) { ticket ->
                // Обработка клика на билет
                navController.navigate("${Screens.TicketDetailsScreen.name}/${ticket.id}")
            }
        }
    }
}


@Composable
fun OpeningCeremonyTicketGroup(tickets: List<Ticket>, onItemClick: (Ticket) -> Unit) {
    TicketGroup(title = "Церемония открытия", tickets = tickets, onItemClick = onItemClick)
}

@Composable
fun ClosingCeremonyTicketGroup(tickets: List<Ticket>, onItemClick: (Ticket) -> Unit) {
    TicketGroup(title = "Церемония закрытия", tickets = tickets, onItemClick = onItemClick)
}

@Composable
fun TicketGroup(title: String, tickets: List<Ticket>, onItemClick: (Ticket) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        tickets.forEach { ticket ->
            TicketItem(ticket = ticket, onItemClick = { onItemClick(ticket) })
        }
    }
}


@Composable
fun TicketItem(ticket: Ticket, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemClick() }, // Обработка клика на билет
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Билет: ${ticket.title}", fontWeight = FontWeight.Bold)
            Text(text = "Тип: ${ticket.type}")
            Text(text = "Место: ${ticket.seatLocation}")

        }
    }
}




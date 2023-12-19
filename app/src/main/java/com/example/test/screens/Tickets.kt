package com.example.test.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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


class TicketViewModel : ViewModel() {

    private val _tickets = mutableStateListOf<Ticket>()
    val tickets: List<Ticket> get() = _tickets

    fun addTicket(ticket: Ticket) {
        _tickets.add(ticket)
        Log.d("TicketViewModel", "Ticket added: $ticket")
    }
}

data class Ticket(
    val id: Int,
    val title: String,
    val type: String
    // Добавьте другие свойства билета, если необходимо
)


@Composable
fun TicketsScreen(navController: NavController, ticketViewModel: TicketViewModel) {
    val tickets by ticketViewModel.tickets.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Список билетов",
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    // Навигация на экран создания билета
                    navController.navigate(Screens.CreateTicketScreen.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Создать новый билет")
            }
        }

        items(tickets) { ticket ->
            Log.d("TicketsScreen", "Displaying ticket: $ticket")
            TicketItem(ticket = ticket)
        }
    }
}


@Composable
fun TicketGroup(title: String, tickets: List<Ticket>) {
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
            TicketItem(ticket = ticket)
        }
    }
}


@Composable
fun TicketItem(ticket: Ticket) {
    // Реализация отображения информации о билете и навигации на страницу сведений о билете
    // при клике на билет
}


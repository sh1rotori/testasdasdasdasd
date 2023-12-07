package com.example.test.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.test.navigation.Screens
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.AllPermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController, events: List<Event>) {

    // Загружаем данные из JSON-файла
    val allEvents: List<Event> by remember { mutableStateOf(loadEvents()) }
    var filteredEvents by remember { mutableStateOf(allEvents) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // Текущий выбранный фильтр
    var filter by remember { mutableStateOf(Filter.ALL) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilterButtons(onFilterSelected = {
            filter = it
            filteredEvents = filterEvents(allEvents, filter)
        })

        Text(
            text = "Events List",
            modifier = Modifier.padding(vertical = 20.dp),
            fontSize = 32.sp
        )

        // EventsScreen composable
        LazyColumn {
            items(filteredEvents) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    onClick = {
                        selectedEvent = event
                        // Navigate to EventDetailsScreen
                        navController.navigate(Screens.EventDetailsScreen.name + "/${event.id}")
                    }
                ) {
                    EventCard(event)
                }
            }
        }

    }

    // Показать экран сведений о событии, если событие выбрано
    selectedEvent?.let { event ->
        EventDetailsScreen(navController = navController, eventId = event.id, events = filteredEvents)
    }


}


@Composable
fun FilterButtons(onFilterSelected: (Filter) -> Unit) {
    val selectedFilter by remember { mutableStateOf(Filter.ALL) }

    // Верхние кнопки фильтра
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilterButton("Все", Filter.ALL, selectedFilter, onFilterSelected)
        FilterButton("Непрочитанные", Filter.UNREAD, selectedFilter, onFilterSelected)
        FilterButton("Прочитанные", Filter.READ, selectedFilter, onFilterSelected)
    }
}

@Composable
fun FilterButton(
    text: String,
    filter: Filter,
    selectedFilter: Filter,
    onFilterSelected: (Filter) -> Unit
) {
    Button(
        onClick = { onFilterSelected(filter) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (filter == selectedFilter) Color.Gray else Color.White
        )
    ) {
        Text(text = text)
    }
}

enum class Filter {
    ALL,
    UNREAD,
    READ
}

// Updated filterEvents function
fun filterEvents(events: List<Event>, filter: Filter): List<Event> {
    return when (filter) {
        Filter.ALL -> events
        Filter.UNREAD -> events.filter { !it.read }
        Filter.READ -> events.filter { it.read }
    }
}


@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = event.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Описание события с ограничением в две строки
            Text(
                text = event.description,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Use Coil to load images
            val painter = rememberImagePainter(
                data = event.images.firstOrNull(),
                builder = {
                    crossfade(true)
                }
            )

            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
            )

            // Handle loading and error states
            when {
                painter.state is AsyncImagePainter.State.Loading -> {
                    // You can show a loading indicator here if needed
                }
                painter.state is AsyncImagePainter.State.Error -> {
                    // You can show an error placeholder here if needed
                }
            }

            // Статус элемента
            Text(
                text = "Статус: ${if (event.read) "Прочитано" else "Непрочитано"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// Модель данных для события
@Serializable
data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val images: List<String>,  // Заменим поле image на images, которое теперь будет списком строк
    var read: Boolean = false,
    var views: Int = 0
)




// Функция для загрузки данных из JSON-файла
fun loadEvents(): List<Event> {
    val jsonString = """
        [
          {
            "id": 1,
            "title": "Культовая GTA снова возвращается.",
            "description": "Студия Rockstar Games представила следующую часть серии Grand Theft Auto. Трейлер GTA VI выложили на YouTube-канал разработчиков.",
            "images":  [
      "https://virtus-img.cdnvideo.ru/images/material-card/plain/c5/c573f42d-0078-4048-9239-20cbaf6e8073.webp",
      "https://virtus-img.cdnvideo.ru/images/material-card/plain/c5/c573f42d-0078-4048-9239-20cbaf6e8073.webp",
      "https://virtus-img.cdnvideo.ru/images/material-card/plain/c5/c573f42d-0078-4048-9239-20cbaf6e8073.webp"
    ]
          },
          {
            "id": 2,
            "title": "Событие 2",
            "description": "Описание события 2",
            "images":
            [
      "https://habrastorage.org/webt/61/85/0f/61850f212acd6264540097.png",
      "https://virtus-img.cdnvideo.ru/images/material-card/plain/c5/c573f42d-0078-4048-9239-20cbaf6e8073.webp",
      "https://virtus-img.cdnvideo.ru/images/material-card/plain/c5/c573f42d-0078-4048-9239-20cbaf6e8073.webp"
    ]
          },
          {
            "id": 3,
            "title": "Событие 3",
            "description": "Описание события 3",
            "images": [
      "https://habrastorage.org/webt/61/85/0f/61850f212acd6264540097.png",
      "https://virtus-img.cdnvideo.ru/images/material-card/plain/c5/c573f42d-0078-4048-9239-20cbaf6e8073.webp",
      "https://virtus-img.cdnvideo.ru/images/material-card/plain/c5/c573f42d-0078-4048-9239-20cbaf6e8073.webp"
    ]
          }
        ]
    """.trimIndent()

    return Json.decodeFromString<List<Event>>(jsonString)
}

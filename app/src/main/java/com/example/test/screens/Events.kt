package com.example.test.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.serialization.json.Json
import java.security.AllPermission

@Composable
fun EventsScreen() {
    // Загружаем данные из JSON-файла
    val events: List<Event> by remember { mutableStateOf(loadEvents()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Events List",
            modifier = Modifier.padding(vertical = 20.dp),
            fontSize = 32.sp
        )

        // Отображаем карточки событий
        LazyColumn {
            items(events) { event ->
                EventCard(event)
            }
        }
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
            Text(text = event.description, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            // Загружаем изображение
            AsyncImage(
                model = event.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
            )

        }
    }
}

// Модель данных для события
data class Event(
    val title: String,
    val description: String,
    val image: String
)

// Функция для загрузки данных из JSON-файла
fun loadEvents(): List<Event> {
    val jsonString = """
        [
          {
            "title": "Событие 1",
            "description": "Описание события 1",
            "image": "https://coil-kt.github.io/coil/logo.svg"
          },
          {
            "title": "Событие 2",
            "description": "Описание события 2",
            "image": "https://coil-kt.github.io/coil/logo.svg"
          },
          {
            "title": "Событие 3",
            "description": "Описание события 3",
            "image": "https://coil-kt.github.io/coil/logo.svg"
          }
        ]
    """.trimIndent()

    return Json.decodeFromString<List<Event>>(jsonString)
}


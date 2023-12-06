package com.example.test.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import kotlinx.serialization.Serializable
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
                data = event.image,
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
        }
    }
}

// Модель данных для события
@Serializable
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
            "title": "Культовая GTA снова возвращается.",
            "description": "Студия Rockstar Games представила следующую часть серии Grand Theft Auto. Трейлер GTA VI выложили на YouTube-канал разработчиков.",
            "image": "https://virtus-img.cdnvideo.ru/images/material-card/plain/c5/c573f42d-0078-4048-9239-20cbaf6e8073.webp"
          },
          {
            "title": "Событие 2",
            "description": "Описание события 2",
            "image": "https://habrastorage.org/webt/61/85/0f/61850f212acd6264540097.png"
          },
          {
            "title": "Событие 3",
            "description": "Описание события 3",
            "image": "https://habrastorage.org/webt/61/85/0f/61850f212acd6264540097.png"
          }
        ]
    """.trimIndent()

    return Json.decodeFromString<List<Event>>(jsonString)
}


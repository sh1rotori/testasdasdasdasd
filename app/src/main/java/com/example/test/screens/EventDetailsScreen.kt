package com.example.test.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter


@Composable
fun EventDetailsScreen(navController: NavController, eventId: Int, events: List<Event>) {
    // Найдите событие по идентификатору
    val event = events.find { it.id == eventId } ?: return

    event.views++

    // Пометьте событие как прочитанное
    event.read = true

    DisposableEffect(Unit) {
        onDispose {
            // Сохраните измененное событие в исходный список
            events.find { it.id == eventId }?.views = event.views
        }
    }
    // Отобразите информацию о событии
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Название события
        Text(text = event.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Строка из трех изображений
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            event.images.take(3).forEach { imageUrl ->
                Image(
                    painter = rememberImagePainter(
                        data = imageUrl,
                        builder = {
                            crossfade(true)
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(4.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Текст с подробным описанием события
        Text(text = event.description, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Количество просмотров
        Text(text = "Количество просмотров: ${event.views}", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Статус элемента
        Text(
            text = "Статус: ${if (event.read) "Прочитано" else "Непрочитано"}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        // Кнопка для возврата к списку событий
        Button(
            onClick = {
                // Вернуться к предыдущему экрану
                navController.navigateUp()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Назад")
        }
    }
}


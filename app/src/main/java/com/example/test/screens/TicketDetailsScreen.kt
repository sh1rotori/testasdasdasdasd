package com.example.test.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.fonts.FontFamily
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch


@Composable
fun TicketDetailsScreen(ticket: Ticket, onSaveImageClick: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Outermost container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // "Загрузить" button
        Button(
            onClick = {
                coroutineScope.launch {

                }
            },
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text("Загрузить")
        }

        // Box with ticket details and black border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black)
        ) {
            // Content of the ticket details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(16.dp)
            ) {
                // Тип билета
                Text(
                    text = "Тип билета: ${ticket.type.name}",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Имя посетителя
                Text(
                    text = "Имя посетителя: ${ticket.title}",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Время
                Text(
                    text = "Время: ${
                        SimpleDateFormat(
                            "yyyy-MM-dd HH:mm",
                            Locale.getDefault()
                        ).format(Date())
                    }",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Место
                Text(
                    text = "Место: ${ticket.seatLocation}",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Card with the image
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clip(shapes.medium)
                        .background(colorScheme.surface)
                        .clickable {
                            // Handle image click if needed
                        },
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    // Фотография билета
                    Image(
                        painter = rememberAsyncImagePainter(ticket.imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
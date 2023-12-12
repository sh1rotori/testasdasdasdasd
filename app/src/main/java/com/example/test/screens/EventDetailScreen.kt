package com.example.test.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import kotlin.math.roundToInt

@Composable
fun EventDetailsScreen(event: Event?, onClick: () -> Unit) {



// Log the eventId to check if it's correct
    Log.d("EventDetailsScreen", "EventId: ${event?.eventId}")



    if (event == null) {
        // Можно отобразить какое-то уведомление об ошибке или вернуться назад
        Text("Ошибка загрузки данных")
        return
    }

    var isFullImageDialogVisible by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }

    var viewsCount by remember { mutableStateOf(0) }
    var largeImageOffset by remember { mutableStateOf(0f) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
            .padding(16.dp)
    ) {
        // Название страницы
        Text(

            text = "Подробности о событии",
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Название события
        Text(
            text = event.eventTitle,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        // Группа миниатюр событий (3 картинки)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(event.eventPictures) { picture ->
                ThumbnailImage(
                    imageUrl = "file:///android_asset/images/$picture",
                    onClick = {
                        selectedImage = picture
                        isFullImageDialogVisible = true
                    }
                )
            }
        }

        // Текст с подробной информацией о событии
        Text(
            text = event.eventText,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Количество просмотров
        Text(
            text = "Количество просмотров: $viewsCount",
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Область изображения большего размера
        Box(

        ) {
            val density = LocalDensity.current.density
            val thumbWidthDp = 64.dp
            val thumbSpacingDp = 8.dp
            val thumbWidthPx = with(LocalDensity.current) { thumbWidthDp.toPx() }
            val thumbSpacingPx = with(LocalDensity.current) { thumbSpacingDp.toPx() }



            Image(
                painter = rememberAsyncImagePainter(model = event.eventPictures.first()),
                contentDescription = "Large Image",
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        val offset =
                            (largeImageOffset * (thumbWidthPx + thumbSpacingPx)).roundToInt()
                        IntOffset(-offset, 0)
                    }
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            // Добавьте логику для отображения полного изображения в виде диалога
            if (isFullImageDialogVisible) {
                FullImageDialog(
                    imageUrl = selectedImage,
                    onDismiss = {
                        isFullImageDialogVisible = false
                        selectedImage = null
                    }
                )
            }
        }
    }
}

@Composable
fun FullImageDialog(imageUrl: String?, onDismiss: () -> Unit) {
    if (imageUrl == null) {
        onDismiss()
        return
    }
    Log.d("FullImageDialog", "ImageUrl: $imageUrl")

    val painter = rememberImagePainter(
        data = "file:///android_asset/images/$imageUrl",
        builder = {
            crossfade(true) // Эта опция добавляет crossfade-эффект
        }
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Добавьте здесь любые другие элементы UI, такие как кнопка закрытия и т. д.
        }
    }
}
@Composable
fun ThumbnailImage(imageUrl: String, onClick: () -> Unit) {
    Image(
        painter = rememberAsyncImagePainter(model = imageUrl),
        contentDescription = null,
        modifier = Modifier
            .size(90.dp)
            .padding(end = 8.dp)
            .clickable { onClick.invoke() }
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop
    )
}

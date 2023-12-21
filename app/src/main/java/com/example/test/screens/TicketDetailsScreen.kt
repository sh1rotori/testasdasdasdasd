package com.example.test.screens

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.fonts.FontFamily
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import java.io.OutputStream


@Composable
fun TicketDetailsScreen(ticket: Ticket, onSaveImageClick: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Permission launcher for WRITE_EXTERNAL_STORAGE
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                saveTicketDetailsAsImage(context) { imageUri ->
                    // Callback when the image is saved
                    capturedImageUri = imageUri
                }
            } else {
                // Handle the case when permission is not granted
            }
        }

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
                    // Check and request permission to write to external storage
                    if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        saveTicketDetailsAsImage(context) { imageUri ->
                            // Callback when the image is saved
                            capturedImageUri = imageUri
                        }
                    } else {
                        requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
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
                    text = "Тип билета: ${ticket.title}",
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

private fun saveTicketDetailsAsImage(context: Context, onSaveImageClick: (Uri) -> Unit) {
    // Get the root view
    val rootView = (context as Activity).window.decorView.findViewById<View>(android.R.id.content)

    // Create a Bitmap of the Compose content with RGBA_F16 configuration
    val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    rootView.draw(canvas)

    // Save the Bitmap to the gallery
    val imageUri = saveBitmapToGallery(context, bitmap, "ticket_details")

    // Notify the caller about the saved image URI

}

private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String): Uri? {
    // Define the image metadata
    val imageDisplayName = "$displayName.png"
    val imageDescription = "Ticket Details Image"
    val imageMimeType = "image/png"
    val imageRelativePath = Environment.DIRECTORY_PICTURES

    // Prepare the values for the MediaStore
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, imageDisplayName)
        put(MediaStore.Images.Media.DESCRIPTION, imageDescription)
        put(MediaStore.Images.Media.MIME_TYPE, imageMimeType)
        put(MediaStore.Images.Media.RELATIVE_PATH, imageRelativePath)
    }

    // Use ContentResolver to insert the image into the MediaStore
    val contentResolver = context.contentResolver
    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    // Use OutputStream to write the bitmap data into the MediaStore entry
    uri?.let {
        contentResolver.openOutputStream(it)?.use { outputStream: OutputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    }

    return uri
}
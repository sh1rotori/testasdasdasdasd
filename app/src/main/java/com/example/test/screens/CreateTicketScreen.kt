package com.example.test.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.RadioGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun CreateTicketScreen(navController: NavController, ticketViewModel: TicketViewModel) {
    var selectedEventType by remember { mutableStateOf("Церемония открытия") }
    var userName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Создаем лаунчер для получения результатов из галереи
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Создание билета",
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Функция для открытия галереи
        fun openGallery(pickImageLauncher: ActivityResultLauncher<Intent>) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Поле ввода имени
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Введите свое имя") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Кнопка выбора изображения из галереи
        Button(
            onClick = {
                openGallery(pickImageLauncher)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Выбрать из галереи")
        }

        // Область предварительного просмотра
        if (selectedImageUri != null) {
            Image(
                painter = rememberImagePainter(selectedImageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
            )
        }
        @Composable
        fun RadioGroup(
            options: List<String>,
            selectedOption: String,
            onOptionSelected: (String) -> Unit
        ) {
            var selected by remember { mutableStateOf(selectedOption) }

            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == selected),
                            onClick = {
                                selected = option
                                onOptionSelected(option)
                            }
                        )
                        .padding(vertical = 4.dp)
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selected),
                        onClick = {
                            selected = option
                            onOptionSelected(option)
                        }
                    )
                    Text(text = option)
                }
            }
        }
        RadioGroup(
            options = listOf("Церемония открытия", "Церемония закрытия"),
            selectedOption = selectedEventType,
            onOptionSelected = { selectedEventType = it }
        )

        // Кнопка создания билета
        Button(
            onClick = {
                // Создание билета
                val newTicket = Ticket(
                    id = Random.nextInt(),
                    title = "Билет для $userName",
                    type = selectedEventType,
                    // Другие свойства билета
                )
                // Добавление билета в список через ViewModel
                ticketViewModel.addTicket(newTicket)

                // Переход на предыдущий экран
                navController.popBackStack()
                Log.d("CreateTicketScreen", "Navigating back after creating ticket: $newTicket")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Создать")
        }
    }
}
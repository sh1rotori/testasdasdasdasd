package com.example.test.screens

import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.IOException

@Composable
fun RecordsScreen() {
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var recordings by remember { mutableStateOf(listOf<String>()) }
    var outputFileName by remember { mutableStateOf<String?>(null) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val context = LocalContext.current // Получение контекста


    var recorder: MediaRecorder? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Records",
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    if (!isRecording) {
                        try {
                            // Начать запись
                            recorder = MediaRecorder()
                            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                            recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                            outputFileName =
                                "${context.externalCacheDir?.absolutePath}/recording.3gp"
                            recorder?.setOutputFile(outputFileName)

                            recorder?.prepare()
                            recorder?.start()

                            isRecording = true
                        } catch (e: IOException) {
                            // Обработка ошибок
                            e.printStackTrace()
                        }
                    } else {
                        // Остановить запись
                        recorder?.stop()
                        recorder?.release()
                        recorder = null

                        isRecording = false

                        // Обработка завершения записи
                        outputFileName?.let {
                            recordings = recordings + it
                        }

                        outputFileName = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Иконка изменяется в зависимости от состояния записи
                val icon = if (isRecording) Icons.Filled.Close else Icons.Filled.Phone
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isRecording) "Stop Recording" else "Start Recording")
            }

            Button(
                onClick = {
                    if (!isRecording && !isPlaying) {
                        // Воспроизвести только что записанный звук
                        outputFileName?.let {
                            // Ваш код для воспроизведения аудио
                            isPlaying = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = !isRecording
            ) {
                val icon = if (isPlaying) Icons.Filled.Close else Icons.Filled.PlayArrow
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isPlaying) "Stop Playback" else "Play Voice")
            }

            Button(
                onClick = {
                    if (!isRecording) {
                        // Отправить запись в список аудиозаписей
                        outputFileName?.let {
                            // Ваш код для отправки аудио в список
                            recordings = recordings + it
                            outputFileName = null
                        }
                    }
                },
                enabled = !isRecording, // Кнопка отправки отключена до завершения записи голоса
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Send")
            }

            LazyColumn {
                items(recordings) { recording ->
                    Text(
                        text = recording,
                        modifier = Modifier.clickable {
                            // Ваш код для воспроизведения выбранной аудиозаписи

                            // Остановить воспроизведение предыдущей записи (если есть)
                            mediaPlayer?.stop()
                            mediaPlayer?.release()

                            // Создать новый экземпляр MediaPlayer для воспроизведения выбранной записи
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(recording)
                                prepare()
                                start()

                                // Добавьте слушатель для обработки завершения воспроизведения
                                setOnCompletionListener {
                                    // Очистите ресурсы MediaPlayer после завершения воспроизведения
                                    mediaPlayer?.release()
                                    mediaPlayer = null
                                }
                            }
                        }
                    )
                }
            }

            fun playAudio(audioPath: String) {
                val mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioPath)
                    prepare()
                    start()
                    setOnCompletionListener {
                        // Очистите ресурсы MediaPlayer после завершения воспроизведения
                        release()
                    }
                }
            }
        }
    }
}


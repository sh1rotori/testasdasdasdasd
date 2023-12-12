package com.example.test.screens


import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.io.File

data class AudioRecording(val id: Int, val name: String, val filePath: String)

@Composable
fun RecordsScreen() {
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var recordingPath by remember { mutableStateOf("") }
    var recordings by remember { mutableStateOf(emptyList<AudioRecording>()) }

    var showDialog by remember { mutableStateOf(true) }
    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, you can proceed with audio recording
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    DisposableEffect(context) {
        // Check and request RECORD_AUDIO permission if not granted
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
        onDispose { }
    }
    DisposableEffect(context) {
        // Check and request WRITE_EXTERNAL_STORAGE permission if not granted
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        onDispose { }
    }


    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Успешно отправлено") },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                ) {
                    Text("OK")
                }
            }
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Records",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = {
                    // Toggle recording state
                    isRecording = !isRecording

                    if (isRecording) {
                        // Start recording
                        mediaRecorder = MediaRecorder().apply {
                            setAudioSource(MediaRecorder.AudioSource.MIC)
                            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                            val fileName = "recording_${System.currentTimeMillis()}.3gp"
                            val directory = File(
                                context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                                "MyAudioRecordings"
                            )

                            if (!directory.exists()) {
                                directory.mkdirs()
                            }

                            recordingPath = "${directory.absolutePath}/$fileName"

                            setOutputFile(recordingPath)
                            prepare()
                            start()
                        }
                    } else {
                        // Stop recording
                        mediaRecorder?.apply {
                            stop()
                            release()
                        }
                        mediaRecorder = null

                        if (File(recordingPath).exists()) {
                            Toast.makeText(context, "Recording saved to $recordingPath", Toast.LENGTH_SHORT).show()

                            // После окончания записи, обновим список записей
                            val recording = AudioRecording(
                                id = recordings.size + 1,
                                name = "Recording ${recordings.size + 1}",
                                filePath = recordingPath
                            )
                            recordings = recordings + recording

                            showSuccessDialog = true
                        } else {
                            Toast.makeText(context, "Error saving recording", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isPlaying
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Filled.Close else Icons.Filled.Call,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isRecording) "Stop Recording" else "Start Recording")
            }


            Button(
                onClick = {
                    // Toggle playing state
                    isPlaying = !isPlaying

                    if (isPlaying) {
                        // Start playing
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(recordingPath)
                            prepare()
                            start()
                        }
                    } else {
                        // Stop playing
                        mediaPlayer?.apply {
                            pause()
                            seekTo(0)
                        }
                        mediaPlayer = null
                    }
                },
                enabled = !isRecording
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isPlaying) "Stop Playing" else "Start Playing")
            }

            Button(
                onClick = {
                    // Check if recording is completed before allowing sending
                    if (isRecording) {
                        Toast.makeText(context, "Finish recording before sending", Toast.LENGTH_SHORT).show()
                    } else {
                        // Send the recording
                        val recording = AudioRecording(
                            id = recordings.size + 1,
                            name = "Recording ${recordings.size + 1}",
                            filePath = recordingPath
                        )
                        recordings = recordings + recording

                        // Show success dialog
                        // This should be a Compose Dialog
                        // showDialog = true
                    }
                },
                enabled = !isRecording
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Send")
            }

            LazyColumn {
                items(recordings) { recording ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Play the recording if the file exists
                                if (File(recording.filePath).exists()) {
                                    mediaPlayer = MediaPlayer().apply {
                                        setDataSource(recording.filePath)
                                        prepare()
                                        start()
                                    }
                                } else {
                                    Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recording.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun RecordsScreenPreview() {
    RecordsScreen()
}
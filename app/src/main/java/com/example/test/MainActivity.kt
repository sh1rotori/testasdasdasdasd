package com.example.test

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.test.navigation.AppNavigation
import com.example.test.navigation.Screens
import com.example.test.screens.EventsScreen
import com.example.test.screens.TicketViewModel
import com.example.test.ui.theme.TestTheme

class MainActivity : ComponentActivity() {

    // Запрос разрешения на запись во внешнее хранилище
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Разрешение предоставлено
                showToast("Разрешение получено. Повторите действие.")
            } else {
                // Разрешение не предоставлено
                showToast("Разрешение не предоставлено.")
            }
        }

    // Функция для отображения всплывающего уведомления
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    // Код запроса разрешения
    private val REQUEST_STORAGE_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                // Ваша функция навигации
                AppNavigation()


            }
        }
    }


}
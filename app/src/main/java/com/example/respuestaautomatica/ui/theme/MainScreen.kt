package com.example.respuestaautomatica.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.respuestaautomatica.ViewModel.ConfigViewModel
import com.example.respuestaautomatica.datos.PreferencesManager
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(navController: NavController, viewModel: ConfigViewModel = viewModel()) {
    val context = LocalContext.current
    val preferences = remember { PreferencesManager(context) }

    val savedNumber = preferences.getSavedNumber() ?: "No configurado"
    val savedMessage = preferences.getSavedMessage()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Número guardado: $savedNumber")
        Text("Mensaje de respuesta: $savedMessage")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("config") }) {
            Text("Configurar respuesta automática")
        }
    }
}

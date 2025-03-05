package com.example.respuestaautomatica.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.respuestaautomatica.ViewModel.ConfigViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ConfigScreen(viewModel: ConfigViewModel = viewModel()) {
    var phoneNumber by remember { mutableStateOf("") }
    var autoReplyMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Número de teléfono:")
        TextField(value = phoneNumber, onValueChange = { phoneNumber = it })

        Spacer(modifier = Modifier.height(8.dp))

        Text("Mensaje de respuesta:")
        TextField(value = autoReplyMessage, onValueChange = { autoReplyMessage = it })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.saveConfig(context, phoneNumber, autoReplyMessage)
        }) {
            Text("Guardar configuración")
        }
    }
}

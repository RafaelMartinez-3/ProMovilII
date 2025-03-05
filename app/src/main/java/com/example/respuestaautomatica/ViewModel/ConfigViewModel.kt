package com.example.respuestaautomatica.ViewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.respuestaautomatica.datos.PreferencesManager

class ConfigViewModel : ViewModel() {
    fun saveConfig(context: Context, phoneNumber: String, message: String) {
        PreferencesManager(context).saveConfig(phoneNumber, message)
        Toast.makeText(context, "Configuración guardada", Toast.LENGTH_SHORT).show()
    }
}

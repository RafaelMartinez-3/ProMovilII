package com.example.respuestaautomatica.datos

import android.content.Context

class PreferencesManager(context: Context) {
    private val sharedPrefs = context.getSharedPreferences("AutoReplyPrefs", Context.MODE_PRIVATE)

    fun saveConfig(number: String, message: String) {
        with(sharedPrefs.edit()) {
            putString("savedNumber", number)
            putString("autoReplyMessage", message)
            apply()
        }
    }

    fun getSavedNumber(): String? = sharedPrefs.getString("savedNumber", null)
    fun getSavedMessage(): String = sharedPrefs.getString("autoReplyMessage", "Estoy ocupado.") ?: "Estoy ocupado."
}

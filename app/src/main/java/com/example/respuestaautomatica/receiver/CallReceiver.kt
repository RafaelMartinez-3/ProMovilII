package com.example.respuestaautomatica.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.respuestaautomatica.datos.PreferencesManager

class CallReceiver : BroadcastReceiver() {
    private var lastState: String? = null  // Guardar el estado previo

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state != null) {
                if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    // Solo proceder si el número no es nulo (segunda difusión)
                    if (incomingNumber != null) {
                        processIncomingCall(context, incomingNumber)
                    }
                }
                lastState = state
            }
        }
    }

    private fun processIncomingCall(context: Context, incomingNumber: String) {
        val preferences = PreferencesManager(context)
        val savedNumber = preferences.getSavedNumber()
        val autoReplyMessage = preferences.getSavedMessage()

        if (incomingNumber == savedNumber) {
            sendAutoReply(context, incomingNumber, autoReplyMessage)
        }
    }

    private fun sendAutoReply(context: Context, phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        Toast.makeText(context, "Respuesta automática enviada a $phoneNumber", Toast.LENGTH_SHORT).show()
    }
}

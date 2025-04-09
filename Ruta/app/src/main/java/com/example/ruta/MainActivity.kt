package com.example.ruta

import kotlinx.coroutines.tasks.await
import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.ruta.viewmodel.MapViewModel
import com.example.ruta.viewmodel.MapViewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.copy
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicita permisos
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )

        setContent {
            MapScreen()
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapScreen() {
    val viewModel: MapViewModel = viewModel(factory = MapViewModelFactory())
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val route = viewModel.route.value
    val cameraPositionState = rememberCameraPositionState()

    var destination by remember { mutableStateOf("") }
    var currentLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    // Obtener la ubicación actual
    LaunchedEffect(Unit) {
        val location = fusedLocationClient.lastLocation.await()

        if (location != null) {
            currentLocation = LatLng(location.latitude, location.longitude)
            cameraPositionState.position = CameraPosition.Builder()
                .target(currentLocation)
                .zoom(15f)
                .build()
        } else {
            println("No se pudo obtener la ubicación actual.")
        }
    }



    Column {
        TextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Ingresa dirección destino") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Button(
            onClick = {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocationName(destination, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses.first()
                    val destinationLatLng = LatLng(address.latitude, address.longitude)
                    viewModel.getRoute(
                        "5b3ce3597851110001cf624807517449de8440ad8f419543f4845ad2",
                        currentLocation,
                        destinationLatLng
                    )
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Trazar ruta")
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = currentLocation),
                title = "Tu ubicación"
            )

            if (route.isNotEmpty()) {
                Polyline(points = route)
                Marker(
                    state = MarkerState(position = route.last()),
                    title = "Destino"
                )
            }
        }
    }
}

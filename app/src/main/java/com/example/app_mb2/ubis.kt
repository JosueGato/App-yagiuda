package com.example.app_mb2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ubis : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubis)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap: GoogleMap? ->
            // Verifica que el mapa no sea nulo
            googleMap ?: return@getMapAsync

            // Añade marcadores adicionales
            val location1 = LatLng(-21.426972, -65.722000)
            val markerOptions1 = MarkerOptions().position(location1).title("PTS_TUPIZA_NORTE")
            googleMap.addMarker(markerOptions1)

            // Configuración de la cámara para hacer zoom en la primera ubicación
            val cameraPosition = com.google.android.gms.maps.model.CameraPosition.Builder()
                .target(location1)
                .zoom(12f)
                .build()

            // Mueve la cámara a la posición configurada
            googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }
}
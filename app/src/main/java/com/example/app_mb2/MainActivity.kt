package com.example.app_mb2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


import android.net.Uri


class MainActivity : AppCompatActivity() {

    private lateinit var nombreUsuarioTextView: TextView
    private lateinit var apellidoPUsuarioTextView: TextView
    private lateinit var salirButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var imageView2: ImageView // Agregamos la variable imageView2
    private lateinit var imageView6: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nombreUsuarioTextView = findViewById(R.id.nombre_usuario)
        apellidoPUsuarioTextView = findViewById(R.id.apellidoP_usuario)
        imageView2 = findViewById(R.id.imageView2) // Inicializamos imageView2
        imageView6 = findViewById(R.id.imageView6)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        // Inicializar la referencia al almacenamiento de Firebase
        storageRef = FirebaseStorage.getInstance().reference

        // Inicializar el botón de salir
        salirButton = findViewById(R.id.btnsalir)

        // Configurar el evento de clic para el botón de salir
        salirButton.setOnClickListener {
            // Cerrar sesión
            auth.signOut()

            // Redirigir al usuario a la actividad de inicio de sesión
            redirectToLogin()
        }

        // Configurar evento de clic para imageView2
        imageView2.setOnClickListener {
            // Iniciar la actividad ubis.kt
            val intent = Intent(this, ubis::class.java)
            startActivity(intent)
        }

        val enlaceDrive = "https://drive.google.com/drive/folders/1sHHpk5cQX-OuD7q1Fc0bs5WEQYXQPNry?usp=drive_link"

        imageView6.setOnClickListener {
            abrirEnlace(enlaceDrive)
        }

        // Obtener el ID del usuario actual
        val userId = auth.currentUser?.uid

        // Obtener la referencia a la imagen del usuario en Firebase Storage
        val imageRef = storageRef.child("images/$userId.jpg")

        // Obtener la URL de descarga de la imagen del usuario
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // Cargar la imagen del usuario en el ImageView usando Glide
            Glide.with(this /* context */)
                .load(uri)
                .into(findViewById<ImageView>(R.id.imagen_usuario))
        }.addOnFailureListener { exception ->
            // Manejar errores de obtención de la URL de descarga
            // Por ejemplo, puedes mostrar un mensaje de error o una imagen predeterminada
        }

        // Obtener los datos del usuario desde Firestore
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Obtener los datos del documento y establecer los textos en los TextView
                        val nombre = document.getString("nombre_usuario")
                        val apellidoP = document.getString("apellidoP_usuario")
                        nombreUsuarioTextView.text = nombre
                        apellidoPUsuarioTextView.text = apellidoP
                    } else {
                        // El documento no existe
                        nombreUsuarioTextView.text = "Nombre no encontrado"
                        apellidoPUsuarioTextView.text = "Apellido no encontrado"
                    }
                }
                .addOnFailureListener { exception ->
                    // Error al obtener los datos
                    nombreUsuarioTextView.text = "Error: $exception"
                    apellidoPUsuarioTextView.text = "Error: $exception"
                }
        } else {
            // No se ha iniciado sesión
            nombreUsuarioTextView.text = "Usuario no identificado"
            apellidoPUsuarioTextView.text = "Usuario no identificado"
        }
    }

    // Método para redirigir al usuario a la actividad de inicio de sesión
    private fun redirectToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish() // Cierra MainActivity para que el usuario no pueda volver atrás
    }
    private fun abrirEnlace(enlace: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(enlace))
        startActivity(intent)
    }
}

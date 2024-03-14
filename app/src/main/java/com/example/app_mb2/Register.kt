package com.example.app_mb2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Register : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var fechaNacimientoEditText: TextInputEditText
    private lateinit var clienteSpinner: Spinner
    private lateinit var registroButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var logintextView: TextView
    private lateinit var nombreEditText: TextInputEditText
    private lateinit var apellidoPEditText: TextInputEditText
    private lateinit var apellidoMEditText: TextInputEditText

    private var imageUri: Uri? = null // URI de la imagen seleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        fechaNacimientoEditText = findViewById(R.id.fecha_nacimiento)
        clienteSpinner = findViewById(R.id.spinner_cliente)
        registroButton = findViewById(R.id.btnregistro)
        progressBar = findViewById(R.id.progressbar)
        logintextView = findViewById(R.id.loginnow)
        nombreEditText = findViewById(R.id.nombre_usuario)
        apellidoPEditText = findViewById(R.id.apellidoP_usuario)
        apellidoMEditText = findViewById(R.id.apellidoM_usuario)

        // Configurar el ArrayAdapter para el Spinner
        val clientes = arrayOf("ZTE", "HUAWEI")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clientes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        clienteSpinner.adapter = adapter

        // Configurar el botón de registro
        registroButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val fechaNacimiento = fechaNacimientoEditText.text.toString()
            val cliente = clienteSpinner.selectedItem.toString()
            val nombre = nombreEditText.text.toString()
            val apellidoP = apellidoPEditText.text.toString()
            val apellidoM = apellidoMEditText.text.toString()

            if (email.isEmpty() || password.isEmpty() || fechaNacimiento.isEmpty() || nombre.isEmpty() || apellidoP.isEmpty() || apellidoM.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = ProgressBar.VISIBLE

            // Crear usuario en Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = ProgressBar.GONE

                    if (task.isSuccessful) {
                        Log.d("Register", "createUserWithEmail:success")
                        val user = auth.currentUser

                        // Subir imagen a Firebase Storage
                        uploadImageToFirebaseStorage(user?.uid ?: "")

                        // Guardar datos en Firestore
                        guardarDatosEnBaseDeDatos(email, fechaNacimiento, cliente, nombre, apellidoP, apellidoM)

                        // Navegar a la actividad de login
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w("Register", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Configurar el TextView para iniciar sesión
        logintextView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // Configurar el botón para cargar imagen desde la galería
        val cargarImagenButton: Button = findViewById(R.id.btn_cargar_imagen)
        cargarImagenButton.setOnClickListener {
            openGallery()
        }
    }

    // Método para subir la imagen a Firebase Storage
    private fun uploadImageToFirebaseStorage(userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/$userId.jpg")

        // Comprobar si hay una imagen seleccionada para subir
        imageUri?.let { uri ->
            imagesRef.putFile(uri)
                .addOnSuccessListener {
                    Log.d("Register", "Imagen subida con éxito: ${it.metadata?.path}")
                }
                .addOnFailureListener { e ->
                    Log.e("Register", "Error al subir la imagen", e)
                }
        }
    }

    // Método para guardar los datos en Firestore
    private fun guardarDatosEnBaseDeDatos(email: String, fechaNacimiento: String, cliente: String, nombre: String, apellidoP: String, apellidoM: String) {
        val user = auth.currentUser

        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(user.uid)

            val userData = hashMapOf(
                "email" to email,
                "fecha_nacimiento" to fechaNacimiento,
                "cliente" to cliente,
                "nombre_usuario" to nombre,
                "apellidoP_usuario" to apellidoP,
                "apellidoM_usuario" to apellidoM
            )

            userRef.set(userData)
                .addOnSuccessListener {
                    Log.d("Register", "Datos guardados correctamente en Firestore")
                }
                .addOnFailureListener { e ->
                    Log.w("Register", "Error al guardar datos en Firestore", e)
                }
        } else {
            Log.e("Register", "El usuario actual es nulo")
        }
    }

    // Inicializar el contrato para seleccionar imágenes
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Seleccionar una imagen
            imageUri = it
            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para abrir la galería y seleccionar una imagen
    private fun openGallery() {
        getContent.launch("image/*")
    }
}

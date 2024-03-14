package com.example.app_mb2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var inicioButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var registertextView: TextView

    override fun onStart() {
        super.onStart()

        // Comprobar si el usuario está autenticado (no nulo) y actualizar la interfaz de usuario en consecuencia.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Lógica adicional según el estado del usuario autenticado, por ejemplo, navegar a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Cierra la actividad actual para que el usuario no pueda volver atrás
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inicializar los EditText, el Button y el TextView
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        inicioButton = findViewById(R.id.btnlogin)
        progressBar = findViewById(R.id.progressbar)
        registertextView = findViewById(R.id.registernow)

        // Configurar el evento de clic para el TextView de registro
        registertextView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Configurar el evento de clic para el botón de inicio de sesión
        inicioButton.setOnClickListener {
            // Obtener los valores de los EditText
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validar si los campos están vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {

                // Hacer visible el ProgressBar
                progressBar.visibility = ProgressBar.VISIBLE
                // Iniciar sesión con Firebase Authentication
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        // Ocultar el ProgressBar cuando la operación se complete
                        progressBar.visibility = ProgressBar.GONE
                        if (task.isSuccessful) {
                            // Inicio de sesión exitoso
                            Log.d("Login", "signInWithEmail:success")
                            val user = auth.currentUser

                            // Puedes realizar acciones adicionales aquí según el éxito del inicio de sesión
                            // Por ejemplo, navegar a otra actividad
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()  // Cierra la actividad actual para que el usuario no pueda volver atrás
                        } else {
                            // Inicio de sesión fallido
                            Log.w("Login", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Error en el inicio de sesión. Verifica tus credenciales.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }


}

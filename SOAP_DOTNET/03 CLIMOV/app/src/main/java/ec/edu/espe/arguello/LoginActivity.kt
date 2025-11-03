package ec.edu.espe.arguello

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        val usernameInput = findViewById<TextInputEditText>(R.id.usernameInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)
        
        // Pre-llenar con credenciales
        usernameInput.setText("MONSTER")
        passwordInput.setText("MONSTER9")
        
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Validación quemada en el frontend
            if (username.equals("MONSTER", ignoreCase = true) && password == "MONSTER9") {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

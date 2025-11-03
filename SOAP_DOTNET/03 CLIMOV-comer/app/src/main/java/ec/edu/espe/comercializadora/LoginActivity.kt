package ec.edu.espe.comercializadora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.espe.comercializadora.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        
        // Prellenar los campos
        binding.etUsuario.setText("MONSTER")
        binding.etContrasena.setText("MONSTER9")
        
        binding.btnLogin.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val contrasena = binding.etContrasena.text.toString().trim()
            login(usuario, contrasena)
        }
    }
    
    private fun login(usuario: String, contrasena: String) {
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Iniciando sesión..."
        
        // Credenciales quemadas: MONSTER / MONSTER9
        if (usuario == "MONSTER" && contrasena == "MONSTER9") {
            // Guardar sesión
            val prefs = getSharedPreferences("comercializadora_prefs", MODE_PRIVATE)
            prefs.edit().apply {
                putInt("user_id", 1)
                putString("user_nombre", "MONSTER")
                putBoolean("is_logged_in", true)
                apply()
            }
            
            // Navegar al MainActivity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            binding.btnLogin.isEnabled = true
            binding.btnLogin.text = "Iniciar Sesión"
        }
    }
}

package ec.edu.espe.arguello.ui.cuentas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.models.Cuenta
import ec.edu.espe.arguello.repository.BancoRepository
import kotlinx.coroutines.launch

class CuentaFormActivity : AppCompatActivity() {
    
    private val repository = BancoRepository()
    private var cuentaId: Int = 0
    private var clienteId: Int = 0
    private var isEditMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuenta_form)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        cuentaId = intent.getIntExtra("CUENTA_ID", 0)
        clienteId = intent.getIntExtra("CLIENTE_ID", 0)
        isEditMode = cuentaId != 0
        
        supportActionBar?.title = if (isEditMode) "Editar Cuenta" else "Nueva Cuenta"
        
        val numeroCuentaEditText = findViewById<TextInputEditText>(R.id.numeroCuentaEditText)
        val tipoCuentaGroup = findViewById<MaterialButtonToggleGroup>(R.id.tipoCuentaGroup)
        val saldoEditText = findViewById<TextInputEditText>(R.id.saldoEditText)
        val btnGuardar = findViewById<MaterialButton>(R.id.btnGuardar)
        val btnCancelar = findViewById<MaterialButton>(R.id.btnCancelar)
        
        if (isEditMode) {
            numeroCuentaEditText.setText(intent.getStringExtra("CUENTA_NUMERO"))
            val tipoCuenta = intent.getStringExtra("CUENTA_TIPO")
            if (tipoCuenta == "Ahorros") {
                tipoCuentaGroup.check(R.id.btnAhorros)
            } else {
                tipoCuentaGroup.check(R.id.btnCorriente)
            }
            saldoEditText.setText(intent.getDoubleExtra("CUENTA_SALDO", 0.0).toString())
        } else {
            tipoCuentaGroup.check(R.id.btnAhorros)
        }
        
        btnGuardar.setOnClickListener {
            val numeroCuenta = numeroCuentaEditText.text.toString()
            val tipoCuenta = if (tipoCuentaGroup.checkedButtonId == R.id.btnAhorros) "Ahorros" else "Corriente"
            val saldo = saldoEditText.text.toString().toDoubleOrNull() ?: 0.0
            
            if (numeroCuenta.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val cuenta = Cuenta(
                id = cuentaId,
                clienteBancoId = clienteId,
                numeroCuenta = numeroCuenta,
                saldo = saldo,
                tipoCuenta = tipoCuenta
            )
            
            lifecycleScope.launch {
                try {
                    if (isEditMode) {
                        repository.updateCuenta(cuenta)
                        Toast.makeText(this@CuentaFormActivity, "Cuenta actualizada", Toast.LENGTH_SHORT).show()
                    } else {
                        repository.createCuenta(cuenta)
                        Toast.makeText(this@CuentaFormActivity, "Cuenta creada", Toast.LENGTH_SHORT).show()
                    }
                    setResult(RESULT_OK)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@CuentaFormActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        btnCancelar.setOnClickListener {
            finish()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

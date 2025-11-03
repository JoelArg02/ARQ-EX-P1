package ec.edu.espe.arguello.ui.cuentas

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.models.Movimiento
import ec.edu.espe.arguello.repository.BancoRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OperacionBancariaActivity : AppCompatActivity() {
    
    private val repository = BancoRepository()
    private var cuentaId: Int = 0
    private var saldoActual: Double = 0.0
    private var tipoOperacion: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operacion_bancaria)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        cuentaId = intent.getIntExtra("CUENTA_ID", 0)
        saldoActual = intent.getDoubleExtra("CUENTA_SALDO", 0.0)
        tipoOperacion = intent.getStringExtra("TIPO_OPERACION") ?: "Deposito"
        val numeroCuenta = intent.getStringExtra("CUENTA_NUMERO") ?: ""
        
        supportActionBar?.title = tipoOperacion
        
        val cuentaInfoTextView = findViewById<TextView>(R.id.cuentaInfoTextView)
        val saldoActualTextView = findViewById<TextView>(R.id.saldoActualTextView)
        val nuevoSaldoTextView = findViewById<TextView>(R.id.nuevoSaldoTextView)
        val montoEditText = findViewById<TextInputEditText>(R.id.montoEditText)
        val descripcionEditText = findViewById<TextInputEditText>(R.id.descripcionEditText)
        val btnConfirmar = findViewById<MaterialButton>(R.id.btnConfirmar)
        val btnCancelar = findViewById<MaterialButton>(R.id.btnCancelar)
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "EC"))
        
        cuentaInfoTextView.text = "Cuenta: $numeroCuenta"
        saldoActualTextView.text = "Saldo Actual: ${formatter.format(saldoActual)}"
        
        montoEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val monto = s.toString().toDoubleOrNull() ?: 0.0
                val nuevoSaldo = if (tipoOperacion == "Deposito") {
                    saldoActual + monto
                } else {
                    saldoActual - monto
                }
                nuevoSaldoTextView.text = "Nuevo Saldo: ${formatter.format(nuevoSaldo)}"
                
                if (tipoOperacion == "Retiro" && monto > saldoActual) {
                    nuevoSaldoTextView.setTextColor(getColor(android.R.color.holo_red_dark))
                } else {
                    nuevoSaldoTextView.setTextColor(getColor(android.R.color.holo_green_dark))
                }
            }
        })
        
        btnConfirmar.setOnClickListener {
            val monto = montoEditText.text.toString().toDoubleOrNull() ?: 0.0
            val descripcion = descripcionEditText.text.toString()
            
            if (monto <= 0) {
                Toast.makeText(this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (tipoOperacion == "Retiro" && monto > saldoActual) {
                Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
            
            val movimiento = Movimiento(
                cuentaId = cuentaId,
                tipoMovimiento = tipoOperacion,
                monto = monto,
                fecha = currentDate,
                descripcion = descripcion
            )
            
            lifecycleScope.launch {
                try {
                    repository.createMovimiento(movimiento)
                    Toast.makeText(this@OperacionBancariaActivity, "$tipoOperacion realizado exitosamente", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@OperacionBancariaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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

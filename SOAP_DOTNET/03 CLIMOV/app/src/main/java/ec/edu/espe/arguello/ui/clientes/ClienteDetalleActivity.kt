package ec.edu.espe.arguello.ui.clientes

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.repository.BancoRepository
import ec.edu.espe.arguello.ui.cuentas.CuentasAdapter
import ec.edu.espe.arguello.ui.creditos.CreditosAdapter
import kotlinx.coroutines.launch

class ClienteDetalleActivity : AppCompatActivity() {

    private val repository = BancoRepository()
    private lateinit var cuentasAdapter: CuentasAdapter
    private lateinit var creditosAdapter: CreditosAdapter
    private var clienteId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_detalle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        clienteId = intent.getIntExtra("CLIENTE_ID", 0)
        val nombre = intent.getStringExtra("CLIENTE_NOMBRE") ?: ""
        val cedula = intent.getStringExtra("CLIENTE_CEDULA") ?: ""
        val estadoCivil = intent.getStringExtra("CLIENTE_ESTADO_CIVIL") ?: ""
        val fechaNacimiento = intent.getStringExtra("CLIENTE_FECHA_NACIMIENTO") ?: ""

        supportActionBar?.title = nombre

        findViewById<TextView>(R.id.nombreTextView).text = nombre
        findViewById<TextView>(R.id.cedulaTextView).text = "CI: $cedula"
        findViewById<TextView>(R.id.estadoCivilTextView).text = "Estado Civil: $estadoCivil"
        findViewById<TextView>(R.id.fechaNacimientoTextView).text = "Fecha Nacimiento: ${fechaNacimiento.substringBefore("T")}"

        val cuentasRecyclerView = findViewById<RecyclerView>(R.id.cuentasRecyclerView)
        val creditosRecyclerView = findViewById<RecyclerView>(R.id.creditosRecyclerView)

        cuentasRecyclerView.layoutManager = LinearLayoutManager(this)
        cuentasAdapter = CuentasAdapter { cuenta ->
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Operaciones")
                .setMessage("Seleccione una operación para la cuenta ${cuenta.numeroCuenta}")
                .setPositiveButton("Depositar") { _, _ ->
                    val intent = Intent(this, ec.edu.espe.arguello.ui.cuentas.OperacionBancariaActivity::class.java).apply {
                        putExtra("CUENTA_ID", cuenta.id)
                        putExtra("CUENTA_SALDO", cuenta.saldo)
                        putExtra("CUENTA_NUMERO", cuenta.numeroCuenta)
                        putExtra("TIPO_OPERACION", "Deposito")
                    }
                    startActivity(intent)
                }
                .setNegativeButton("Retirar") { _, _ ->
                    val intent = Intent(this, ec.edu.espe.arguello.ui.cuentas.OperacionBancariaActivity::class.java).apply {
                        putExtra("CUENTA_ID", cuenta.id)
                        putExtra("CUENTA_SALDO", cuenta.saldo)
                        putExtra("CUENTA_NUMERO", cuenta.numeroCuenta)
                        putExtra("TIPO_OPERACION", "Retiro")
                    }
                    startActivity(intent)
                }
                .setNeutralButton("Cancelar", null)
                .show()
        }
        cuentasRecyclerView.adapter = cuentasAdapter

        creditosRecyclerView.layoutManager = LinearLayoutManager(this)
        creditosAdapter = CreditosAdapter { credito ->
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Detalle de Crédito")
                .setMessage("Monto Aprobado: $${credito.montoAprobado}\nCuotas: ${credito.numeroCuotas}\nTasa: ${credito.tasaInteres * 100}%\nFecha: ${credito.fechaAprobacion}\nActivo: ${if (credito.activo) "Sí" else "No"}")
                .setPositiveButton("Cerrar", null)
                .show()
        }
        creditosRecyclerView.adapter = creditosAdapter

        val btnCrearCuenta = findViewById<MaterialButton>(R.id.btnCrearCuenta)
        val btnCrearCredito = findViewById<MaterialButton>(R.id.btnCrearCredito)
        val btnEditarCliente = findViewById<MaterialButton>(R.id.btnEditarCliente)
        val btnEliminarCliente = findViewById<Button>(R.id.btnEliminarCliente)

        btnCrearCuenta.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val cuentasExistentes = repository.getCuentasByClienteId(clienteId)
                    if (cuentasExistentes.isNotEmpty()) {
                        Toast.makeText(this@ClienteDetalleActivity, "El cliente ya tiene una cuenta. Solo se permite una por cliente.", Toast.LENGTH_LONG).show()
                    } else {
                        val intent = Intent(this@ClienteDetalleActivity, ec.edu.espe.arguello.ui.cuentas.CuentaFormActivity::class.java).apply {
                            putExtra("CLIENTE_ID", clienteId)
                        }
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ClienteDetalleActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnCrearCredito.setOnClickListener {
            val intent = Intent(this, ec.edu.espe.arguello.ui.creditos.CreditoFormActivity::class.java).apply {
                putExtra("CLIENTE_ID", clienteId)
            }
            startActivity(intent)
        }

        btnEditarCliente.setOnClickListener {
            val intent = Intent(this, ClienteFormActivity::class.java).apply {
                putExtra("CLIENTE_ID", clienteId)
                putExtra("CLIENTE_CEDULA", cedula)
                putExtra("CLIENTE_NOMBRE", nombre)
                putExtra("CLIENTE_ESTADO_CIVIL", estadoCivil)
                putExtra("CLIENTE_FECHA_NACIMIENTO", fechaNacimiento)
            }
            startActivity(intent)
        }

        btnEliminarCliente.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar Cliente")
                .setMessage("¿Está seguro de eliminar este cliente?")
                .setPositiveButton("Eliminar") { _, _ ->
                    lifecycleScope.launch {
                        try {
                            repository.deleteCliente(clienteId)
                            Toast.makeText(this@ClienteDetalleActivity, "Cliente eliminado", Toast.LENGTH_SHORT).show()
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(this@ClienteDetalleActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        loadClienteData()
    }

    override fun onResume() {
        super.onResume()
        loadClienteData()
    }

    private fun loadClienteData() {
        lifecycleScope.launch {
            try {
                val cuentas = repository.getCuentasByClienteId(clienteId)
                cuentasAdapter.submitList(cuentas)
                val creditos = repository.getCreditosByClienteId(clienteId)
                creditosAdapter.submitList(creditos)
            } catch (e: Exception) {
                Toast.makeText(this@ClienteDetalleActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

package ec.edu.espe.arguello.ui.creditos

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.models.ClienteBanco
import ec.edu.espe.arguello.repository.BancoRepository
import kotlinx.coroutines.launch

class CreditoFormActivity : AppCompatActivity() {

    private val repository = BancoRepository()
    private lateinit var clienteSpinner: Spinner
    private lateinit var resumenText: TextView
    private lateinit var montoEditText: TextInputEditText
    private lateinit var cuotasEditText: TextInputEditText
    private lateinit var btnContinuar: MaterialButton
    private lateinit var btnCancelar: MaterialButton
    private lateinit var formularioContainer: LinearLayout

    private var pasoActual = 1
    private var clienteSeleccionado: ClienteBanco? = null
    private val clientes = mutableListOf<ClienteBanco>()
    private var montoMaximo = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credito_form)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Solicitud de Crédito"

        clienteSpinner = findViewById(R.id.clienteSpinner)
        resumenText = findViewById(R.id.resumenText)
        montoEditText = findViewById(R.id.montoEditText)
        cuotasEditText = findViewById(R.id.cuotasEditText)
        btnContinuar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        formularioContainer = findViewById(R.id.formContainer)

        formularioContainer.visibility = View.GONE
        btnCancelar.setOnClickListener { finish() }

        cargarClientes()

        btnContinuar.setOnClickListener {
            when (pasoActual) {
                1 -> verificarElegibilidad()
                2 -> calcularMontoMaximo()
                3 -> mostrarFormularioCredito()
                4 -> confirmarCredito()
            }
        }
    }

    private fun cargarClientes() {
        lifecycleScope.launch {
            try {
                val lista = repository.getAllClientes()
                clientes.clear()
                clientes.addAll(lista)
                val adapter = ArrayAdapter(
                    this@CreditoFormActivity,
                    android.R.layout.simple_spinner_item,
                    clientes.map { it.nombreCompleto }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                clienteSpinner.adapter = adapter
                clienteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        clienteSeleccionado = clientes[position]
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreditoFormActivity, "Error al cargar clientes", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun verificarElegibilidad() {
        val cliente = clienteSeleccionado ?: return
        lifecycleScope.launch {
            val resultado = repository.verificarElegibilidadCliente(cliente.cedula)
            if (resultado == null) {
                Toast.makeText(this@CreditoFormActivity, "No se pudo verificar la elegibilidad", Toast.LENGTH_LONG).show()
                return@launch
            }

            if (!resultado.esElegible) {
                AlertDialog.Builder(this@CreditoFormActivity)
                    .setTitle("Cliente no elegible")
                    .setMessage(resultado.mensaje)
                    .setPositiveButton("Aceptar", null)
                    .show()
                return@launch
            }

            resumenText.text = "✅ ${cliente.nombreCompleto}\n${resultado.mensaje}"
            pasoActual = 2
            btnContinuar.text = "Calcular monto máximo"
        }
    }

    private fun calcularMontoMaximo() {
        val cliente = clienteSeleccionado ?: return
        lifecycleScope.launch {
            val resultado = repository.calcularMontoMaximoCredito(cliente.cedula)
            if (resultado == null) {
                Toast.makeText(this@CreditoFormActivity, "Error al calcular monto", Toast.LENGTH_LONG).show()
                return@launch
            }

            montoMaximo = resultado.montoMaximo
            resumenText.text = "💰 Monto máximo: $${"%.2f".format(montoMaximo)}\nPromedio depósitos: $${"%.2f".format(resultado.promedioDepositos)}"
            pasoActual = 3
            btnContinuar.text = "Continuar"
        }
    }

    private fun mostrarFormularioCredito() {
        formularioContainer.visibility = View.VISIBLE
        btnContinuar.text = "Solicitar crédito"
        pasoActual = 4
    }

    private fun confirmarCredito() {
        val cliente = clienteSeleccionado ?: return
        val monto = montoEditText.text.toString().toDoubleOrNull() ?: 0.0
        val cuotas = cuotasEditText.text.toString().toIntOrNull() ?: 0

        if (monto > montoMaximo) {
            Toast.makeText(this, "Monto excede el máximo ($${"%.2f".format(montoMaximo)})", Toast.LENGTH_LONG).show()
            return
        }

        if (monto <= 0 || cuotas <= 0) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmar solicitud")
            .setMessage("¿Solicitar crédito de $${"%.2f".format(monto)}?\nCuotas: $cuotas")
            .setPositiveButton("Confirmar") { _, _ -> crearCredito(cliente.cedula, monto, cuotas) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun crearCredito(cedula: String, monto: Double, cuotas: Int) {
        lifecycleScope.launch {
            try {
                repository.createCredito(cedula, monto, cuotas)
                Toast.makeText(this@CreditoFormActivity, "Crédito creado exitosamente", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@CreditoFormActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

package ec.edu.espe.arguello.ui.clientes

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.models.ClienteBanco
import ec.edu.espe.arguello.repository.BancoRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class ClienteFormActivity : AppCompatActivity() {

    private val repository = BancoRepository()
    private var clienteId: Int = 0
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_form)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        clienteId = intent.getIntExtra("CLIENTE_ID", 0)
        isEditMode = clienteId != 0

        supportActionBar?.title = if (isEditMode) "Editar Cliente" else "Nuevo Cliente"

        val cedulaEditText = findViewById<TextInputEditText>(R.id.cedulaEditText)
        val nombreEditText = findViewById<TextInputEditText>(R.id.nombreEditText)
        val estadoCivilGroup = findViewById<MaterialButtonToggleGroup>(R.id.estadoCivilGroup)
        val fechaNacimientoEditText = findViewById<TextInputEditText>(R.id.fechaNacimientoEditText)
        val btnGuardar = findViewById<MaterialButton>(R.id.btnGuardar)
        val btnCancelar = findViewById<MaterialButton>(R.id.btnCancelar)

        fechaNacimientoEditText.isFocusable = false
        fechaNacimientoEditText.isClickable = true
        fechaNacimientoEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    fechaNacimientoEditText.setText(formattedDate)
                },
                year - 18, month, day
            )

            calendar.add(Calendar.YEAR, -18)
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            calendar.add(Calendar.YEAR, -82)
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        if (isEditMode) {
            cedulaEditText.setText(intent.getStringExtra("CLIENTE_CEDULA") ?: "")
            nombreEditText.setText(intent.getStringExtra("CLIENTE_NOMBRE") ?: "")
            val estadoCivil = intent.getStringExtra("CLIENTE_ESTADO_CIVIL")
            when (estadoCivil) {
                "Soltero" -> estadoCivilGroup.check(R.id.btnSoltero)
                "Casado" -> estadoCivilGroup.check(R.id.btnCasado)
                "Divorciado" -> estadoCivilGroup.check(R.id.btnSoltero)
                "Viudo" -> estadoCivilGroup.check(R.id.btnSoltero)
                else -> estadoCivilGroup.check(R.id.btnSoltero)
            }
            fechaNacimientoEditText.setText(intent.getStringExtra("CLIENTE_FECHA_NACIMIENTO")?.substringBefore("T") ?: "")
        } else {
            estadoCivilGroup.check(R.id.btnSoltero)
        }

        btnGuardar.setOnClickListener {
            val cedula = cedulaEditText.text?.toString()?.trim() ?: ""
            val nombre = nombreEditText.text?.toString()?.trim() ?: ""
            val fechaNacimiento = fechaNacimientoEditText.text?.toString()?.trim() ?: ""
            val estadoCivil = when (estadoCivilGroup.checkedButtonId) {
                R.id.btnSoltero -> "Soltero"
                R.id.btnCasado -> "Casado"
                else -> "Soltero"
            }

            Log.d("ClienteForm", "Cedula: '$cedula'")
            Log.d("ClienteForm", "Nombre: '$nombre'")
            Log.d("ClienteForm", "EstadoCivil: '$estadoCivil'")
            Log.d("ClienteForm", "FechaNacimiento: '$fechaNacimiento'")

            if (cedula.isBlank() || nombre.isBlank() || fechaNacimiento.isBlank()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cedula.length != 10) {
                Toast.makeText(this, "Ingrese una cédula válida de 10 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cliente = ClienteBanco(
                id = clienteId,
                cedula = cedula,
                nombreCompleto = nombre,
                estadoCivil = estadoCivil,
                fechaNacimiento = fechaNacimiento,
                tieneCreditoActivo = false
            )

            Log.e("ClienteForm", "Cliente a enviar: $cliente")

            lifecycleScope.launch {
                try {
                    if (isEditMode) {
                        repository.updateCliente(cliente)
                        Toast.makeText(this@ClienteFormActivity, "Cliente actualizado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        repository.createCliente(cliente)
                        Toast.makeText(this@ClienteFormActivity, "Cliente creado correctamente", Toast.LENGTH_SHORT).show()
                    }
                    setResult(RESULT_OK)
                    finish()
                } catch (e: Exception) {
                    Log.e("ClienteForm", "Error al guardar cliente", e)
                    Toast.makeText(this@ClienteFormActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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

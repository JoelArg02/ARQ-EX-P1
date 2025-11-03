package ec.edu.espe.arguello.ui.creditos

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.repository.BancoRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class TablaAmortizacionActivity : AppCompatActivity() {
    
    private val repository = BancoRepository()
    private lateinit var adapter: AmortizacionAdapter
    private var creditoId: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabla_amortizacion)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Tabla de Amortización"
        
        creditoId = intent.getIntExtra("CREDITO_ID", 0)
        val montoAprobado = intent.getDoubleExtra("CREDITO_MONTO", 0.0)
        val numeroCuotas = intent.getIntExtra("CREDITO_CUOTAS", 0)
        val tasaInteres = intent.getDoubleExtra("CREDITO_TASA", 0.16)
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "EC"))
        
        findViewById<TextView>(R.id.montoTotalTextView).text = "Monto Total: ${formatter.format(montoAprobado)}"
        findViewById<TextView>(R.id.tasaTextView).text = "Tasa: ${(tasaInteres * 100).toInt()}%"
        findViewById<TextView>(R.id.cuotasTextView).text = "Cuotas: $numeroCuotas"
        
        val tasaMensual = tasaInteres / 12
        val montoCuota = if (tasaMensual > 0) {
            (montoAprobado * tasaMensual * Math.pow(1 + tasaMensual, numeroCuotas.toDouble())) /
                    (Math.pow(1 + tasaMensual, numeroCuotas.toDouble()) - 1)
        } else {
            montoAprobado / numeroCuotas
        }
        
        findViewById<TextView>(R.id.montoCuotaTextView).text = "Cuota Mensual: ${formatter.format(montoCuota)}"
        
        val recyclerView = findViewById<RecyclerView>(R.id.amortizacionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AmortizacionAdapter()
        recyclerView.adapter = adapter
        
        loadAmortizaciones()
    }
    
    private fun loadAmortizaciones() {
        lifecycleScope.launch {
            try {
                val amortizaciones = repository.getAmortizacionesByCredito(creditoId)
                adapter.submitList(amortizaciones)
            } catch (e: Exception) {
                Toast.makeText(this@TablaAmortizacionActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

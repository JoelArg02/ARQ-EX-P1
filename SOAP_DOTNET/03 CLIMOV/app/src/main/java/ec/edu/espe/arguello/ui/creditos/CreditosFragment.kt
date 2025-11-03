package ec.edu.espe.arguello.ui.creditos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.repository.BancoRepository
import kotlinx.coroutines.launch

class CreditosFragment : Fragment() {
    
    private val repository = BancoRepository()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CreditosAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var emptyTextView: TextView
    private lateinit var addButton: FloatingActionButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_creditos, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.creditosRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        errorTextView = view.findViewById(R.id.errorTextView)
        emptyTextView = view.findViewById(R.id.emptyTextView)
        addButton = view.findViewById(R.id.addButton)
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CreditosAdapter { credito ->
            val options = arrayOf("Ver Tabla de Amortización", "Editar", "Eliminar")
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Crédito")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            val intent = Intent(requireContext(), TablaAmortizacionActivity::class.java).apply {
                                putExtra("CREDITO_ID", credito.id)
                                putExtra("CREDITO_MONTO", credito.montoAprobado)
                                putExtra("CREDITO_CUOTAS", credito.numeroCuotas)
                                putExtra("CREDITO_TASA", credito.tasaInteres)
                            }
                            startActivity(intent)
                        }
                        1 -> {
                            val intent = Intent(requireContext(), CreditoFormActivity::class.java).apply {
                                putExtra("CREDITO_ID", credito.id)
                                putExtra("CLIENTE_ID", credito.clienteBancoId)
                                putExtra("CREDITO_MONTO", credito.montoAprobado)
                                putExtra("CREDITO_CUOTAS", credito.numeroCuotas)
                                putExtra("CREDITO_TASA", credito.tasaInteres)
                                putExtra("CREDITO_FECHA", credito.fechaAprobacion)
                            }
                            startActivity(intent)
                        }
                        2 -> {
                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Eliminar Crédito")
                                .setMessage("¿Está seguro de eliminar este crédito?")
                                .setPositiveButton("Eliminar") { _, _ ->
                                    lifecycleScope.launch {
                                        try {
                                            repository.deleteCredito(credito.id)
                                            Toast.makeText(requireContext(), "Crédito eliminado", Toast.LENGTH_SHORT).show()
                                            loadCreditos()
                                        } catch (e: Exception) {
                                            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                    }
                }
                .show()
        }
        recyclerView.adapter = adapter
        
        addButton.setOnClickListener {
            val intent = Intent(requireContext(), CreditoFormActivity::class.java)
            startActivity(intent)
        }
        
        loadCreditos()
    }
    
    override fun onResume() {
        super.onResume()
        loadCreditos()
    }
    
    private fun loadCreditos() {
        showLoading()
        lifecycleScope.launch {
            try {
                val creditos = repository.getAllCreditos()
                hideLoading()
                
                if (creditos.isEmpty()) {
                    showEmpty()
                } else {
                    showData()
                    adapter.submitList(creditos)
                }
            } catch (e: Exception) {
                hideLoading()
                showError("Error al cargar créditos del servidor:\n${e.message}")
            }
        }
    }
    
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorTextView.visibility = View.GONE
        emptyTextView.visibility = View.GONE
    }
    
    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }
    
    private fun showData() {
        recyclerView.visibility = View.VISIBLE
        errorTextView.visibility = View.GONE
        emptyTextView.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        recyclerView.visibility = View.GONE
        errorTextView.visibility = View.VISIBLE
        emptyTextView.visibility = View.GONE
        errorTextView.text = message
    }
    
    private fun showEmpty() {
        recyclerView.visibility = View.GONE
        errorTextView.visibility = View.GONE
        emptyTextView.visibility = View.VISIBLE
    }
}
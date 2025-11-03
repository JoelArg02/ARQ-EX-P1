package ec.edu.espe.arguello.ui.cuentas

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

class CuentasFragment : Fragment() {
    
    private val repository = BancoRepository()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CuentasAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var emptyTextView: TextView
    private lateinit var addButton: FloatingActionButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_cuentas, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.cuentasRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        errorTextView = view.findViewById(R.id.errorTextView)
        emptyTextView = view.findViewById(R.id.emptyTextView)
        addButton = view.findViewById(R.id.addButton)
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CuentasAdapter { cuenta ->
            val options = arrayOf("Ver Detalles", "Depositar", "Retirar", "Editar", "Eliminar")
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cuenta ${cuenta.numeroCuenta}")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "EC"))
                            Toast.makeText(
                                requireContext(),
                                "Cuenta: ${cuenta.numeroCuenta}\nTipo: ${cuenta.tipoCuenta}\nSaldo: ${formatter.format(cuenta.saldo)}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        1 -> {
                            val intent = Intent(requireContext(), OperacionBancariaActivity::class.java).apply {
                                putExtra("CUENTA_ID", cuenta.id)
                                putExtra("CUENTA_NUMERO", cuenta.numeroCuenta)
                                putExtra("CUENTA_SALDO", cuenta.saldo)
                                putExtra("TIPO_OPERACION", "Deposito")
                            }
                            startActivity(intent)
                        }
                        2 -> {
                            val intent = Intent(requireContext(), OperacionBancariaActivity::class.java).apply {
                                putExtra("CUENTA_ID", cuenta.id)
                                putExtra("CUENTA_NUMERO", cuenta.numeroCuenta)
                                putExtra("CUENTA_SALDO", cuenta.saldo)
                                putExtra("TIPO_OPERACION", "Retiro")
                            }
                            startActivity(intent)
                        }
                        3 -> {
                            val intent = Intent(requireContext(), CuentaFormActivity::class.java).apply {
                                putExtra("CUENTA_ID", cuenta.id)
                                putExtra("CLIENTE_ID", cuenta.clienteBancoId)
                                putExtra("CUENTA_NUMERO", cuenta.numeroCuenta)
                                putExtra("CUENTA_TIPO", cuenta.tipoCuenta)
                                putExtra("CUENTA_SALDO", cuenta.saldo)
                            }
                            startActivity(intent)
                        }
                        4 -> {
                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Eliminar Cuenta")
                                .setMessage("¿Está seguro de eliminar esta cuenta?")
                                .setPositiveButton("Eliminar") { _, _ ->
                                    lifecycleScope.launch {
                                        try {
                                            repository.deleteCuenta(cuenta.id)
                                            Toast.makeText(requireContext(), "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                                            loadCuentas()
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
            val intent = Intent(requireContext(), CuentaFormActivity::class.java)
            startActivity(intent)
        }
        
        loadCuentas()
    }
    
    override fun onResume() {
        super.onResume()
        loadCuentas()
    }
    
    private fun loadCuentas() {
        showLoading()
        lifecycleScope.launch {
            try {
                val cuentas = repository.getAllCuentas()
                hideLoading()
                
                if (cuentas.isEmpty()) {
                    showEmpty()
                } else {
                    showData()
                    adapter.submitList(cuentas)
                }
            } catch (e: Exception) {
                hideLoading()
                showError("Error al cargar cuentas del servidor:\n${e.message}")
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
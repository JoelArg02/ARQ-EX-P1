package ec.edu.espe.arguello.ui.clientes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ec.edu.espe.arguello.R
import ec.edu.espe.arguello.repository.BancoRepository
import kotlinx.coroutines.launch

class ClientesFragment : Fragment() {
    
    private val repository = BancoRepository()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClientesAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var emptyTextView: TextView
    private lateinit var addButton: FloatingActionButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_clientes, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.clientesRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        errorTextView = view.findViewById(R.id.errorTextView)
        emptyTextView = view.findViewById(R.id.emptyTextView)
        addButton = view.findViewById(R.id.addButton)
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ClientesAdapter { cliente ->
            val intent = Intent(requireContext(), ClienteDetalleActivity::class.java)
            intent.putExtra("CLIENTE_ID", cliente.id)
            intent.putExtra("CLIENTE_NOMBRE", cliente.nombreCompleto)
            intent.putExtra("CLIENTE_CEDULA", cliente.cedula)
            intent.putExtra("CLIENTE_ESTADO_CIVIL", cliente.estadoCivil)
            intent.putExtra("CLIENTE_FECHA_NACIMIENTO", cliente.fechaNacimiento)
            intent.putExtra("CLIENTE_TIENE_CREDITO", cliente.tieneCreditoActivo)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        
        addButton.setOnClickListener {
            val intent = Intent(requireContext(), ClienteFormActivity::class.java)
            startActivity(intent)
        }
        
        loadClientes()
    }
    
    override fun onResume() {
        super.onResume()
        loadClientes()
    }
    
    private fun loadClientes() {
        showLoading()
        lifecycleScope.launch {
            try {
                val clientes = repository.getAllClientes()
                hideLoading()
                
                if (clientes.isEmpty()) {
                    showEmpty()
                } else {
                    showData()
                    adapter.submitList(clientes)
                }
            } catch (e: Exception) {
                hideLoading()
                showError("Error al cargar clientes del servidor:\n${e.message}")
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
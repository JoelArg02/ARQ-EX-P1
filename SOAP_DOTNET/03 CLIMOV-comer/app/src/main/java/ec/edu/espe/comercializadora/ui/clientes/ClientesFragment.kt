package ec.edu.espe.comercializadora.ui.clientes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import ec.edu.espe.comercializadora.R
import ec.edu.espe.comercializadora.databinding.FragmentClientesBinding
import ec.edu.espe.comercializadora.models.Cliente
import ec.edu.espe.comercializadora.repository.ClienteRepository
import kotlinx.coroutines.launch

class ClientesFragment : Fragment() {

    private var _binding: FragmentClientesBinding? = null
    private val binding get() = _binding!!
    private val clienteRepository = ClienteRepository()
    private lateinit var clientesAdapter: ClientesAdapter
    private val clientes = mutableListOf<Cliente>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFab()
        loadClientes()
    }

    private fun setupRecyclerView() {
        clientesAdapter = ClientesAdapter(
            clientes = clientes,
            onItemClick = { cliente ->
                showClienteDialog(cliente)
            }
        )
        
        binding.recyclerViewClientes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = clientesAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddCliente.setOnClickListener {
            showClienteDialog(null)
        }
    }

    private fun loadClientes() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val result = clienteRepository.getAllClientes()
                clientes.clear()
                clientes.addAll(result)
                clientesAdapter.notifyDataSetChanged()
                
                binding.progressBar.visibility = View.GONE
                
                if (clientes.isEmpty()) {
                    binding.textViewEmpty.visibility = View.VISIBLE
                } else {
                    binding.textViewEmpty.visibility = View.GONE
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showClienteDialog(cliente: Cliente?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_cliente, null)
        
        val etCedula = dialogView.findViewById<TextInputEditText>(R.id.et_cedula)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.et_nombre)
        val etCorreo = dialogView.findViewById<TextInputEditText>(R.id.et_correo)
        val etTelefono = dialogView.findViewById<TextInputEditText>(R.id.et_telefono)
        val etDireccion = dialogView.findViewById<TextInputEditText>(R.id.et_direccion)
        
        // Si es edición, llenar campos
        cliente?.let {
            etCedula.setText(it.cedula)
            etCedula.isEnabled = false // No se puede editar la cédula
            etNombre.setText(it.nombreCompleto)
            etCorreo.setText(it.correo ?: "")
            etTelefono.setText(it.telefono ?: "")
            etDireccion.setText(it.direccion ?: "")
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle(if (cliente == null) "Nuevo Cliente" else "Editar Cliente")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val cedula = etCedula.text.toString().trim()
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim().takeIf { it.isNotEmpty() }
                val telefono = etTelefono.text.toString().trim().takeIf { it.isNotEmpty() }
                val direccion = etDireccion.text.toString().trim().takeIf { it.isNotEmpty() }
                
                if (cedula.isEmpty() || nombre.isEmpty()) {
                    Toast.makeText(requireContext(), "Cédula y nombre son requeridos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                saveCliente(cedula, nombre, correo, telefono, direccion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveCliente(
        cedula: String,
        nombre: String,
        correo: String?,
        telefono: String?,
        direccion: String?
    ) {
        lifecycleScope.launch {
            try {
                val result = clienteRepository.createCliente(cedula, nombre, correo, telefono, direccion)
                
                if (result != null) {
                    Toast.makeText(requireContext(), "Cliente guardado exitosamente", Toast.LENGTH_SHORT).show()
                    loadClientes()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar cliente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

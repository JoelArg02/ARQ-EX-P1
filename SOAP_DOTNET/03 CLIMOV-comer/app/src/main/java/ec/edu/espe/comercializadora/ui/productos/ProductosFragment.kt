package ec.edu.espe.comercializadora.ui.productos

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
import ec.edu.espe.comercializadora.databinding.FragmentProductosBinding
import ec.edu.espe.comercializadora.models.Electrodomestico
import ec.edu.espe.comercializadora.repository.ElectrodomesticoRepository
import kotlinx.coroutines.launch

class ProductosFragment : Fragment() {

    private var _binding: FragmentProductosBinding? = null
    private val binding get() = _binding!!
    private val productoRepository = ElectrodomesticoRepository()
    private lateinit var productosAdapter: ProductosAdapter
    private val productos = mutableListOf<Electrodomestico>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFab()
        loadProductos()
    }

    private fun setupRecyclerView() {
        productosAdapter = ProductosAdapter(
            productos = productos,
            onItemClick = { producto ->
                showProductoDialog(producto)
            },
            onDeleteClick = { producto ->
                confirmDelete(producto)
            }
        )
        
        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productosAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddProducto.setOnClickListener {
            showProductoDialog(null)
        }
    }

    private fun loadProductos() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val result = productoRepository.getAllElectrodomesticos()
                productos.clear()
                productos.addAll(result)
                productosAdapter.notifyDataSetChanged()
                
                binding.progressBar.visibility = View.GONE
                
                if (productos.isEmpty()) {
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

    private fun showProductoDialog(producto: Electrodomestico?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_producto, null)
        
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.et_nombre)
        val etDescripcion = dialogView.findViewById<TextInputEditText>(R.id.et_descripcion)
        val etMarca = dialogView.findViewById<TextInputEditText>(R.id.et_marca)
        val etPrecio = dialogView.findViewById<TextInputEditText>(R.id.et_precio)
        val cbActivo = dialogView.findViewById<com.google.android.material.checkbox.MaterialCheckBox>(R.id.cb_activo)
        
        producto?.let {
            etNombre.setText(it.nombre)
            etDescripcion.setText(it.descripcion ?: "")
            etMarca.setText(it.marca ?: "")
            etPrecio.setText(it.precioVenta.toString())
            cbActivo.isChecked = it.activo
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle(if (producto == null) "Nuevo Producto" else "Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val descripcion = etDescripcion.text.toString().trim()
                val marca = etMarca.text.toString().trim()
                val precioStr = etPrecio.text.toString().trim()
                val activo = cbActivo.isChecked
                
                if (nombre.isEmpty() || precioStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Nombre y precio son requeridos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                val precio = precioStr.toDoubleOrNull()
                if (precio == null || precio <= 0) {
                    Toast.makeText(requireContext(), "Precio inválido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                if (producto == null) {
                    saveProducto(nombre, descripcion, marca, precio)
                } else {
                    updateProducto(producto.id, nombre, descripcion, marca, precio, activo)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveProducto(nombre: String, descripcion: String, marca: String, precio: Double) {
        lifecycleScope.launch {
            try {
                val result = productoRepository.createElectrodomestico(nombre, descripcion, marca, precio)
                
                if (result != null) {
                    Toast.makeText(requireContext(), "Producto guardado exitosamente", Toast.LENGTH_SHORT).show()
                    loadProductos()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateProducto(id: Int, nombre: String, descripcion: String, marca: String, precio: Double, activo: Boolean) {
        lifecycleScope.launch {
            try {
                val success = productoRepository.updateElectrodomestico(id, nombre, descripcion, marca, precio, activo)
                
                if (success) {
                    Toast.makeText(requireContext(), "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    loadProductos()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun confirmDelete(producto: Electrodomestico) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Producto")
            .setMessage("¿Está seguro de eliminar ${producto.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteProducto(producto.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteProducto(id: Int) {
        lifecycleScope.launch {
            try {
                val success = productoRepository.deleteElectrodomestico(id)
                
                if (success) {
                    Toast.makeText(requireContext(), "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    loadProductos()
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar producto", Toast.LENGTH_SHORT).show()
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

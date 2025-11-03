package ec.edu.espe.comercializadora.ui.facturar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ec.edu.espe.comercializadora.R
import ec.edu.espe.comercializadora.databinding.FragmentFacturarBinding
import ec.edu.espe.comercializadora.models.Cliente
import ec.edu.espe.comercializadora.models.DetalleFacturaRequest
import ec.edu.espe.comercializadora.models.Electrodomestico
import ec.edu.espe.comercializadora.repository.ClienteRepository
import ec.edu.espe.comercializadora.repository.ElectrodomesticoRepository
import ec.edu.espe.comercializadora.repository.FacturaRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class FacturarFragment : Fragment() {

    private var _binding: FragmentFacturarBinding? = null
    private val binding get() = _binding!!
    
    private val clienteRepository = ClienteRepository()
    private val productoRepository = ElectrodomesticoRepository()
    private val facturaRepository = FacturaRepository()
    
    private val productosSeleccionados = mutableListOf<ProductoCarrito>()
    private lateinit var carritoAdapter: CarritoAdapter
    
    private var clienteSeleccionado: Cliente? = null
    private val listaClientes = mutableListOf<Cliente>()
    private var cedulaCliente: String = ""
    private var montoMaximoCredito: Double = 0.0
    private var esElegible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacturarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupButtons()
        cargarClientes()
    }

    private fun cargarClientes() {
        lifecycleScope.launch {
            try {
                val clientes = clienteRepository.getAllClientes()
                listaClientes.clear()
                listaClientes.addAll(clientes)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar clientes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        carritoAdapter = CarritoAdapter(
            productos = productosSeleccionados,
            onCantidadChanged = { actualizarTotal() },
            onRemoveClick = { producto ->
                productosSeleccionados.remove(producto)
                carritoAdapter.notifyDataSetChanged()
                actualizarTotal()
            }
        )
        
        binding.recyclerViewCarrito.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = carritoAdapter
        }
    }

    private fun setupButtons() {
        binding.btnSeleccionarCliente.setOnClickListener {
            mostrarDialogoSeleccionCliente()
        }
        
        binding.btnVerificarCredito.setOnClickListener {
            if (clienteSeleccionado == null) {
                Toast.makeText(requireContext(), "Seleccione un cliente primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verificarElegibilidad(clienteSeleccionado!!.cedula)
        }
        
        binding.btnAgregarProducto.setOnClickListener {
            mostrarDialogoProductos()
        }
        
        // Listener para cambio de tipo de pago
        binding.rgTipoPago.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.rb_efectivo -> {
                    // En efectivo no se requiere verificación
                    binding.btnGenerarFactura.isEnabled = productosSeleccionados.isNotEmpty() && clienteSeleccionado != null
                }
                R.id.rb_credito -> {
                    // En crédito sí se requiere verificación
                    actualizarTotal()
                }
            }
        }
        
        binding.btnGenerarFactura.setOnClickListener {
            generarFactura()
        }
    }

    private fun mostrarDialogoSeleccionCliente() {
        if (listaClientes.isEmpty()) {
            Toast.makeText(requireContext(), "No hay clientes disponibles", Toast.LENGTH_SHORT).show()
            return
        }
        
        val nombreClientes = listaClientes.map { "${it.nombreCompleto} - ${it.cedula}" }.toTypedArray()
        
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar Cliente")
            .setItems(nombreClientes) { _, which ->
                clienteSeleccionado = listaClientes[which]
                cedulaCliente = clienteSeleccionado!!.cedula
                binding.tvClienteSeleccionado.text = "Cliente: ${clienteSeleccionado!!.nombreCompleto}\nCI: ${clienteSeleccionado!!.cedula}"
                binding.btnVerificarCredito.isEnabled = true
                actualizarTotal()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun verificarElegibilidad(cedula: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.cardElegibilidad.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                val resultado = facturaRepository.verificarElegibilidadCredito(cedula)
                
                binding.progressBar.visibility = View.GONE
                
                if (resultado != null) {
                    cedulaCliente = cedula
                    esElegible = resultado.esElegible
                    montoMaximoCredito = resultado.montoMaximoCredito
                    
                    binding.cardElegibilidad.visibility = View.VISIBLE
                    binding.tvElegibilidadTitulo.text = if (resultado.esElegible) {
                        "✓ Cliente Elegible para Crédito"
                    } else {
                        "✗ Cliente NO Elegible"
                    }
                    
                    binding.tvElegibilidadTitulo.setTextColor(
                        if (resultado.esElegible) 
                            requireContext().getColor(R.color.success)
                        else 
                            requireContext().getColor(R.color.error)
                    )
                    
                    binding.tvMensaje.text = resultado.mensaje
                    
                    if (resultado.esElegible) {
                        val formato = NumberFormat.getCurrencyInstance(Locale.US)
                        binding.tvMontoMaximo.text = "Monto máximo: ${formato.format(resultado.montoMaximoCredito)}"
                        binding.tvMontoMaximo.visibility = View.VISIBLE
                    } else {
                        binding.tvMontoMaximo.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al verificar elegibilidad", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarDialogoProductos() {
        lifecycleScope.launch {
            try {
                val productos = productoRepository.getElectrodomesticosActivos()
                
                if (productos.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay productos disponibles", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val nombres = productos.map { "${it.nombre} - ${NumberFormat.getCurrencyInstance(Locale.US).format(it.precioVenta)}" }
                
                AlertDialog.Builder(requireContext())
                    .setTitle("Seleccionar Producto")
                    .setItems(nombres.toTypedArray()) { _, which ->
                        val productoSeleccionado = productos[which]
                        agregarAlCarrito(productoSeleccionado)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar productos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun agregarAlCarrito(producto: Electrodomestico) {
        val existe = productosSeleccionados.find { it.producto.id == producto.id }
        
        if (existe != null) {
            existe.cantidad++
        } else {
            productosSeleccionados.add(ProductoCarrito(producto, 1))
        }
        
        carritoAdapter.notifyDataSetChanged()
        actualizarTotal()
    }

    private fun actualizarTotal() {
        var subtotal = 0.0
        
        productosSeleccionados.forEach {
            subtotal += it.producto.precioVenta * it.cantidad
        }
        
        val iva = subtotal * 0.15
        val total = subtotal + iva
        
        val formato = NumberFormat.getCurrencyInstance(Locale.US)
        binding.tvSubtotal.text = "Subtotal: ${formato.format(subtotal)}"
        binding.tvIva.text = "IVA (15%): ${formato.format(iva)}"
        binding.tvTotal.text = "Total: ${formato.format(total)}"
        
        // Habilitar botón solo si hay productos y cliente seleccionado
        binding.btnGenerarFactura.isEnabled = productosSeleccionados.isNotEmpty() && clienteSeleccionado != null
    }

    private fun generarFactura() {
        if (clienteSeleccionado == null) {
            Toast.makeText(requireContext(), "Primero seleccione un cliente", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (productosSeleccionados.isEmpty()) {
            Toast.makeText(requireContext(), "Agregue productos al carrito", Toast.LENGTH_SHORT).show()
            return
        }
        
        val esCredito = binding.rbCredito.isChecked
        val total = productosSeleccionados.sumOf { it.producto.precioVenta * it.cantidad } * 1.15
        
        if (esCredito) {
            // Validaciones para crédito
            if (!esElegible) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Cliente No Elegible")
                    .setMessage("Este cliente no es elegible para crédito. Cambie a efectivo o verifique otro cliente.")
                    .setPositiveButton("Entendido", null)
                    .show()
                return
            }
            
            if (total > montoMaximoCredito) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Monto Excedido")
                    .setMessage("El total (${NumberFormat.getCurrencyInstance(Locale.US).format(total)}) excede el monto máximo de crédito (${NumberFormat.getCurrencyInstance(Locale.US).format(montoMaximoCredito)})")
                    .setPositiveButton("Entendido", null)
                    .show()
                return
            }
            
            // Cliente elegible y monto dentro del límite
            AlertDialog.Builder(requireContext())
                .setTitle("Generar Factura a Crédito")
                .setMessage("Total: ${NumberFormat.getCurrencyInstance(Locale.US).format(total)}\n\n¿Confirma la factura a crédito?")
                .setPositiveButton("Sí, Generar") { _, _ ->
                    procesarFacturaCredito()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } else {
            // Factura en efectivo - no requiere validación de crédito
            AlertDialog.Builder(requireContext())
                .setTitle("Generar Factura en Efectivo")
                .setMessage("Total: ${NumberFormat.getCurrencyInstance(Locale.US).format(total)}\n\n¿Confirma la factura en efectivo?")
                .setPositiveButton("Sí, Generar") { _, _ ->
                    procesarFacturaEfectivo()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun procesarFacturaCredito() {
        binding.progressBar.visibility = View.VISIBLE
        
        val detalles = productosSeleccionados.map {
            DetalleFacturaRequest(it.producto.id, it.cantidad)
        }
        
        lifecycleScope.launch {
            try {
                // FormaPago: 1 = CreditoDirecto, NumeroCuotas por defecto 6
                val resultado = facturaRepository.crearFacturaConValidacion(
                    clienteId = clienteSeleccionado!!.id,
                    detalles = detalles,
                    formaPago = 1, // CreditoDirecto
                    numeroCuotas = 6
                )
                
                binding.progressBar.visibility = View.GONE
                
                if (resultado != null && resultado.exitoso) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("✓ Factura a Crédito Generada")
                        .setMessage(resultado.mensaje + "\n\nFactura ID: ${resultado.facturaId}\nCrédito ID: ${resultado.creditoId}")
                        .setPositiveButton("Aceptar") { _, _ ->
                            limpiarFormulario()
                        }
                        .show()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage(resultado?.mensaje ?: "Error al generar la factura a crédito")
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun procesarFacturaEfectivo() {
        binding.progressBar.visibility = View.VISIBLE
        
        val detalles = productosSeleccionados.map {
            DetalleFacturaRequest(it.producto.id, it.cantidad)
        }
        
        lifecycleScope.launch {
            try {
                // FormaPago: 0 = Efectivo, NumeroCuotas 0 (no aplica)
                val resultado = facturaRepository.crearFacturaConValidacion(
                    clienteId = clienteSeleccionado!!.id,
                    detalles = detalles,
                    formaPago = 0, // Efectivo
                    numeroCuotas = 0
                )
                
                binding.progressBar.visibility = View.GONE
                
                if (resultado != null && resultado.exitoso) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("✓ Factura en Efectivo Generada")
                        .setMessage("Factura generada exitosamente\n\nFactura ID: ${resultado.facturaId}")
                        .setPositiveButton("Aceptar") { _, _ ->
                            limpiarFormulario()
                        }
                        .show()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage(resultado?.mensaje ?: "Error al generar la factura en efectivo")
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun limpiarFormulario() {
        clienteSeleccionado = null
        binding.tvClienteSeleccionado.text = "Ningún cliente seleccionado"
        binding.btnVerificarCredito.isEnabled = false
        productosSeleccionados.clear()
        carritoAdapter.notifyDataSetChanged()
        binding.cardElegibilidad.visibility = View.GONE
        cedulaCliente = ""
        esElegible = false
        montoMaximoCredito = 0.0
        actualizarTotal()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class ProductoCarrito(
    val producto: Electrodomestico,
    var cantidad: Int
)

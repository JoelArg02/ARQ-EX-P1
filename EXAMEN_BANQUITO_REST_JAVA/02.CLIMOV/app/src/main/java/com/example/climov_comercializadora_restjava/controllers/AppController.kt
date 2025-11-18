package com.example.climov_comercializadora_restjava.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.climov_comercializadora_restjava.models.FacturaResponseDTO
import com.example.climov_comercializadora_restjava.models.ItemCarrito
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import com.example.climov_comercializadora_restjava.services.ApiProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class UiState(
    val isLoggedIn: Boolean = false,
    val usuario: String = "",
    val productos: List<ProductoDTO> = emptyList(),
    val carrito: List<ItemCarrito> = emptyList(),
    val ventas: List<FacturaResponseDTO> = emptyList(),
    val misCompras: List<FacturaResponseDTO> = emptyList(),
    val facturaActual: FacturaResponseDTO? = null,
    val mensaje: String? = null
)

class AppController(
    private val authController: AuthController,
    private val productosController: ProductosController,
    private val carritoController: CarritoController,
    private val ventasController: VentasController,
    private val checkoutController: CheckoutController
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun login(usuario: String, password: String) {
        if (authController.login(usuario, password)) {
            _state.value = _state.value.copy(isLoggedIn = true, usuario = usuario, mensaje = null)
            cargarProductos()
        } else {
            _state.value = _state.value.copy(mensaje = "Usuario o contrasena incorrectos")
        }
    }

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                val productos = productosController.obtenerProductos()
                _state.value = _state.value.copy(productos = productos, mensaje = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(mensaje = "Error al cargar productos: ${e.message}")
            }
        }
    }

    fun agregarAlCarrito(producto: ProductoDTO, cantidad: Int) {
        val error = carritoController.agregar(producto, cantidad)
        _state.value = _state.value.copy(
            carrito = carritoController.obtenerCarrito(),
            mensaje = error ?: "Producto agregado al carrito."
        )
    }

    fun eliminarDelCarrito(idProducto: Int) {
        carritoController.eliminar(idProducto)
        _state.value = _state.value.copy(carrito = carritoController.obtenerCarrito())
    }

    fun vaciarCarrito() {
        carritoController.vaciar()
        _state.value = _state.value.copy(carrito = carritoController.obtenerCarrito())
    }

    fun cargarVentas() {
        viewModelScope.launch {
            try {
                val ventas = ventasController.obtenerVentas()
                _state.value = _state.value.copy(ventas = ventas, mensaje = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(mensaje = "Error al cargar ventas: ${e.message}")
            }
        }
    }

    fun cargarMisCompras(cedula: String) {
        viewModelScope.launch {
            try {
                val compras = ventasController.obtenerCompras(cedula)
                _state.value = _state.value.copy(misCompras = compras, mensaje = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(mensaje = "Error al cargar compras: ${e.message}")
            }
        }
    }

    fun cargarFacturaDetalle(id: Int) {
        viewModelScope.launch {
            try {
                val factura = ventasController.obtenerDetalle(id)
                val amort = try {
                    ventasController.obtenerTablaAmortizacion(id)
                } catch (_: Exception) {
                    emptyList()
                }
                val facturaConTabla = factura.copy(
                    infoCredito = factura.infoCredito?.copy(tablaAmortizacion = amort)
                )
                _state.value = _state.value.copy(facturaActual = facturaConTabla, mensaje = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(mensaje = "Error al cargar factura: ${e.message}")
            }
        }
    }

    fun checkout(cedula: String, formaPago: String, cuotas: Int?) {
        if (state.value.carrito.isEmpty()) {
            _state.value = _state.value.copy(mensaje = "El carrito esta vacio.")
            return
        }
        if (cedula.isBlank()) {
            _state.value = _state.value.copy(mensaje = "La cedula es obligatoria.")
            return
        }
        if (formaPago == "CREDITO_DIRECTO") {
            val n = cuotas ?: 0
            if (n < 3 || n > 24) {
                _state.value = _state.value.copy(mensaje = "Numero de cuotas invalido (3-24).")
                return
            }
        }
        viewModelScope.launch {
            val result = checkoutController.crearFactura(cedula, formaPago, cuotas, carritoController.obtenerCarrito())
            result.onSuccess { factura ->
                carritoController.vaciar()
                _state.value = _state.value.copy(
                    facturaActual = factura,
                    carrito = carritoController.obtenerCarrito(),
                    mensaje = "Factura creada con exito."
                )
            }.onFailure {
                _state.value = _state.value.copy(mensaje = it.message ?: "No se pudo crear la factura.")
            }
        }
    }

    val totalCarrito: BigDecimal
        get() = carritoController.total()

    fun limpiarMensaje() {
        _state.value = _state.value.copy(mensaje = null)
    }

    fun mostrarMensaje(mensaje: String) {
        _state.value = _state.value.copy(mensaje = mensaje)
    }

    // ===== Métodos de administración de productos =====
    
    fun crearProducto(producto: ProductoDTO) {
        viewModelScope.launch {
            try {
                productosController.crearProducto(producto)
                _state.value = _state.value.copy(mensaje = "Producto creado exitosamente")
                cargarProductos()
            } catch (e: Exception) {
                _state.value = _state.value.copy(mensaje = "Error al crear producto: ${e.message}")
            }
        }
    }

    fun actualizarProducto(id: Int, producto: ProductoDTO) {
        viewModelScope.launch {
            try {
                productosController.actualizarProducto(id, producto)
                _state.value = _state.value.copy(mensaje = "Producto actualizado exitosamente")
                cargarProductos()
            } catch (e: Exception) {
                _state.value = _state.value.copy(mensaje = "Error al actualizar producto: ${e.message}")
            }
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            try {
                productosController.eliminarProducto(id)
                _state.value = _state.value.copy(mensaje = "Producto eliminado exitosamente")
                cargarProductos()
            } catch (e: Exception) {
                _state.value = _state.value.copy(mensaje = "Error al eliminar producto: ${e.message}")
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val api = ApiProvider.api
                return AppController(
                    AuthController,
                    ProductosController(api),
                    CarritoController(),
                    VentasController(api),
                    CheckoutController(api)
                ) as T
            }
        }
    }
}

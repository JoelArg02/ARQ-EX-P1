package com.example.climov_comercializadora_restjava.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.climov_comercializadora_restjava.components.ProductCardAdmin
import com.example.climov_comercializadora_restjava.controllers.AppController
import com.example.climov_comercializadora_restjava.controllers.UiState
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import kotlinx.coroutines.launch
import java.math.BigDecimal

sealed class AdminScreenMode {
    object List : AdminScreenMode()
    object Create : AdminScreenMode()
    data class Edit(val idProducto: Int, val codigo: String, val nombre: String, val precio: String, val stock: String) : AdminScreenMode()
}

@Composable
fun AdminProductosScreen(
    state: UiState,
    controller: AppController,
    modifier: Modifier = Modifier
) {
    var screenMode by remember { mutableStateOf<AdminScreenMode>(AdminScreenMode.List) }
    var showDeleteDialog by remember { mutableStateOf<ProductoDTO?>(null) }

    LaunchedEffect(Unit) {
        controller.cargarProductos()
    }

    Column(modifier = modifier.fillMaxSize()) {
        when (val mode = screenMode) {
            is AdminScreenMode.List -> {
                AdminListView(
                    productos = state.productos,
                    onAdd = { screenMode = AdminScreenMode.Create },
                    onEdit = { producto ->
                        screenMode = AdminScreenMode.Edit(
                            idProducto = producto.idProducto,
                            codigo = producto.codigo,
                            nombre = producto.nombre,
                            precio = producto.precio.toString(),
                            stock = producto.stock.toString()
                        )
                    },
                    onDelete = { showDeleteDialog = it }
                )
            }
            is AdminScreenMode.Create -> {
                SimpleProductForm(
                    title = "Crear Producto",
                    onSave = { codigo, nombre, precio, stock ->
                        val nuevoProducto = ProductoDTO(
                            idProducto = 0,
                            codigo = codigo,
                            nombre = nombre,
                            precio = precio,
                            stock = stock,
                            imagen = null
                        )
                        controller.crearProducto(nuevoProducto)
                        screenMode = AdminScreenMode.List
                    },
                    onCancel = { screenMode = AdminScreenMode.List }
                )
            }
            is AdminScreenMode.Edit -> {
                SimpleProductForm(
                    title = "Editar Producto",
                    initialCodigo = mode.codigo,
                    initialNombre = mode.nombre,
                    initialPrecio = mode.precio,
                    initialStock = mode.stock,
                    codigoEnabled = false,
                    onSave = { codigo, nombre, precio, stock ->
                        val productoActualizado = ProductoDTO(
                            idProducto = mode.idProducto,
                            codigo = mode.codigo,
                            nombre = nombre,
                            precio = precio,
                            stock = stock,
                            imagen = null
                        )
                        controller.actualizarProducto(mode.idProducto, productoActualizado)
                        screenMode = AdminScreenMode.List
                    },
                    onCancel = { screenMode = AdminScreenMode.List }
                )
            }
        }
    }

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { producto ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Seguro que deseas eliminar el producto '${producto.nombre}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        controller.eliminarProducto(producto.idProducto)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun AdminListView(
    productos: List<ProductoDTO>,
    onAdd: () -> Unit,
    onEdit: (ProductoDTO) -> Unit,
    onDelete: (ProductoDTO) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Administrar Productos",
                style = MaterialTheme.typography.titleLarge
            )
            FloatingActionButton(
                onClick = onAdd,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }

        if (productos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos. Agrega uno nuevo.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productos) { producto ->
                    ProductCardAdmin(
                        producto = producto,
                        onEdit = { onEdit(producto) },
                        onDelete = { onDelete(producto) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleProductForm(
    title: String,
    initialCodigo: String = "",
    initialNombre: String = "",
    initialPrecio: String = "",
    initialStock: String = "",
    codigoEnabled: Boolean = true,
    onSave: (String, String, BigDecimal, Int) -> Unit,
    onCancel: () -> Unit
) {
    var codigo by remember { mutableStateOf(initialCodigo) }
    var nombre by remember { mutableStateOf(initialNombre) }
    var precioText by remember { mutableStateOf(initialPrecio) }
    var stockText by remember { mutableStateOf(initialStock) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código") },
            enabled = codigoEnabled,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = precioText,
            onValueChange = { precioText = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = stockText,
            onValueChange = { stockText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        errorMsg?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    try {
                        if (codigo.isBlank() || nombre.isBlank()) {
                            errorMsg = "Código y nombre son obligatorios"
                            return@Button
                        }
                        
                        val precio = BigDecimal(precioText)
                        val stock = stockText.toInt()
                        
                        onSave(codigo, nombre, precio, stock)
                    } catch (e: Exception) {
                        errorMsg = "Error en los datos: ${e.message}"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
        }
    }
}

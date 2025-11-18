package com.example.climov_comercializadora_restjava.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.climov_comercializadora_restjava.components.ProductCardAdmin
import com.example.climov_comercializadora_restjava.components.ProductForm
import com.example.climov_comercializadora_restjava.controllers.AppController
import com.example.climov_comercializadora_restjava.controllers.UiState
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import kotlinx.coroutines.launch
import java.math.BigDecimal

sealed class AdminScreenMode {
    object List : AdminScreenMode()
    object Create : AdminScreenMode()
    data class Edit(val producto: ProductoDTO) : AdminScreenMode()
}

@Composable
fun AdminProductosScreen(
    state: UiState,
    controller: AppController,
    modifier: Modifier = Modifier
) {
    var screenMode by remember { mutableStateOf<AdminScreenMode>(AdminScreenMode.List) }
    var showDeleteDialog by remember { mutableStateOf<ProductoDTO?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        controller.cargarProductos()
    }

    Column(modifier = modifier.fillMaxSize()) {
        when (val mode = screenMode) {
            is AdminScreenMode.List -> {
                AdminListView(
                    productos = state.productos,
                    onAdd = { screenMode = AdminScreenMode.Create },
                    onEdit = { screenMode = AdminScreenMode.Edit(it) },
                    onDelete = { showDeleteDialog = it },
                    onRefresh = { controller.cargarProductos() }
                )
            }
            is AdminScreenMode.Create -> {
                ProductForm(
                    onSave = { codigo, nombre, precio, stock, imagen ->
                        scope.launch {
                            try {
                                val nuevoProducto = ProductoDTO(
                                    idProducto = 0,
                                    codigo = codigo,
                                    nombre = nombre,
                                    precio = precio,
                                    stock = stock,
                                    imagen = imagen
                                )
                                controller.crearProducto(nuevoProducto)
                                screenMode = AdminScreenMode.List
                            } catch (e: Exception) {
                                controller.mostrarMensaje("Error: ${e.message}")
                            }
                        }
                    },
                    onCancel = { screenMode = AdminScreenMode.List }
                )
            }
            is AdminScreenMode.Edit -> {
                ProductForm(
                    producto = mode.producto,
                    onSave = { codigo, nombre, precio, stock, imagen ->
                        scope.launch {
                            try {
                                val productoActualizado = mode.producto.copy(
                                    nombre = nombre,
                                    precio = precio,
                                    stock = stock,
                                    imagen = imagen
                                )
                                controller.actualizarProducto(mode.producto.idProducto, productoActualizado)
                                screenMode = AdminScreenMode.List
                            } catch (e: Exception) {
                                controller.mostrarMensaje("Error: ${e.message}")
                            }
                        }
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
                        scope.launch {
                            try {
                                controller.eliminarProducto(producto.idProducto)
                                showDeleteDialog = null
                            } catch (e: Exception) {
                                controller.mostrarMensaje("Error: ${e.message}")
                            }
                        }
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
    onDelete: (ProductoDTO) -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
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
                contentAlignment = androidx.compose.ui.Alignment.Center
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

package com.example.climov_comercializadora_restjava.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.climov_comercializadora_restjava.components.ProductCardAdmin
import com.example.climov_comercializadora_restjava.controllers.AppController
import com.example.climov_comercializadora_restjava.controllers.UiState
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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
                    onSave = { codigo, nombre, precio, stock, imagen ->
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
                    onSave = { codigo, nombre, precio, stock, imagen ->
                        val productoActualizado = ProductoDTO(
                            idProducto = mode.idProducto,
                            codigo = mode.codigo,
                            nombre = nombre,
                            precio = precio,
                            stock = stock,
                            imagen = imagen
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
    onSave: (String, String, BigDecimal, Int, String?) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var codigo by remember { mutableStateOf(initialCodigo) }
    var nombre by remember { mutableStateOf(initialNombre) }
    var precioText by remember { mutableStateOf(initialPrecio) }
    var stockText by remember { mutableStateOf(initialStock) }
    var imagenBase64 by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isProcessingImage by remember { mutableStateOf(false) }
    
    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isProcessingImage = true
                errorMsg = null
                try {
                    val base64 = withContext(Dispatchers.IO) {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        compressImageToBase64(inputStream, maxSizeKB = 100)
                    }
                    imagenBase64 = base64
                    if (imagenBase64 == null) {
                        errorMsg = "Error al procesar la imagen"
                    }
                } catch (e: Exception) {
                    errorMsg = "Error: ${e.message}"
                } finally {
                    isProcessingImage = false
                }
            }
        }
    }

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

        // Botón para seleccionar imagen
        OutlinedButton(
            onClick = { imagePickerLauncher.launch("image/*") },
            enabled = !isProcessingImage,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isProcessingImage) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Procesando imagen...")
            } else {
                Text(if (imagenBase64 == null) "📷 Seleccionar Imagen" else "📷 Cambiar Imagen")
            }
        }

        // Indicador de imagen
        imagenBase64?.let { img ->
            Text(
                text = "✓ Imagen seleccionada (${img.length / 1024}KB aprox.)",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }

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
                        
                        onSave(codigo, nombre, precio, stock, imagenBase64)
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

// Función para comprimir imagen y convertir a Base64
private fun compressImageToBase64(inputStream: java.io.InputStream?, maxSizeKB: Int = 100): String? {
    if (inputStream == null) return null
    
    return try {
        // Decodificar la imagen
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        
        // Calcular el factor de escala para reducir el tamaño
        val maxDimension = 800 // Máximo ancho/alto en píxeles
        val scale = minOf(
            maxDimension.toFloat() / originalBitmap.width,
            maxDimension.toFloat() / originalBitmap.height,
            1f
        )
        
        val newWidth = (originalBitmap.width * scale).toInt()
        val newHeight = (originalBitmap.height * scale).toInt()
        
        // Redimensionar
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        
        // Comprimir a JPEG con calidad ajustable
        val outputStream = ByteArrayOutputStream()
        var quality = 85
        
        do {
            outputStream.reset()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5
        } while (outputStream.size() > maxSizeKB * 1024 && quality > 30)
        
        // Convertir a Base64
        val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        
        // Limpiar recursos
        originalBitmap.recycle()
        resizedBitmap.recycle()
        
        base64
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

package com.example.climov_comercializadora_restjava.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigDecimal

@Composable
fun ProductForm(
    producto: ProductoDTO? = null,
    onSave: (codigo: String, nombre: String, precio: BigDecimal, stock: Int, imagen: String?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var codigo by remember { mutableStateOf(producto?.codigo ?: "") }
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var precioText by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var stockText by remember { mutableStateOf(producto?.stock?.toString() ?: "") }
    var imagen by remember { mutableStateOf(producto?.imagen ?: "") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isProcessingImage by remember { mutableStateOf(false) }
    
    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isProcessingImage = true
                try {
                    val base64 = withContext(Dispatchers.IO) {
                        compressImageToBase64(context.contentResolver.openInputStream(uri), maxSizeKB = 100)
                    }
                    imagen = base64 ?: ""
                    if (imagen.isBlank()) {
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
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (producto == null) "Crear Producto" else "Editar Producto",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código") },
            enabled = producto == null, // Solo editable al crear
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
                Text("Procesando...")
            } else {
                Text(if (imagen.isBlank()) "📷 Seleccionar Imagen" else "📷 Cambiar Imagen")
            }
        }

        // Indicador simple sin renderizar la imagen para evitar ANR
        if (!imagen.isNullOrBlank()) {
            Text(
                text = "✓ Imagen configurada (${imagen.length} caracteres - ${imagen.length / 1024}KB aprox.)",
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
                        val precio = BigDecimal(precioText)
                        val stock = stockText.toInt()
                        
                        if (codigo.isBlank() || nombre.isBlank()) {
                            errorMsg = "Código y nombre son obligatorios"
                            return@Button
                        }
                        
                        onSave(codigo, nombre, precio, stock, imagen.ifBlank { null })
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
private fun compressImageToBase64(inputStream: InputStream?, maxSizeKB: Int = 100): String? {
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

package com.example.climov_comercializadora_restjava.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import java.math.BigDecimal

@Composable
fun ProductForm(
    producto: ProductoDTO? = null,
    onSave: (codigo: String, nombre: String, precio: BigDecimal, stock: Int, imagen: String?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var codigo by remember { mutableStateOf(producto?.codigo ?: "") }
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var precioText by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var stockText by remember { mutableStateOf(producto?.stock?.toString() ?: "") }
    var imagen by remember { mutableStateOf(producto?.imagen ?: "") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

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

        OutlinedTextField(
            value = imagen,
            onValueChange = { imagen = it },
            label = { Text("Imagen (Base64 - opcional)") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        if (!imagen.isNullOrBlank()) {
            ProductImage(
                base64Image = imagen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
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

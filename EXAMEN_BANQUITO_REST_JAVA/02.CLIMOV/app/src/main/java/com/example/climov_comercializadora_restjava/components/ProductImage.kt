package com.example.climov_comercializadora_restjava.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProductImage(
    base64Image: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    LaunchedEffect(base64Image) {
        if (!base64Image.isNullOrBlank()) {
            isLoading = true
            hasError = false
            try {
                bitmap = withContext(Dispatchers.IO) {
                    val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                }
                hasError = bitmap == null
            } catch (e: Exception) {
                hasError = true
                bitmap = null
            } finally {
                isLoading = false
            }
        } else {
            bitmap = null
            hasError = false
        }
    }

    Box(
        modifier = modifier.background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Imagen del producto",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = contentScale
                )
            }
            hasError -> {
                Text(
                    text = "❌",
                    fontSize = 48.sp,
                    color = Color.Red.copy(alpha = 0.5f)
                )
            }
            else -> {
                Text(
                    text = "📦",
                    fontSize = 48.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

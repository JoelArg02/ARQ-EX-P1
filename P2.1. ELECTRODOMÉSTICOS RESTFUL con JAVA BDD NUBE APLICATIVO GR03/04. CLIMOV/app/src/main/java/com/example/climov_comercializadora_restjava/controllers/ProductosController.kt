package com.example.climov_comercializadora_restjava.controllers

import com.example.climov_comercializadora_restjava.models.ProductoDTO
import com.example.climov_comercializadora_restjava.services.ComercializadoraApi
import okhttp3.ResponseBody

class ProductosController(private val api: ComercializadoraApi) {

    suspend fun obtenerProductos(): List<ProductoDTO> =
        api.obtenerProductos()

    suspend fun obtenerProductoPorId(id: Int): ProductoDTO =
        api.obtenerProductoPorId(id)

    suspend fun obtenerProductoPorCodigo(codigo: String): ProductoDTO =
        api.obtenerProductoPorCodigo(codigo)

    suspend fun buscarProductosPorNombre(nombre: String): List<ProductoDTO> =
        api.buscarProductosPorNombre(nombre)

    suspend fun contarProductos(): String =
        api.contarProductos().string()

    suspend fun productosBajoStock(min: Int = 5): List<ProductoDTO> =
        api.productosBajoStock(min)

    suspend fun crearProducto(producto: ProductoDTO): ProductoDTO =
        api.crearProducto(producto)

    suspend fun actualizarProducto(id: Int, producto: ProductoDTO): ProductoDTO =
        api.actualizarProducto(id, producto)

    suspend fun actualizarStock(id: Int, nuevoStock: Int): ProductoDTO =
        api.actualizarStock(id, mapOf("stock" to nuevoStock))

    suspend fun actualizarImagen(id: Int, imagenBase64: String): ProductoDTO =
        api.actualizarImagen(id, mapOf("imagen" to imagenBase64))

    suspend fun eliminarProducto(id: Int): String =
        api.eliminarProducto(id).string()
}

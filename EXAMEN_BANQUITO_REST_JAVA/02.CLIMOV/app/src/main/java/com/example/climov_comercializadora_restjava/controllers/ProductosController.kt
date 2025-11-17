package com.example.climov_comercializadora_restjava.controllers

import com.example.climov_comercializadora_restjava.models.ProductoDTO
import com.example.climov_comercializadora_restjava.services.ComercializadoraApi

class ProductosController(private val api: ComercializadoraApi) {
    suspend fun obtenerProductos(): List<ProductoDTO> = api.obtenerProductos()
}

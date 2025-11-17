package com.example.climov_comercializadora_restjava.models

import java.math.BigDecimal

data class ItemCarrito(
    val idProducto: Int,
    val nombre: String,
    val precio: BigDecimal,
    val cantidad: Int,
    val stockDisponible: Int
) {
    val subtotal: BigDecimal get() = precio.multiply(BigDecimal(cantidad))
}

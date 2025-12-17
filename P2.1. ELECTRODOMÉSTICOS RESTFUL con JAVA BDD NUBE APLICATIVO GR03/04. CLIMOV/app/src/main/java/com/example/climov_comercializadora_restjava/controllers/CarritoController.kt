package com.example.climov_comercializadora_restjava.controllers

import com.example.climov_comercializadora_restjava.models.ItemCarrito
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import java.math.BigDecimal

class CarritoController {
    private val carrito: MutableList<ItemCarrito> = mutableListOf()

    fun obtenerCarrito(): List<ItemCarrito> = carrito

    fun agregar(producto: ProductoDTO, cantidad: Int): String? {
        if (cantidad <= 0) return "La cantidad debe ser mayor a cero."

        val existente = carrito.find { it.idProducto == producto.idProducto }
        val cantidadActual = existente?.cantidad ?: 0
        val totalSolicitado = cantidadActual + cantidad
        if (totalSolicitado > producto.stock) {
            return "No se puede agregar. Stock disponible: ${producto.stock}. Ya tienes $cantidadActual en tu factura."
        }
        if (existente != null) {
            val idx = carrito.indexOf(existente)
            carrito[idx] = existente.copy(cantidad = totalSolicitado)
        } else {
            carrito.add(
                ItemCarrito(
                    idProducto = producto.idProducto,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    cantidad = cantidad,
                    stockDisponible = producto.stock
                )
            )
        }
        return null
    }

    fun eliminar(idProducto: Int) {
        carrito.removeIf { it.idProducto == idProducto }
    }

    fun vaciar() {
        carrito.clear()
    }

    fun total(): BigDecimal = carrito.fold(BigDecimal.ZERO) { acc, item -> acc + item.subtotal }
}

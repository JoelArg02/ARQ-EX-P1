package com.example.climov_comercializadora_restjava.controllers

import com.example.climov_comercializadora_restjava.models.FacturaRequest
import com.example.climov_comercializadora_restjava.models.FacturaResponseDTO
import com.example.climov_comercializadora_restjava.models.ItemCarrito
import com.example.climov_comercializadora_restjava.services.ComercializadoraApi

class CheckoutController(private val api: ComercializadoraApi) {

    suspend fun crearFactura(
        cedula: String,
        formaPago: String,
        cuotas: Int?,
        items: List<ItemCarrito>
    ): Result<FacturaResponseDTO> {
        return try {
            val bodyItems = items.map { mapOf("idProducto" to it.idProducto, "cantidad" to it.cantidad) }
            val req = FacturaRequest(cedula, formaPago, cuotas, bodyItems)
            val resp = api.crearFactura(req)
            if (resp.isSuccessful) {
                val factura = resp.body()
                if (factura != null && factura.exitoso) {
                    // cargar tabla amortizacion si aplica
                    val conTabla = if (factura.idFactura != null && "CREDITO_DIRECTO" == factura.formaPago) {
                        val tabla = api.obtenerTablaAmortizacion(factura.idFactura)
                        factura.copy(infoCredito = factura.infoCredito?.copy(tablaAmortizacion = tabla))
                    } else factura
                    Result.success(conTabla)
                } else {
                    Result.failure(IllegalStateException(factura?.mensaje ?: "No se pudo crear la factura"))
                }
            } else {
                Result.failure(IllegalStateException("Error HTTP ${resp.code()} al crear factura"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

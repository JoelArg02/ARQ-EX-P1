package com.example.climov_comercializadora_restjava.controllers

import com.example.climov_comercializadora_restjava.models.FacturaResponseDTO
import com.example.climov_comercializadora_restjava.services.ComercializadoraApi

class VentasController(private val api: ComercializadoraApi) {
    suspend fun obtenerVentas(): List<FacturaResponseDTO> = api.obtenerFacturas()
    suspend fun obtenerCompras(cedula: String): List<FacturaResponseDTO> = api.obtenerFacturasPorCliente(cedula)
    suspend fun obtenerDetalle(idFactura: Int): FacturaResponseDTO = api.obtenerFactura(idFactura)
    suspend fun obtenerTablaAmortizacion(idFactura: Int): List<FacturaResponseDTO.CuotaAmortizacionDTO> =
        api.obtenerTablaAmortizacion(idFactura)
}

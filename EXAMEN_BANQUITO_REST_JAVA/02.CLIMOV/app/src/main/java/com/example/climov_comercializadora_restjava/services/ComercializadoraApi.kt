package com.example.climov_comercializadora_restjava.services

import com.example.climov_comercializadora_restjava.models.FacturaRequest
import com.example.climov_comercializadora_restjava.models.FacturaResponseDTO
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ComercializadoraApi {
    @GET("productos")
    suspend fun obtenerProductos(): List<ProductoDTO>

    @POST("facturas")
    suspend fun crearFactura(@Body request: FacturaRequest): Response<FacturaResponseDTO>

    @GET("facturas")
    suspend fun obtenerFacturas(): List<FacturaResponseDTO>

    @GET("facturas/cliente/{cedula}")
    suspend fun obtenerFacturasPorCliente(@Path("cedula") cedula: String): List<FacturaResponseDTO>

    @GET("facturas/{idFactura}")
    suspend fun obtenerFactura(@Path("idFactura") idFactura: Int): FacturaResponseDTO

    @GET("facturas/{idFactura}/amortizacion")
    suspend fun obtenerTablaAmortizacion(@Path("idFactura") idFactura: Int): List<FacturaResponseDTO.CuotaAmortizacionDTO>
}

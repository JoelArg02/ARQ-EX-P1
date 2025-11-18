package com.example.climov_comercializadora_restjava.services

import com.example.climov_comercializadora_restjava.models.FacturaRequest
import com.example.climov_comercializadora_restjava.models.FacturaResponseDTO
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ComercializadoraApi {

    @GET("productos")
    suspend fun obtenerProductos(): List<ProductoDTO>

    @GET("productos/{id}")
    suspend fun obtenerProductoPorId(@Path("id") id: Int): ProductoDTO

    @GET("productos/codigo/{codigo}")
    suspend fun obtenerProductoPorCodigo(@Path("codigo") codigo: String): ProductoDTO

    @GET("productos/buscar")
    suspend fun buscarProductosPorNombre(@Query("nombre") nombre: String): List<ProductoDTO>

    @GET("productos/count")
    suspend fun contarProductos(): ResponseBody

    @GET("productos/low-stock")
    suspend fun productosBajoStock(@Query("min") min: Int = 5): List<ProductoDTO>

    @POST("productos")
    suspend fun crearProducto(@Body producto: ProductoDTO): ProductoDTO

    @PUT("productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Int,
        @Body producto: ProductoDTO
    ): ProductoDTO

    @PATCH("productos/{id}/stock")
    suspend fun actualizarStock(
        @Path("id") id: Int,
        @Body body: Map<String, Int>
    ): ProductoDTO

    @PATCH("productos/{id}/imagen")
    suspend fun actualizarImagen(
        @Path("id") id: Int,
        @Body body: Map<String, String>
    ): ProductoDTO

    @DELETE("productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Int): ResponseBody

    @POST("facturas")
    suspend fun crearFactura(@Body request: FacturaRequest): Response<FacturaResponseDTO>

    @GET("facturas")
    suspend fun obtenerFacturas(): List<FacturaResponseDTO>

    @GET("facturas/cliente/{cedula}")
    suspend fun obtenerFacturasPorCliente(@Path("cedula") cedula: String): List<FacturaResponseDTO>

    @GET("facturas/{idFactura}")
    suspend fun obtenerFactura(@Path("idFactura") idFactura: Int): FacturaResponseDTO

    @GET("facturas/{idFactura}/amortizacion")
    suspend fun obtenerTablaAmortizacion(
        @Path("idFactura") idFactura: Int
    ): List<FacturaResponseDTO.CuotaAmortizacionDTO>
}

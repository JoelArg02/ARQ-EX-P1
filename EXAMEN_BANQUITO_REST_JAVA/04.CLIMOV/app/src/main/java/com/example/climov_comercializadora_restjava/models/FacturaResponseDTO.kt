package com.example.climov_comercializadora_restjava.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class FacturaResponseDTO(
    @SerializedName("exitoso") val exitoso: Boolean = false,
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("idFactura") val idFactura: Int? = null,
    @SerializedName("cedulaCliente") val cedulaCliente: String? = null,
    @SerializedName("nombreCliente") val nombreCliente: String? = null,
    @SerializedName("fecha") val fecha: String? = null,
    @SerializedName("total") val total: BigDecimal? = null,
    @SerializedName("formaPago") val formaPago: String? = null,
    @SerializedName("idCreditoBanco") val idCreditoBanco: Int? = null,
    @SerializedName("detalles") val detalles: List<DetalleFacturaDTO>? = null,
    @SerializedName("infoCredito") val infoCredito: InfoCreditoDTO? = null
) {
    data class DetalleFacturaDTO(
        @SerializedName("codigoProducto") val codigoProducto: String? = null,
        @SerializedName("nombreProducto") val nombreProducto: String? = null,
        @SerializedName("cantidad") val cantidad: Int = 0,
        @SerializedName("precioUnitario") val precioUnitario: BigDecimal = BigDecimal.ZERO,
        @SerializedName("subtotal") val subtotal: BigDecimal = BigDecimal.ZERO
    )

    data class InfoCreditoDTO(
        @SerializedName("idCredito") val idCredito: Int? = null,
        @SerializedName("montoCredito") val montoCredito: BigDecimal? = null,
        @SerializedName("numeroCuotas") val numeroCuotas: Int? = null,
        @SerializedName("valorCuota") val valorCuota: BigDecimal? = null,
        @SerializedName("tasaInteres") val tasaInteres: BigDecimal? = null,
        @SerializedName("estado") val estado: String? = null,
        @SerializedName("tablaAmortizacion") val tablaAmortizacion: List<CuotaAmortizacionDTO>? = null
    )

    data class CuotaAmortizacionDTO(
        @SerializedName("numeroCuota") val numeroCuota: Int = 0,
        @SerializedName("valorCuota") val valorCuota: BigDecimal = BigDecimal.ZERO,
        @SerializedName("interesPagado") val interesPagado: BigDecimal = BigDecimal.ZERO,
        @SerializedName("capitalPagado") val capitalPagado: BigDecimal = BigDecimal.ZERO,
        @SerializedName("saldoRestante") val saldoRestante: BigDecimal = BigDecimal.ZERO
    )
}

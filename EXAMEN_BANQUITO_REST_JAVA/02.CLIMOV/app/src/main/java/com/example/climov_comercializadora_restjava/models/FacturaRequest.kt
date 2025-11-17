package com.example.climov_comercializadora_restjava.models

import com.google.gson.annotations.SerializedName

data class FacturaRequest(
    @SerializedName("cedulaCliente") val cedulaCliente: String,
    @SerializedName("formaPago") val formaPago: String,
    @SerializedName("numeroCuotas") val numeroCuotas: Int?,
    @SerializedName("items") val items: List<Map<String, Int>>
)

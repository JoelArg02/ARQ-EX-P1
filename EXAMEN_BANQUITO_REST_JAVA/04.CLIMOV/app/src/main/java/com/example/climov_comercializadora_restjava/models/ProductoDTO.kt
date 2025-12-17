package com.example.climov_comercializadora_restjava.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ProductoDTO(
    @SerializedName("idProducto") val idProducto: Int,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("precio") val precio: BigDecimal,
    @SerializedName("stock") val stock: Int,
    @SerializedName("imagen") val imagen: String?
)

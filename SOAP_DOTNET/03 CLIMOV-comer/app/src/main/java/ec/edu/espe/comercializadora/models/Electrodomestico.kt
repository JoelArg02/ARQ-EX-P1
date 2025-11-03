package ec.edu.espe.comercializadora.models

data class Electrodomestico(
    val id: Int = 0,
    val nombre: String = "",
    val descripcion: String? = null,
    val marca: String? = null,
    val precioVenta: Double = 0.0,
    val activo: Boolean = true,
    val fechaRegistro: String = ""
)

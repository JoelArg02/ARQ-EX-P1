package ec.edu.espe.comercializadora.models

data class Cliente(
    val id: Int = 0,
    val cedula: String = "",
    val nombreCompleto: String = "",
    val correo: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val fechaRegistro: String = ""
)

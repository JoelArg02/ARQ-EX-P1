package ec.edu.espe.arguello.models

data class ClienteBanco(
    val id: Int = 0,
    val cedula: String = "",
    val nombreCompleto: String = "",
    val estadoCivil: String = "",
    val fechaNacimiento: String = "",
    val tieneCreditoActivo: Boolean = false
)

enum class EstadoCivil {
    Soltero,
    Casado,
    Divorciado,
    Viudo
}

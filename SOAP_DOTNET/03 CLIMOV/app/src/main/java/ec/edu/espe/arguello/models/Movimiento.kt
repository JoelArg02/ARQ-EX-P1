package ec.edu.espe.arguello.models

data class Movimiento(
    val id: Int = 0,
    val cuentaId: Int = 0,
    val tipoMovimiento: String = "",
    val monto: Double = 0.0,
    val fecha: String = "",
    val descripcion: String = ""
)

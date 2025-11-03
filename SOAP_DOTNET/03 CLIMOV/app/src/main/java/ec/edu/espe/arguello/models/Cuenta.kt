package ec.edu.espe.arguello.models

data class Cuenta(
    val id: Int = 0,
    val clienteBancoId: Int = 0,
    val numeroCuenta: String = "",
    val saldo: Double = 0.0,
    val tipoCuenta: String = "Ahorros"
)

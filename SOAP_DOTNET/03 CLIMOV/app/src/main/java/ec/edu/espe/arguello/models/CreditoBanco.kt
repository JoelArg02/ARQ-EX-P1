package ec.edu.espe.arguello.models

data class CreditoBanco(
    val id: Int = 0,
    val clienteBancoId: Int = 0,
    val montoAprobado: Double = 0.0,
    val numeroCuotas: Int = 0,
    val tasaInteres: Double = 0.16,
    val fechaAprobacion: String = "",
    val activo: Boolean = true
)

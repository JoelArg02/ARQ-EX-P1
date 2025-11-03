package ec.edu.espe.arguello.models

data class CuotaAmortizacion(
    val numeroCuota: Int = 0,
    val montoCuota: Double = 0.0,
    val capital: Double = 0.0,
    val interes: Double = 0.0,
    val saldoPendiente: Double = 0.0,
    val pagada: Boolean = false
)

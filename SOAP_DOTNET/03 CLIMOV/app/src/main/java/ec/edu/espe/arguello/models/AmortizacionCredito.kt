package ec.edu.espe.arguello.models

data class AmortizacionCredito(
    val id: Int = 0,
    val creditoBancoId: Int = 0,
    val numeroCuota: Int = 0,
    val montoCuota: Double = 0.0,
    val capital: Double = 0.0,
    val interes: Double = 0.0,
    val saldoPendiente: Double = 0.0,
    val fechaVencimiento: String = "",
    val pagada: Boolean = false
)

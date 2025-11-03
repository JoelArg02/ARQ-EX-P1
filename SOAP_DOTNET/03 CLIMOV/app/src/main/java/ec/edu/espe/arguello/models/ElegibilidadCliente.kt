package ec.edu.espe.arguello.models

data class ElegibilidadCliente(
    val esCliente: Boolean,
    val cumpleEdadEstadoCivil: Boolean,
    val tieneDepositoUltimoMes: Boolean,
    val noTieneCreditoActivo: Boolean,
    val esElegible: Boolean,
    val mensaje: String
)

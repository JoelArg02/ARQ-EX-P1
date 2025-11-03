package ec.edu.espe.comercializadora.models

data class Factura(
    val id: Int = 0,
    val clienteId: Int = 0,
    val nombreCliente: String = "",
    val cedulaCliente: String = "",
    val fechaEmision: String = "",
    val subtotal: Double = 0.0,
    val iva: Double = 0.0,
    val total: Double = 0.0,
    val formaPago: String = "Efectivo", // "Efectivo" o "Crédito"
    val cantidadProductos: Int = 0
)

data class DetalleFactura(
    val id: Int = 0,
    val facturaId: Int = 0,
    val electrodomesticoId: Int = 0,
    val cantidad: Int = 1,
    val precioUnitario: Double = 0.0,
    val subtotal: Double = 0.0,
    val electrodomestico: Electrodomestico? = null
)

data class ElegibilidadCredito(
    val esElegible: Boolean = false,
    val mensaje: String = "",
    val montoMaximoCredito: Double = 0.0
)

data class FacturaRequest(
    val cedulaCliente: String = "",
    val detalles: List<DetalleFacturaRequest> = emptyList()
)

data class DetalleFacturaRequest(
    val electrodomesticoId: Int = 0,
    val cantidad: Int = 1
)

data class FacturaResponse(
    val exitoso: Boolean = false,
    val mensaje: String = "",
    val facturaId: Int? = null,
    val creditoId: Int? = null,
    val factura: Factura? = null
)

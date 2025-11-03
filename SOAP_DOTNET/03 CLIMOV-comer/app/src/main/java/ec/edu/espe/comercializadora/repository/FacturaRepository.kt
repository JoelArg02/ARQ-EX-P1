package ec.edu.espe.comercializadora.repository

import android.util.Log
import ec.edu.espe.comercializadora.models.*
import ec.edu.espe.comercializadora.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class FacturaRepository {
    private val soapService = RetrofitClient.soapService

    suspend fun verificarElegibilidadCredito(cedula: String): ElegibilidadCredito? = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
   <soapenv:Header/>
   <soapenv:Body>
      <tem:VerificarElegibilidadCredito>
         <tem:cedula>$cedula</tem:cedula>
      </tem:VerificarElegibilidadCredito>
   </soapenv:Body>
</soapenv:Envelope>
""".trimIndent()

            val response = soapService.verificarElegibilidadCredito(soapEnvelope)

            if (response.isSuccessful) {
                val responseBody = response.body()?.string().orEmpty()
                Log.d("FacturaRepository", "VerificarElegibilidadCredito response: $responseBody")
                parseElegibilidadFromXml(responseBody)
            } else {
                Log.e("FacturaRepository", "VerificarElegibilidadCredito error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("FacturaRepository", "VerificarElegibilidadCredito exception", e)
            null
        }
    }

    suspend fun crearFacturaConValidacion(
        clienteId: Int,
        detalles: List<DetalleFacturaRequest>,
        formaPago: Int, // 0 = Efectivo, 1 = CreditoDirecto
        numeroCuotas: Int = 0
    ): FacturaResponse? = withContext(Dispatchers.IO) {
        try {
            // Construir XML de detalles
            val detallesXml = detalles.joinToString("\n") { detalle ->
                """
               <api:DetalleFacturaDto>
                  <api:Cantidad>${detalle.cantidad}</api:Cantidad>
                  <api:ElectrodomesticoId>${detalle.electrodomesticoId}</api:ElectrodomesticoId>
               </api:DetalleFacturaDto>"""
            }
            
            val soapEnvelope = """
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tem="http://tempuri.org/"
                  xmlns:api="http://schemas.datacontract.org/2004/07/API_Comercializadora.Application.DTOs">
   <soapenv:Header/>
   <soapenv:Body>
      <tem:CrearFacturaConValidacion>
         <tem:request>
            <api:ClienteId>$clienteId</api:ClienteId>
            <api:Detalles>$detallesXml
            </api:Detalles>
            <api:FormaPago>$formaPago</api:FormaPago>
            <api:NumeroCuotas>$numeroCuotas</api:NumeroCuotas>
         </tem:request>
      </tem:CrearFacturaConValidacion>
   </soapenv:Body>
</soapenv:Envelope>
""".trimIndent()

            Log.d("FacturaRepository", "CrearFacturaConValidacion request: $soapEnvelope")
            
            val response = soapService.crearFacturaConValidacion(soapEnvelope)
            if (response.isSuccessful) {
                val xml = response.body()?.string().orEmpty()
                Log.d("FacturaRepository", "CrearFacturaConValidacion response: $xml")
                parseFacturaResponseFromXml(xml)
            } else {
                Log.e("FacturaRepository", "CrearFacturaConValidacion error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("FacturaRepository", "CrearFacturaConValidacion exception", e)
            null
        }
    }

    suspend fun getAllFacturas(): List<Factura> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Body>
                        <tem:GetAllFacturas/>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()
            
            val response = soapService.getAllFacturas(soapEnvelope)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string() ?: ""
                Log.d("FacturaRepository", "GetAllFacturas response: $responseBody")
                parseFacturasFromXml(responseBody)
            } else {
                Log.e("FacturaRepository", "GetAllFacturas error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("FacturaRepository", "GetAllFacturas exception", e)
            emptyList()
        }
    }
    
    private fun parseElegibilidadFromXml(xml: String): ElegibilidadCredito? {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var elegibilidad = ElegibilidadCredito()
            var currentTag: String? = null
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            elegibilidad = when (currentTag) {
                                "EsElegible" -> elegibilidad.copy(esElegible = text.toBoolean())
                                "Mensaje" -> elegibilidad.copy(mensaje = text)
                                "MontoMaximoCredito" -> elegibilidad.copy(montoMaximoCredito = text.toDoubleOrNull() ?: 0.0)
                                else -> elegibilidad
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
            return elegibilidad
        } catch (e: Exception) {
            Log.e("FacturaRepository", "Error parsing elegibilidad XML", e)
            return null
        }
    }
    
    private fun parseFacturaResponseFromXml(xml: String): FacturaResponse? {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var facturaResponse = FacturaResponse()
            var currentTag: String? = null
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            facturaResponse = when (currentTag) {
                                "Exitoso" -> facturaResponse.copy(exitoso = text.toBoolean())
                                "Mensaje" -> facturaResponse.copy(mensaje = text)
                                "FacturaId" -> facturaResponse.copy(facturaId = text.toIntOrNull())
                                "CreditoId" -> facturaResponse.copy(creditoId = text.toIntOrNull())
                                else -> facturaResponse
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
            return facturaResponse
        } catch (e: Exception) {
            Log.e("FacturaRepository", "Error parsing factura response XML", e)
            return null
        }
    }
    
    private fun parseFacturasFromXml(xml: String): List<Factura> {
        val facturas = mutableListOf<Factura>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var currentFactura: Factura? = null
            var currentTag: String? = null
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "FacturaListDto") {
                            currentFactura = Factura()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty() && currentFactura != null) {
                            currentFactura = when (currentTag) {
                                "Id" -> currentFactura.copy(id = text.toIntOrNull() ?: 0)
                                "ClienteId" -> currentFactura.copy(clienteId = text.toIntOrNull() ?: 0)
                                "NombreCliente" -> currentFactura.copy(nombreCliente = text)
                                "CedulaCliente" -> currentFactura.copy(cedulaCliente = text)
                                "FechaEmision" -> currentFactura.copy(fechaEmision = text)
                                "Subtotal" -> currentFactura.copy(subtotal = text.toDoubleOrNull() ?: 0.0)
                                "Iva" -> currentFactura.copy(iva = text.toDoubleOrNull() ?: 0.0)
                                "Total" -> currentFactura.copy(total = text.toDoubleOrNull() ?: 0.0)
                                "FormaPago" -> currentFactura.copy(formaPago = text)
                                "CantidadProductos" -> currentFactura.copy(cantidadProductos = text.toIntOrNull() ?: 0)
                                else -> currentFactura
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "FacturaListDto" && currentFactura != null) {
                            facturas.add(currentFactura)
                            currentFactura = null
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("FacturaRepository", "Error parsing facturas XML", e)
        }
        return facturas
    }
}

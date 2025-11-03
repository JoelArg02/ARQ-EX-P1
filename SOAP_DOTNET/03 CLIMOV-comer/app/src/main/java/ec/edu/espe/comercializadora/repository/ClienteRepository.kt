package ec.edu.espe.comercializadora.repository

import android.util.Log
import ec.edu.espe.comercializadora.models.Cliente
import ec.edu.espe.comercializadora.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class ClienteRepository {
    private val soapService = RetrofitClient.soapService
    
    suspend fun getAllClientes(): List<Cliente> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Body>
                        <tem:GetAllClientes/>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()
            
            val response = soapService.getAllClientes(soapEnvelope)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string() ?: ""
                Log.d("ClienteRepository", "GetAllClientes response: $responseBody")
                parseClientesFromXml(responseBody)
            } else {
                Log.e("ClienteRepository", "GetAllClientes error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ClienteRepository", "GetAllClientes exception", e)
            emptyList()
        }
    }
    
    suspend fun getClienteByCedula(cedula: String): Cliente? = withContext(Dispatchers.IO) {
        try {
            val allClientes = getAllClientes()
            allClientes.find { it.cedula == cedula }
        } catch (e: Exception) {
            Log.e("ClienteRepository", "GetClienteByCedula exception", e)
            null
        }
    }
    
    suspend fun createCliente(
        cedula: String,
        nombreCompleto: String,
        correo: String? = null,
        telefono: String? = null,
        direccion: String? = null
    ): Cliente? = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Body>
                        <tem:CreateCliente>
                            <tem:cedula>$cedula</tem:cedula>
                            <tem:nombreCompleto>$nombreCompleto</tem:nombreCompleto>
                            ${correo?.let { "<tem:correo>$it</tem:correo>" } ?: ""}
                            ${telefono?.let { "<tem:telefono>$it</tem:telefono>" } ?: ""}
                            ${direccion?.let { "<tem:direccion>$it</tem:direccion>" } ?: ""}
                        </tem:CreateCliente>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()
            
            val response = soapService.createCliente(soapEnvelope)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string() ?: ""
                Log.d("ClienteRepository", "CreateCliente response: $responseBody")
                parseClienteFromXml(responseBody)
            } else {
                Log.e("ClienteRepository", "CreateCliente error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ClienteRepository", "CreateCliente exception", e)
            null
        }
    }
    
    private fun parseClientesFromXml(xml: String): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var currentCliente: Cliente? = null
            var currentTag: String? = null
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "Cliente") {
                            currentCliente = Cliente()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty() && currentCliente != null) {
                            currentCliente = when (currentTag) {
                                "Id" -> currentCliente.copy(id = text.toIntOrNull() ?: 0)
                                "Cedula" -> currentCliente.copy(cedula = text)
                                "NombreCompleto" -> currentCliente.copy(nombreCompleto = text)
                                "Correo" -> currentCliente.copy(correo = text)
                                "Telefono" -> currentCliente.copy(telefono = text)
                                "Direccion" -> currentCliente.copy(direccion = text)
                                "FechaRegistro" -> currentCliente.copy(fechaRegistro = text)
                                else -> currentCliente
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "Cliente" && currentCliente != null) {
                            clientes.add(currentCliente)
                            currentCliente = null
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("ClienteRepository", "Error parsing clientes XML", e)
        }
        return clientes
    }
    
    private fun parseClienteFromXml(xml: String): Cliente? {
        val clientes = parseClientesFromXml(xml)
        return clientes.firstOrNull()
    }
}

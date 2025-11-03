package ec.edu.espe.comercializadora.repository

import android.util.Log
import ec.edu.espe.comercializadora.models.Electrodomestico
import ec.edu.espe.comercializadora.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class ElectrodomesticoRepository {
    private val soapService = RetrofitClient.soapService
    
    suspend fun getAllElectrodomesticos(): List<Electrodomestico> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Body>
                        <tem:GetAllElectrodomesticos/>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()
            
            val response = soapService.getAllElectrodomesticos(soapEnvelope)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string() ?: ""
                Log.d("ElectrodomesticoRepo", "GetAllElectrodomesticos response: $responseBody")
                parseElectrodomesticosFromXml(responseBody)
            } else {
                Log.e("ElectrodomesticoRepo", "GetAllElectrodomesticos error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ElectrodomesticoRepo", "GetAllElectrodomesticos exception", e)
            emptyList()
        }
    }
    
    suspend fun getElectrodomesticosActivos(): List<Electrodomestico> = withContext(Dispatchers.IO) {
        try {
            val all = getAllElectrodomesticos()
            all.filter { it.activo }
        } catch (e: Exception) {
            Log.e("ElectrodomesticoRepo", "GetElectrodomesticosActivos exception", e)
            emptyList()
        }
    }
    
    suspend fun createElectrodomestico(
        nombre: String,
        descripcion: String,
        marca: String,
        precioVenta: Double,
        activo: Boolean = true
    ): Electrodomestico? = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Body>
                        <tem:CreateElectrodomestico>
                            <tem:nombre>$nombre</tem:nombre>
                            <tem:descripcion>$descripcion</tem:descripcion>
                            <tem:marca>$marca</tem:marca>
                            <tem:precioVenta>$precioVenta</tem:precioVenta>
                            <tem:activo>$activo</tem:activo>
                        </tem:CreateElectrodomestico>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()
            
            val response = soapService.createElectrodomestico(soapEnvelope)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string() ?: ""
                Log.d("ElectrodomesticoRepo", "CreateElectrodomestico response: $responseBody")
                parseElectrodomesticoFromXml(responseBody)
            } else {
                Log.e("ElectrodomesticoRepo", "CreateElectrodomestico error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ElectrodomesticoRepo", "CreateElectrodomestico exception", e)
            null
        }
    }
    
    suspend fun updateElectrodomestico(
        id: Int,
        nombre: String,
        descripcion: String,
        marca: String,
        precioVenta: Double,
        activo: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Body>
                        <tem:UpdateElectrodomestico>
                            <tem:id>$id</tem:id>
                            <tem:nombre>$nombre</tem:nombre>
                            <tem:descripcion>$descripcion</tem:descripcion>
                            <tem:marca>$marca</tem:marca>
                            <tem:precioVenta>$precioVenta</tem:precioVenta>
                            <tem:activo>$activo</tem:activo>
                        </tem:UpdateElectrodomestico>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()
            
            val response = soapService.updateElectrodomestico(soapEnvelope)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ElectrodomesticoRepo", "UpdateElectrodomestico exception", e)
            false
        }
    }
    
    suspend fun deleteElectrodomestico(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Body>
                        <tem:DeleteElectrodomestico>
                            <tem:id>$id</tem:id>
                        </tem:DeleteElectrodomestico>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()
            
            val response = soapService.deleteElectrodomestico(soapEnvelope)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ElectrodomesticoRepo", "DeleteElectrodomestico exception", e)
            false
        }
    }
    
    private fun parseElectrodomesticosFromXml(xml: String): List<Electrodomestico> {
        val electrodomesticos = mutableListOf<Electrodomestico>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var currentElectrodomestico: Electrodomestico? = null
            var currentTag: String? = null
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "Electrodomestico") {
                            currentElectrodomestico = Electrodomestico()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty() && currentElectrodomestico != null) {
                            currentElectrodomestico = when (currentTag) {
                                "Id" -> currentElectrodomestico.copy(id = text.toIntOrNull() ?: 0)
                                "Nombre" -> currentElectrodomestico.copy(nombre = text)
                                "Descripcion" -> currentElectrodomestico.copy(descripcion = text)
                                "Marca" -> currentElectrodomestico.copy(marca = text)
                                "PrecioVenta" -> currentElectrodomestico.copy(precioVenta = text.toDoubleOrNull() ?: 0.0)
                                "Activo" -> currentElectrodomestico.copy(activo = text.toBoolean())
                                "FechaRegistro" -> currentElectrodomestico.copy(fechaRegistro = text)
                                else -> currentElectrodomestico
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "Electrodomestico" && currentElectrodomestico != null) {
                            electrodomesticos.add(currentElectrodomestico)
                            currentElectrodomestico = null
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("ElectrodomesticoRepo", "Error parsing electrodomesticos XML", e)
        }
        return electrodomesticos
    }
    
    private fun parseElectrodomesticoFromXml(xml: String): Electrodomestico? {
        val electrodomesticos = parseElectrodomesticosFromXml(xml)
        return electrodomesticos.firstOrNull()
    }
}

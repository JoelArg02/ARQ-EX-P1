package ec.edu.espe.comercializadora.repository

import android.util.Log
import ec.edu.espe.comercializadora.models.User
import ec.edu.espe.comercializadora.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class UserRepository {
    private val soapService = RetrofitClient.soapService
    
    suspend fun login(nombre: String, contrasena: String): User? = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Body>
                        <tem:GetAllUsers/>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()
            
            val response = soapService.getAllUsers(soapEnvelope)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string() ?: ""
                Log.d("UserRepository", "Login response: $responseBody")
                
                val users = parseUsersFromXml(responseBody)
                users.find { it.nombre.equals(nombre, ignoreCase = true) && it.contrasena == contrasena }
            } else {
                Log.e("UserRepository", "Login error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Login exception", e)
            null
        }
    }
    
    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Body>
                        <tem:GetAllUsers/>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()
            
            val response = soapService.getAllUsers(soapEnvelope)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string() ?: ""
                Log.d("UserRepository", "GetAllUsers response: $responseBody")
                parseUsersFromXml(responseBody)
            } else {
                Log.e("UserRepository", "GetAllUsers error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "GetAllUsers exception", e)
            emptyList()
        }
    }
    
    private fun parseUsersFromXml(xml: String): List<User> {
        val users = mutableListOf<User>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var currentUser: User? = null
            var currentTag: String? = null
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "User") {
                            currentUser = User()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty() && currentUser != null) {
                            currentUser = when (currentTag) {
                                "Id" -> currentUser.copy(id = text.toIntOrNull() ?: 0)
                                "Nombre" -> currentUser.copy(nombre = text)
                                "Contrasena" -> currentUser.copy(contrasena = text)
                                else -> currentUser
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "User" && currentUser != null) {
                            users.add(currentUser)
                            currentUser = null
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error parsing users XML", e)
        }
        return users
    }
}

package com.example.climov_comercializadora_restjava.controllers

import com.example.climov_comercializadora_restjava.models.LoginResponseDTO
import com.example.climov_comercializadora_restjava.services.ComercializadoraApi
import com.google.gson.Gson

/**
 * Controlador de autenticación dinámico via REST API.
 * ADMIN: MONSTER / MONSTER9 (puede vender a cualquier cédula)
 * CLIENTES: cédula / abcd1234 (solo pueden comprar para su propia cédula)
 */
class AuthController(private val api: ComercializadoraApi) {

    suspend fun login(usuario: String, password: String): LoginResponseDTO {
        return try {
            val request = mapOf("username" to usuario, "password" to password)
            val response = api.login(request)
            
            if (response.isSuccessful) {
                response.body() ?: LoginResponseDTO(
                    exitoso = false,
                    mensaje = "Respuesta vacía del servidor"
                )
            } else {
                // Intentar parsear el error del body
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    try {
                        Gson().fromJson(errorBody, LoginResponseDTO::class.java)
                    } catch (e: Exception) {
                        LoginResponseDTO(
                            exitoso = false,
                            mensaje = "Error HTTP ${response.code()}"
                        )
                    }
                } else {
                    LoginResponseDTO(
                        exitoso = false,
                        mensaje = "Error HTTP ${response.code()}"
                    )
                }
            }
        } catch (e: Exception) {
            LoginResponseDTO(
                exitoso = false,
                mensaje = "Error de conexión: ${e.message}"
            )
        }
    }
}

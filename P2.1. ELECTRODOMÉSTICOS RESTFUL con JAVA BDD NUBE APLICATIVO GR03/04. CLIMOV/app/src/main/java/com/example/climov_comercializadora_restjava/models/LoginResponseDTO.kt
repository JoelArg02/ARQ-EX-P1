package com.example.climov_comercializadora_restjava.models

/**
 * DTO para respuesta de login desde el servidor
 */
data class LoginResponseDTO(
    val exitoso: Boolean = false,
    val mensaje: String? = null,
    val idUsuario: Int? = null,
    val username: String? = null,
    val rol: String? = null,
    val cedula: String? = null,
    val nombreCliente: String? = null
) {
    fun isAdmin(): Boolean = rol == "ADMIN"
}

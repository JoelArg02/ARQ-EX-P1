package com.example.climov_comercializadora_restjava.controllers

object AuthController {
    private const val USUARIO_VALIDO = "MONSTER"
    private const val PASSWORD_VALIDO = "MONSTER9"

    fun login(usuario: String, password: String): Boolean {
        return usuario == USUARIO_VALIDO && password == PASSWORD_VALIDO
    }
}

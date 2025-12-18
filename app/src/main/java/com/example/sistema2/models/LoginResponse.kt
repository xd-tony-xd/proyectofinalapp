package com.example.sistema2.models

data class LoginResponse(
    val usuario: Usuario,
    val token: String
)
package com.example.sistema2.models

data class RegisterResponse(
    val usuario: Usuario,
    val token: String
)
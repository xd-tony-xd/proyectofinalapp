package com.example.sistema2.models

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val telefono: String? = null,
    val dni: String? = null,
    val direccion: String? = null,
    val ciudad: String? = null,

    @SerializedName("fechaNacimiento")
    val fechaNacimiento: String? = null,

    @SerializedName("fotoPerfil")
    val fotoPerfil: String? = null
)
package com.example.sistema2.models

import com.google.gson.annotations.SerializedName

data class Usuario(
    val id: Long? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val password: String? = null,
    val telefono: String? = null,
    val dni: String? = null,
    val direccion: String? = null,
    val ciudad: String? = null,

    @SerializedName("fechaNacimiento")
    val fechaNacimiento: String? = null,

    @SerializedName("fotoPerfil")
    val fotoPerfil: String? = null,

    val reputacion: Double? = 5.0,

    @SerializedName("fechaRegistro")
    val fechaRegistro: String? = null
)
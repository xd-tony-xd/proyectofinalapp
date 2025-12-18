package com.example.sistema2.models

data class UsuarioUpdateRequest(
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val dni: String? = null,
    val direccion: String? = null,
    val ciudad: String? = null,
    val fechaNacimiento: String? = null,
    val fotoPerfil: String? = null
)

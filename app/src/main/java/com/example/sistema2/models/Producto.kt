    package com.example.sistema2.models

    import com.google.gson.annotations.SerializedName

    data class Producto(
        val id: Long? = null,

        // Solo envío el ID del usuario
        @SerializedName("usuario")
        val usuario: Usuario? = null,

        // Objeto categoría SOLO se usa al recibir datos
        @SerializedName("categoria")
        val categoria: Categoria? = null,

        // ID de la categoría para enviar al backend al crear/actualizar
        @SerializedName("categoriaId")
        val categoriaId: Long? = null,

        val titulo: String? = null,
        val descripcion: String? = null,
        val precio: Double? = null,
        val stock: Int? = 1,

        @SerializedName("imagenUrl")
        val imagenUrl: String? = null,

        val latitud: Double? = null,
        val longitud: Double? = null,
        val direccion: String? = null,
        val estado: String? = "DISPONIBLE",

        @SerializedName("fechaPublicacion")
        val fechaPublicacion: String? = null
    )

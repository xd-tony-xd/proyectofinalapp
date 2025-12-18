package com.example.sistema2.api

import com.example.sistema2.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------------- AUTH ----------------
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/registro")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>


    // ---------------- PRODUCTOS ----------------

    // LISTAR
    @GET("productos")
    suspend fun obtenerProductos(
        @Header("Authorization") token: String
    ): Response<List<Producto>>

    // OBTENER POR ID
    @GET("productos/{id}")
    suspend fun obtenerProductoPorId(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Producto>

    // OBTENER POR USUARIO
    @GET("productos/usuario/{usuarioId}")
    suspend fun obtenerProductosPorUsuario(
        @Header("Authorization") token: String,
        @Path("usuarioId") usuarioId: Long
    ): Response<List<Producto>>

    // ----------- CREAR PRODUCTO (MULTIPART REAL) ----------
    @Multipart
    @POST("productos/crear")
    suspend fun crearProductoMultipart(
        @Header("Authorization") token: String,
        @Part("producto") productoJson: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<Producto>

    // ----------- ACTUALIZAR PRODUCTO (MULTIPART REAL) ----------
    @Multipart
    @PUT("productos/{id}")
    suspend fun actualizarProductoMultipart(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Part("producto") productoJson: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<Producto>


    // ELIMINAR
    @DELETE("productos/{id}")
    suspend fun eliminarProducto(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Void>


    // ---------------- USUARIOS ----------------
    @GET("usuarios/{id}")
    suspend fun obtenerUsuario(
        @Header("Authorization") token: String,
        @Path("id") usuarioId: Long
    ): Response<Usuario>

    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(
        @Header("Authorization") token: String,
        @Path("id") usuarioId: Long,
        @Body body: UsuarioUpdateRequest
    ): Response<Usuario>


    // ---------------- CATEGORIAS ----------------
    @GET("categorias")
    suspend fun obtenerCategorias(
        @Header("Authorization") token: String
    ): Response<List<Categoria>>
}

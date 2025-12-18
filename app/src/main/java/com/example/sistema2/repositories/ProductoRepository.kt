package com.example.sistema2.repositories

import com.example.sistema2.api.RetrofitClient
import com.example.sistema2.models.Producto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductoRepository {

    private val api = RetrofitClient.apiService
    private val gson = Gson()

    // ----------------------------------------------------------
    // LISTAR
    // ----------------------------------------------------------
    suspend fun obtenerProductos(token: String): Result<List<Producto>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerProductos("Bearer $token")
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Error ${response.code()} al obtener productos"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ----------------------------------------------------------
    // OBTENER POR ID
    // ----------------------------------------------------------
    suspend fun obtenerProductoPorId(token: String, productoId: Long): Result<Producto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerProductoPorId("Bearer $token", productoId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()} al obtener producto"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ----------------------------------------------------------
    // CREAR PRODUCTO (MULTIPART)
    // ----------------------------------------------------------
    suspend fun crearProductoConImagen(
        token: String,
        producto: Producto,
        imagen: File?
    ): Result<Producto> =
        withContext(Dispatchers.IO) {
            try {
                // JSON → RequestBody
                val productoJson = gson.toJson(producto)
                    .toRequestBody("application/json".toMediaType())

                // Imagen opcional
                val imagenPart = imagen?.let {
                    MultipartBody.Part.createFormData(
                        "imagen",
                        it.name,
                        it.asRequestBody("image/*".toMediaType())
                    )
                }

                val response = api.crearProductoMultipart(
                    "Bearer $token",
                    productoJson,
                    imagenPart
                )

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()} al crear producto con imagen"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ----------------------------------------------------------
    // ACTUALIZAR PRODUCTO (MULTIPART)
    // ----------------------------------------------------------
    suspend fun actualizarProductoConImagen(
        token: String,
        productoId: Long,
        producto: Producto,
        imagen: File?
    ): Result<Producto> =
        withContext(Dispatchers.IO) {
            try {
                // JSON
                val productoJson = gson.toJson(producto)
                    .toRequestBody("application/json".toMediaType())

                // Imagen opcional
                val imagenPart = imagen?.let {
                    MultipartBody.Part.createFormData(
                        "imagen",
                        it.name,
                        it.asRequestBody("image/*".toMediaType())
                    )
                }

                val response = api.actualizarProductoMultipart(
                    "Bearer $token",
                    productoId,
                    productoJson,
                    imagenPart
                )

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()} al actualizar producto con imagen"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ----------------------------------------------------------
    // ELIMINAR
    // ----------------------------------------------------------
    suspend fun eliminarProducto(token: String, productoId: Long): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.eliminarProducto("Bearer $token", productoId)

                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Error ${response.code()} al eliminar producto"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

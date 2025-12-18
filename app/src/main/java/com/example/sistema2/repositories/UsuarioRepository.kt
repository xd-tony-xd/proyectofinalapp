package com.example.sistema2.repositories

import com.example.sistema2.api.RetrofitClient
import com.example.sistema2.models.Usuario
import com.example.sistema2.models.UsuarioUpdateRequest

class UsuarioRepository {

    suspend fun obtenerUsuario(token: String, id: Long): Result<Usuario> {
        return try {
            val response = RetrofitClient.apiService.obtenerUsuario("Bearer $token", id)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar perfil: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun actualizarUsuario(
        token: String,
        id: Long,
        datos: UsuarioUpdateRequest
    ): Result<Usuario> {
        return try {
            val response = RetrofitClient.apiService.actualizarUsuario(
                "Bearer $token",
                id,
                datos
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar perfil: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}

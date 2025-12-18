package com.example.sistema2.repositories

import com.example.sistema2.api.RetrofitClient
import com.example.sistema2.models.LoginRequest
import com.example.sistema2.models.LoginResponse
import com.example.sistema2.models.RegisterRequest
import com.example.sistema2.models.RegisterResponse
import com.example.sistema2.models.Usuario
import com.example.sistema2.models.UsuarioUpdateRequest

class AuthRepository {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = RetrofitClient.apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en login: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // MÉTODO REGISTER
    suspend fun register(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        telefono: String? = null,
        dni: String? = null,
        direccion: String? = null,
        ciudad: String? = null,
        fechaNacimiento: String? = null,
        fotoPerfil: String? = null
    ): Result<RegisterResponse> {
        return try {
            val registerRequest = RegisterRequest(
                nombre = nombre,
                apellido = apellido,
                email = email,
                password = password,
                telefono = telefono,
                dni = dni,
                direccion = direccion,
                ciudad = ciudad,
                fechaNacimiento = fechaNacimiento,
                fotoPerfil = fotoPerfil
            )

            val response = RetrofitClient.apiService.register(registerRequest)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en registro: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    // actualizarPerfil
    suspend fun actualizarPerfil(
        token: String,
        usuarioId: Long,
        nombre: String? = null,
        apellido: String? = null,
        telefono: String? = null,
        direccion: String? = null,
        ciudad: String? = null,
        fechaNacimiento: String? = null,
        fotoPerfil: String? = null
    ): Result<Usuario> {
        return try {
            val body = UsuarioUpdateRequest(
                nombre = nombre,
                apellido = apellido,
                telefono = telefono,
                direccion = direccion,
                ciudad = ciudad,
                fechaNacimiento = fechaNacimiento,
                fotoPerfil = fotoPerfil
            )

            val response = RetrofitClient.apiService.actualizarUsuario(
                "Bearer $token",
                usuarioId,
                body
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar perfil: ${response.code()} - ${response.message()}"))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

}
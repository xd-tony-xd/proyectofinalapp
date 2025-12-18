package com.example.sistema2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistema2.models.Usuario
import com.example.sistema2.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _estado = MutableStateFlow<PerfilState>(PerfilState.Idle)
    val estado: StateFlow<PerfilState> = _estado

    fun actualizarPerfil(
        token: String,
        id: Long,
        nombre: String?,
        apellido: String?,
        telefono: String?,
        direccion: String?,
        ciudad: String?,
        fechaNacimiento: String?,
        fotoPerfil: String?
    ) {
        viewModelScope.launch {
            _estado.value = PerfilState.Cargando

            val result = repo.actualizarPerfil(
                token, id, nombre, apellido, telefono,
                direccion, ciudad, fechaNacimiento, fotoPerfil
            )

            if (result.isSuccess) {
                _estado.value = PerfilState.Exito(result.getOrNull()!!)
            } else {
                _estado.value = PerfilState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }
}

sealed class PerfilState {
    object Idle : PerfilState()
    object Cargando : PerfilState()
    data class Exito(val usuario: Usuario) : PerfilState()
    data class Error(val mensaje: String) : PerfilState()
}

package com.example.sistema2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistema2.models.Usuario
import com.example.sistema2.models.UsuarioUpdateRequest
import com.example.sistema2.repositories.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {

    private val repo = UsuarioRepository()

    private val _estado = MutableStateFlow<UsuarioState>(UsuarioState.Idle)
    val estado: StateFlow<UsuarioState> = _estado

    fun cargarPerfil(token: String, id: Long) {
        viewModelScope.launch {
            _estado.value = UsuarioState.Cargando

            val result = repo.obtenerUsuario(token, id)

            if (result.isSuccess) {
                _estado.value = UsuarioState.Exito(result.getOrNull()!!)
            } else {
                _estado.value = UsuarioState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun actualizarPerfil(token: String, id: Long, datos: UsuarioUpdateRequest) {
        viewModelScope.launch {
            _estado.value = UsuarioState.Cargando

            val result = repo.actualizarUsuario(token, id, datos)

            if (result.isSuccess) {
                _estado.value = UsuarioState.Exito(result.getOrNull()!!)
            } else {
                _estado.value = UsuarioState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }
}

sealed class UsuarioState {
    object Idle : UsuarioState()
    object Cargando : UsuarioState()
    data class Exito(val usuario: Usuario) : UsuarioState()
    data class Error(val mensaje: String) : UsuarioState()
}

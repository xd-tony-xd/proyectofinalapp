package com.example.sistema2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistema2.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = authRepository.login(email, password)

            if (result.isSuccess) {
                val loginResponse = result.getOrNull()!!
                _loginState.value = LoginState.Success(loginResponse.token, loginResponse.usuario)
            } else {
                _loginState.value = LoginState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    // MÉTODO REGISTER
    fun register(
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
    ) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            val result = authRepository.register(
                nombre, apellido, email, password, telefono,
                dni, direccion, ciudad, fechaNacimiento, fotoPerfil
            )

            if (result.isSuccess) {
                val registerResponse = result.getOrNull()!!
                _registerState.value = RegisterState.Success(registerResponse.token, registerResponse.usuario)
            } else {
                _registerState.value = RegisterState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}

// Estados para Login
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val usuario: com.example.sistema2.models.Usuario) : LoginState()
    data class Error(val message: String) : LoginState()
}

// Estados para Registro
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val token: String, val usuario: com.example.sistema2.models.Usuario) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
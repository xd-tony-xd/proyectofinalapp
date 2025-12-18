package com.example.sistema2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistema2.models.Producto
import com.example.sistema2.repositories.FavoritosRepository
import com.example.sistema2.repositories.ProductoRepository
import kotlinx.coroutines.launch

class FavoritosViewModel(
    private val favoritosRepository: FavoritosRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _productosFavoritos = MutableLiveData<List<Producto>>()
    val productosFavoritos: LiveData<List<Producto>> = _productosFavoritos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarFavoritos(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val idsFavoritos = favoritosRepository.obtenerTodos()

            if (idsFavoritos.isEmpty()) {
                _productosFavoritos.value = emptyList()
                _isLoading.value = false
                return@launch
            }

            // Nota: Idealmente el backend tendría un endpoint para esto.
            // Como no lo tiene, obtenemos todos y filtramos localmente.
            val resultado = productoRepository.obtenerProductos(token)

            resultado.onSuccess { productos ->
                val favoritos = productos.filter { producto ->
                    producto.id?.toString() in idsFavoritos
                }
                _productosFavoritos.value = favoritos
            }.onFailure { exception ->
                _error.value = exception.message
            }

            _isLoading.value = false
        }
    }
}

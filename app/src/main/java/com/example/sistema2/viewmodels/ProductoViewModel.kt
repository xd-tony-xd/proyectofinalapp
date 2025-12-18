package com.example.sistema2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistema2.models.Producto
import com.example.sistema2.repositories.ProductoRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ProductoViewModel : ViewModel() {

    private val repository = ProductoRepository()

    private val _productosState = MutableStateFlow<ProductosState>(ProductosState.Idle)
    val productosState: StateFlow<ProductosState> = _productosState.asStateFlow()

    // ----------------------------------------------------------
    // LISTAR
    // ----------------------------------------------------------
    fun obtenerProductos(token: String) {
        viewModelScope.launch {
            _productosState.value = ProductosState.Loading
            try {
                val productos = repository.obtenerProductos(token).getOrThrow()
                _productosState.value = ProductosState.Success(productos)
            } catch (e: Exception) {
                _productosState.value = ProductosState.Error(e.message ?: "Error al obtener productos")
            }
        }
    }

    // ----------------------------------------------------------
    // DETALLE POR ID
    // ----------------------------------------------------------
    fun obtenerProductoPorId(token: String, productoId: Long) {
        viewModelScope.launch {
            _productosState.value = ProductosState.Loading
            try {
                val producto = repository.obtenerProductoPorId(token, productoId).getOrThrow()
                _productosState.value = ProductosState.ProductoDetalle(producto)
            } catch (e: Exception) {
                _productosState.value = ProductosState.Error(e.message ?: "Error al obtener el producto")
            }
        }
    }

    // ----------------------------------------------------------
    // CREAR PRODUCTO (MULTIPART)
    // ----------------------------------------------------------
    fun crearProductoConImagen(token: String, producto: Producto, imagen: File?) {
        viewModelScope.launch {

            // ------------------------------
            // 🔥 LOG DE JSON ANTES DE ENVIAR
            // ------------------------------
            val json = Gson().toJson(producto)
            Log.d("PRODUCTO_JSON_CREAR", json)
            // ------------------------------

            _productosState.value = ProductosState.Loading

            try {
                val creado = repository.crearProductoConImagen(token, producto, imagen).getOrThrow()
                _productosState.value = ProductosState.ProductoCreado(creado)
            } catch (e: Exception) {
                _productosState.value = ProductosState.Error(e.message ?: "Error al crear producto")
            }
        }
    }

    // ----------------------------------------------------------
    // ACTUALIZAR PRODUCTO (MULTIPART)
    // ----------------------------------------------------------
    fun actualizarProductoConImagen(token: String, productoId: Long, producto: Producto, imagen: File?) {
        viewModelScope.launch {

            // ------------------------------
            // 🔥 LOG DE JSON ANTES DE ENVIAR
            // ------------------------------
            val json = Gson().toJson(producto)
            Log.d("PRODUCTO_JSON_ACTUALIZAR", json)
            // ------------------------------

            _productosState.value = ProductosState.Loading

            try {
                val actualizado = repository.actualizarProductoConImagen(token, productoId, producto, imagen).getOrThrow()
                _productosState.value = ProductosState.ProductoActualizado(actualizado)
            } catch (e: Exception) {
                _productosState.value = ProductosState.Error(e.message ?: "Error al actualizar producto")
            }
        }
    }

    // ----------------------------------------------------------
    // RESET
    // ----------------------------------------------------------
    fun resetState() {
        _productosState.value = ProductosState.Idle
    }
}

// =============================== ESTADOS ===============================
sealed class ProductosState {
    object Idle : ProductosState()
    object Loading : ProductosState()
    data class Success(val productos: List<Producto>) : ProductosState()
    data class ProductoDetalle(val producto: Producto) : ProductosState()
    data class ProductoCreado(val producto: Producto) : ProductosState()
    data class ProductoActualizado(val producto: Producto) : ProductosState()
    data class Error(val message: String) : ProductosState()
}

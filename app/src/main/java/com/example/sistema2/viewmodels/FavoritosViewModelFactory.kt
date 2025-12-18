package com.example.sistema2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sistema2.repositories.FavoritosRepository
import com.example.sistema2.repositories.ProductoRepository

class FavoritosViewModelFactory(
    private val favoritosRepository: FavoritosRepository,
    private val productoRepository: ProductoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritosViewModel(favoritosRepository, productoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

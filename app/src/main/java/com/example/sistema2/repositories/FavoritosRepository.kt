package com.example.sistema2.repositories

import android.content.Context
import android.content.SharedPreferences

class FavoritosRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("favoritos_prefs", Context.MODE_PRIVATE)

    fun agregar(productoId: Long) {
        val editor = prefs.edit()
        editor.putBoolean(productoId.toString(), true)
        editor.apply()
    }

    fun eliminar(productoId: Long) {
        val editor = prefs.edit()
        editor.remove(productoId.toString())
        editor.apply()
    }

    fun esFavorito(productoId: Long): Boolean {
        return prefs.contains(productoId.toString())
    }

    fun obtenerTodos(): Set<String> {
        return prefs.all.keys
    }
}

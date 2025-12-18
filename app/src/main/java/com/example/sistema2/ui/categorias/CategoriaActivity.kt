package com.example.sistema2.ui.categorias

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistema2.adapters.CategoriaAdapter
import com.example.sistema2.api.RetrofitClient
import com.example.sistema2.databinding.ActivityCategoriaBinding
import com.example.sistema2.models.Categoria
import com.example.sistema2.ui.productos.ProductosPorCategoriaActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriaBinding

    private val token: String by lazy {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        prefs.getString("token", "") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        cargarCategorias()
    }

    private fun cargarCategorias() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerCategorias("Bearer $token")
                }

                if (response.isSuccessful) {
                    val categorias: List<Categoria> = response.body() ?: emptyList()

                    binding.recyclerView.adapter = CategoriaAdapter(categorias) { categoria ->
                        val intent = Intent(
                            this@CategoriaActivity,
                            ProductosPorCategoriaActivity::class.java
                        )
                        intent.putExtra("CATEGORIA_ID", categoria.id!!)
                        intent.putExtra("CATEGORIA_NOMBRE", categoria.nombre)
                        startActivity(intent)
                    }
                } else {
                    Log.e("CategoriaActivity", "Error ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("CategoriaActivity", e.message ?: "Error")
            }
        }
    }
}

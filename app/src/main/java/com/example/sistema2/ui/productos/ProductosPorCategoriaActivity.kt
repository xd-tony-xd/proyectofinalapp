package com.example.sistema2.ui.productos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema2.R
import com.example.sistema2.adapters.ProductoAdapter
import com.example.sistema2.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductosPorCategoriaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val token: String by lazy {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        prefs.getString("token", "") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_por_categoria)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val categoriaId = intent.getLongExtra("CATEGORIA_ID", -1)
        val categoriaNombre = intent.getStringExtra("CATEGORIA_NOMBRE")

        title = categoriaNombre ?: "Productos"

        if (categoriaId != -1L) {
            cargarProductos(categoriaId)
        }
    }

    private fun cargarProductos(categoriaId: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.obtenerProductos("Bearer $token")
            }

            if (response.isSuccessful) {
                val filtrados = response.body()
                    ?.filter { it.categoria?.id == categoriaId }
                    ?: emptyList()

                recyclerView.adapter = ProductoAdapter(filtrados)
            }
        }
    }
}

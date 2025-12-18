package com.example.sistema2.ui.productos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistema2.databinding.ActivityProductoListBinding
import com.example.sistema2.adapters.ProductoAdapter
import com.example.sistema2.viewmodels.ProductoViewModel
import com.example.sistema2.viewmodels.ProductosState
import kotlinx.coroutines.launch

class ProductoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductoListBinding
    private val viewModel: ProductoViewModel by viewModels()
    private lateinit var productoAdapter: ProductoAdapter

    // Mantener un set local de favoritos
    private val favoritosSet = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        cargarProductos()
    }

    private fun setupRecyclerView() {
        productoAdapter = ProductoAdapter(
            favoritosIds = favoritosSet,
            onItemClick = { producto ->
                Toast.makeText(this, "Clic en: ${producto.titulo}", Toast.LENGTH_SHORT).show()
            },
            onFavoritoClick = { producto, agregado ->
                val id = producto.id?.toString() ?: return@ProductoAdapter

                if (agregado) {
                    favoritosSet.add(id)
                    Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    favoritosSet.remove(id)
                    Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }

                // Actualizar el adaptador con los nuevos favoritos
                productoAdapter.updateFavoritos(favoritosSet)
            }
        )

        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(this@ProductoListActivity)
            adapter = productoAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.productosState.collect { state ->
                when (state) {

                    is ProductosState.Idle -> hideLoading()

                    is ProductosState.Loading -> showLoading()

                    is ProductosState.Success -> {
                        hideLoading()
                        productoAdapter.updateProductos(state.productos)
                        binding.tvEmpty.isVisible = state.productos.isEmpty()
                    }

                    is ProductosState.Error -> {
                        hideLoading()
                        Toast.makeText(
                            this@ProductoListActivity,
                            "Error: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> hideLoading()
                }
            }
        }
    }

    private fun cargarProductos() {
        val sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        if (token != null) {
            viewModel.obtenerProductos(token)
        } else {
            Toast.makeText(this, "No hay token de autenticación", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, com.example.sistema2.ui.login.LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.rvProductos.isVisible = false
        binding.tvEmpty.isVisible = false
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
        binding.rvProductos.isVisible = true
    }
}

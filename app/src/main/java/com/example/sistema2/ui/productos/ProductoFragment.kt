package com.example.sistema2.ui.productos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistema2.adapters.ProductoAdapter
import com.example.sistema2.databinding.ActivityProductoListBinding
import com.example.sistema2.repositories.FavoritosRepository
import com.example.sistema2.ui.productos.DetalleProductoActivity
import com.example.sistema2.viewmodels.ProductoViewModel
import com.example.sistema2.viewmodels.ProductosState
import kotlinx.coroutines.launch

class ProductoFragment : Fragment() {

    private var _binding: ActivityProductoListBinding? = null
    private val binding get() = _binding!!

    private lateinit var productoAdapter: ProductoAdapter
    private val viewModel: ProductoViewModel by viewModels()
    private lateinit var favoritosRepository: FavoritosRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityProductoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritosRepository = FavoritosRepository(requireContext())
        setupRecyclerView()
        setupObservers()
        cargarProductos()
    }

    private fun setupRecyclerView() {
        productoAdapter = ProductoAdapter(
            favoritosIds = favoritosRepository.obtenerTodos(),
            onItemClick = { producto ->
                // Navegar al detalle
                val intent = Intent(requireContext(), DetalleProductoActivity::class.java)
                intent.putExtra("productoId", producto.id)
                startActivity(intent)
            },
            onFavoritoClick = { producto, agregado ->
                val id = producto.id ?: return@ProductoAdapter
                if (agregado) {
                    favoritosRepository.agregar(id)
                    Toast.makeText(requireContext(), "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    favoritosRepository.eliminar(id)
                    Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }
                // Actualizar la lista de favoritos en el adaptador para cambiar color del corazón
                productoAdapter.updateFavoritos(favoritosRepository.obtenerTodos())
            }
        )

        binding.rvProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductos.adapter = productoAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.productosState.collect { state ->
                when (state) {
                    ProductosState.Loading -> binding.progressBar.visibility = View.VISIBLE

                    is ProductosState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        productoAdapter.updateProductos(state.productos)
                        binding.tvEmpty.visibility =
                            if (state.productos.isEmpty()) View.VISIBLE else View.GONE
                        // Actualizar favoritos también
                        productoAdapter.updateFavoritos(favoritosRepository.obtenerTodos())
                    }

                    is ProductosState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun cargarProductos() {
        val sharedPref =
            requireContext().getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        if (token != null) {
            viewModel.obtenerProductos(token)
        } else {
            Toast.makeText(requireContext(), "No hay token disponible", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la lista de favoritos al volver al fragment
        productoAdapter.updateFavoritos(favoritosRepository.obtenerTodos())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.sistema2.ui.favoritos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema2.R
import com.example.sistema2.adapters.ProductoAdapter
import com.example.sistema2.repositories.FavoritosRepository
import com.example.sistema2.repositories.ProductoRepository
import com.example.sistema2.viewmodels.FavoritosViewModel
import com.example.sistema2.viewmodels.FavoritosViewModelFactory

class FavoritosFragment : Fragment() {

    private lateinit var viewModel: FavoritosViewModel
    private lateinit var adapter: ProductoAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorito, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        recyclerView = view.findViewById(R.id.recyclerView)

        setupRecyclerView()
        setupViewModel()
        observeViewModel()

        cargarFavoritos()

        return view
    }

    override fun onResume() {
        super.onResume()
        // Recargar al volver
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: ""
        if (token.isNotEmpty()) {
            viewModel.cargarFavoritos(token)
        } else {
            Toast.makeText(context, "Debes iniciar sesión para ver favoritos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val favoritosRepository = FavoritosRepository(requireContext())

        adapter = ProductoAdapter(
            favoritosIds = favoritosRepository.obtenerTodos(),
            onItemClick = { producto ->
                // Opcional: navegar al detalle
            },
            onFavoritoClick = { producto, agregado ->
                val id = producto.id ?: return@ProductoAdapter
                if (agregado) {
                    favoritosRepository.agregar(id)
                    Toast.makeText(context, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    favoritosRepository.eliminar(id)
                    Toast.makeText(context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }

                // Recargar la lista de favoritos
                cargarFavoritos()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        val favoritosRepository = FavoritosRepository(requireContext())
        val productoRepository = ProductoRepository()
        val factory = FavoritosViewModelFactory(favoritosRepository, productoRepository)
        viewModel = ViewModelProvider(this, factory)[FavoritosViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.productosFavoritos.observe(viewLifecycleOwner) { productos ->
            adapter.updateProductos(productos)
            // Actualizar también los IDs de favoritos en el adaptador para que los corazones salgan pintados
            adapter.updateFavoritos(productos.mapNotNull { it.id?.toString() }.toSet())

            tvEmpty.visibility = if (productos.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (productos.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

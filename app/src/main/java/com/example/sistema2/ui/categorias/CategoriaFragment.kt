package com.example.sistema2.ui.categorias

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistema2.adapters.CategoriaAdapter
import com.example.sistema2.api.RetrofitClient
import com.example.sistema2.databinding.FragmentCategoriaBinding
import com.example.sistema2.models.Categoria
import com.example.sistema2.ui.productos.ProductosPorCategoriaActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriaFragment : Fragment() {

    private var _binding: FragmentCategoriaBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CategoriaAdapter

    private val token: String by lazy {
        requireContext().getSharedPreferences("mi_app_prefs", 0)
            .getString("token", "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
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

                    adapter = CategoriaAdapter(categorias) { categoria ->
                        val intent = Intent(requireContext(), ProductosPorCategoriaActivity::class.java)
                        intent.putExtra("CATEGORIA_ID", categoria.id)
                        intent.putExtra("CATEGORIA_NOMBRE", categoria.nombre)
                        startActivity(intent)
                    }
                    binding.recyclerView.adapter = adapter

                } else {
                    Log.e("CategoriaFragment", "Error: Código ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("CategoriaFragment", "Error en la llamada: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

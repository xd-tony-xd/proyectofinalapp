package com.example.sistema2.ui.misproductos

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.example.sistema2.R
import com.example.sistema2.adapters.MisProductosAdapter
import com.example.sistema2.api.RetrofitClient
import com.example.sistema2.databinding.FragmentMisProductosBinding
import com.example.sistema2.models.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MisProductosFragment : Fragment() {

    private var _binding: FragmentMisProductosBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MisProductosAdapter

    private val token: String by lazy {
        val prefs = requireContext().getSharedPreferences("auth_prefs", 0)
        prefs.getString("token", "") ?: ""
    }

    private val usuarioId: Long by lazy {
        val prefs = requireContext().getSharedPreferences("auth_prefs", 0)
        prefs.getLong("usuarioId", -1L)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView
        binding.recyclerMisProductos.layoutManager = LinearLayoutManager(requireContext())
        adapter = MisProductosAdapter(
            onEditar = { producto -> editarProducto(producto) },
            onEliminar = { producto -> eliminarProducto(producto) }
        )
        binding.recyclerMisProductos.adapter = adapter

        // FAB abre el fragment de agregar producto
        binding.fabAgregar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.example.sistema2.ui.agregar.AgregarFragment())
                .addToBackStack(null)
                .commit()
        }

        // Cargar productos del usuario
        cargarProductos()
    }

    private fun cargarProductos() {
        if (token.isEmpty() || usuarioId == -1L) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerProductosPorUsuario("Bearer $token", usuarioId)
                }

                if (response.isSuccessful) {
                    val productos: List<Producto> = response.body() ?: emptyList()
                    adapter.setData(productos)
                    Toast.makeText(requireContext(), "Productos cargados: ${productos.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al cargar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar productos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editarProducto(producto: Producto) {
        // Abrir fragmento de edición
        val fragment = com.example.sistema2.ui.editar.EditarProductoFragment()

        // Pasar el producto al fragmento (Producto debe implementar Parcelable)
        val bundle = Bundle()
        bundle.putLong("productoId", producto.id ?: -1L)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun eliminarProducto(producto: Producto) {
        val productoId = producto.id ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.eliminarProducto("Bearer $token", productoId)
                }

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                    cargarProductos() // Recargar lista
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

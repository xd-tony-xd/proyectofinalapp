package com.example.sistema2.ui.productos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.sistema2.MainActivity
import com.example.sistema2.R
import com.example.sistema2.databinding.ActivityDetalleProductoBinding
import com.example.sistema2.models.Producto
import com.example.sistema2.viewmodels.ProductoViewModel
import com.example.sistema2.viewmodels.ProductosState
import kotlinx.coroutines.launch

class DetalleProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoBinding
    private val viewModel: ProductoViewModel by viewModels()
    private fun navegarA(destino: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("destino", destino)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idProducto = intent.getLongExtra("productoId", -1)
        if (idProducto == -1L) {
            finish()
            return
        }

        val sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        val token = sharedPref.getString("token", null) ?: return

        viewModel.obtenerProductoPorId(token, idProducto)

        lifecycleScope.launch {
            viewModel.productosState.collect { state ->
                when (state) {

                    is ProductosState.ProductoDetalle -> {
                        cargarDatos(state.producto)
                    }

                    is ProductosState.Error -> {
                        binding.tvTituloDetalle.text = "Error: ${state.message}"
                    }

                    else -> {}
                }
            }
        }
        binding.bottomNavigationDetalle.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_productos -> {
                    navegarA("productos")
                    true
                }

                R.id.nav_categorias -> {
                    navegarA("categorias")
                    true
                }

                R.id.nav_agregar -> {
                    navegarA("agregar")
                    true
                }

                R.id.nav_favoritos -> {
                    navegarA("favoritos")
                    true
                }

                R.id.nav_perfil -> {
                    navegarA("perfil")
                    true
                }

                else -> false
            }
        }

    }

    private fun cargarDatos(p: Producto) {
        binding.tvTituloDetalle.text = p.titulo
        binding.tvDescripcionDetalle.text = p.descripcion
        binding.tvPrecioDetalle.text = "S/. ${p.precio}"
        binding.tvEstadoDetalle.text = p.estado
        binding.tvUbicacionDetalle.text = p.direccion
        binding.tvUsuarioDetalle.text = "Publicado por: ${p.usuario?.nombre}"
        binding.tvTelefonoDetalle.text = "Teléfono: ${p.usuario?.telefono}"
        binding.tvFechaDetalle.text = p.fechaPublicacion

        Glide.with(this)
            .load(p.imagenUrl)
            .into(binding.ivImagenDetalle)

        binding.btnWhatsAppDetalle.setOnClickListener {
            val numero = p.usuario?.telefono ?: return@setOnClickListener
            val url = "https://wa.me/51$numero"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        binding.btnMapaDetalle.setOnClickListener {
            val geo = Uri.parse("geo:${p.latitud},${p.longitud}")
            startActivity(Intent(Intent.ACTION_VIEW, geo))
        }
    }
}

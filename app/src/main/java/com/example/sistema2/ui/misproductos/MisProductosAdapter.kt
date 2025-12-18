package com.example.sistema2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistema2.R
import com.example.sistema2.models.Producto

class MisProductosAdapter(
    private val onEditar: (Producto) -> Unit,
    private val onEliminar: (Producto) -> Unit
) : RecyclerView.Adapter<MisProductosAdapter.ViewHolder>() {

    private var productos = mutableListOf<Producto>()

    // 🔥 FUNCIÓN PARA ARREGLAR URLS CON "localhost"
    private fun fixUrl(url: String?): String {
        if (url.isNullOrEmpty()) return ""
        return url.replace("localhost", "10.0.2.2")   // Para emulador Android
    }

    fun setData(lista: List<Producto>) {
        productos.clear()
        productos.addAll(lista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = productos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvTitulo.text = producto.titulo ?: "Sin título"
        holder.tvPrecio.text = "S/. ${producto.precio ?: 0.0}"
        holder.tvEstado.text = producto.estado ?: "DESCONOCIDO"
        holder.tvUbicacion.text = producto.direccion ?: "Ubicación no disponible"

        // 🔥 Cargar imagen con URL corregida
        val urlFinal = fixUrl(producto.imagenUrl)

        Glide.with(holder.ivProducto.context)
            .load(urlFinal)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(holder.ivProducto)

        // Botones
        holder.btnEditar.setOnClickListener { onEditar(producto) }
        holder.btnEliminar.setOnClickListener { onEliminar(producto) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProducto: ImageView = view.findViewById(R.id.ivProducto)
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvUbicacion: TextView = view.findViewById(R.id.tvUbicacion)

        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }
}

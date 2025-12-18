package com.example.sistema2.adapters

import android.content.Intent
import android.net.Uri
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
import com.example.sistema2.ui.productos.DetalleProductoActivity
import androidx.core.content.ContextCompat

class ProductoAdapter(
    private var productos: List<Producto> = emptyList(),
    private var favoritosIds: Set<String> = emptySet(),
    private val onItemClick: (Producto) -> Unit = {},
    private val onFavoritoClick: (Producto, Boolean) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_general, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount(): Int = productos.size

    fun updateProductos(newProductos: List<Producto>) {
        productos = newProductos
        notifyDataSetChanged()
    }

    fun updateFavoritos(newFavoritos: Set<String>) {
        favoritosIds = newFavoritos
        notifyDataSetChanged()
    }

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivImagen: ImageView = itemView.findViewById(R.id.ivProducto)
        private val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        private val tvUbicacion: TextView = itemView.findViewById(R.id.tvUbicacion)
        private val btnWhatsapp: ImageButton = itemView.findViewById(R.id.btnWhatsApp)
        private val btnFavorito: ImageButton = itemView.findViewById(R.id.btnFavorito)

        // ============================
        // 🔥 FUNCIÓN PARA CORREGIR URL
        // ============================
        private fun fixUrl(url: String?): String {
            if (url.isNullOrBlank()) return ""

            // Si ya viene completo, no tocarlo
            if (url.startsWith("http")) return url

            // Si viene solo el nombre → agregar dominio
            return "https://electoral-laurice-tonyxyz-524abfe8.koyeb.app/uploads/$url"
        }

        fun bind(producto: Producto) {

            // =========================
            // DATOS BÁSICOS
            // =========================
            tvTitulo.text = producto.titulo ?: "Sin título"
            tvPrecio.text = "S/. ${producto.precio ?: 0.0}"
            tvEstado.text = producto.estado ?: "DISPONIBLE"
            tvUbicacion.text = producto.direccion ?: "Ubicación no disponible"

            // =========================
            // 🔥 CARGA DE IMAGEN FINAL
            // =========================
            val imagenCorregida = fixUrl(producto.imagenUrl)

            Glide.with(itemView.context)
                .load(imagenCorregida)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(ivImagen)

            ivImagen.visibility = View.VISIBLE

            // =========================
            // CLICK → DETALLE
            // =========================
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetalleProductoActivity::class.java)
                intent.putExtra("productoId", producto.id)
                itemView.context.startActivity(intent)
                onItemClick(producto)
            }

            // =========================
            // CLICK → WHATSAPP
            // =========================
            btnWhatsapp.setOnClickListener {
                val numero = producto.usuario?.telefono ?: ""
                if (numero.isNotEmpty()) {
                    val url = "https://wa.me/51$numero"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    itemView.context.startActivity(intent)
                }
            }

            // =========================
            // FAVORITOS
            // =========================
            fun actualizarColor() {
                val color = if (favoritosIds.contains(producto.id.toString())) {
                    ContextCompat.getColor(itemView.context, R.color.purple_500)
                } else {
                    ContextCompat.getColor(itemView.context, android.R.color.darker_gray)
                }
                btnFavorito.setColorFilter(color)
            }

            actualizarColor()

            btnFavorito.setOnClickListener {
                val idStr = producto.id.toString()
                val esFavorito = favoritosIds.contains(idStr)

                favoritosIds = if (esFavorito) {
                    favoritosIds - idStr
                } else {
                    favoritosIds + idStr
                }

                actualizarColor()
                onFavoritoClick(producto, !esFavorito)
            }
        }
    }
}

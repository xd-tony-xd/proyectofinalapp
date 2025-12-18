package com.example.sistema2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistema2.R
import com.example.sistema2.models.Categoria

class CategoriaAdapter(
    private val categorias: List<Categoria>,
    private val onCategoriaClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.tvNombreCategoria)
        val descripcionTextView: TextView = itemView.findViewById(R.id.tvDescripcionCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.nombreTextView.text = categoria.nombre
        holder.descripcionTextView.text = categoria.descripcion

        holder.itemView.setOnClickListener {
            onCategoriaClick(categoria)
        }
    }

    override fun getItemCount(): Int = categorias.size
}

package com.nicorodgon.listapp.ui.trash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nicorodgon.listapp.databinding.ViewTrashItemBinding
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista

class AdapterTrash(val listener: (Lista) -> Unit):
    RecyclerView.Adapter<AdapterTrash.ViewHolder>() {

    var listas = emptyList<Lista>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewTrashItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lista = listas[position]
        holder.bind(lista)

        holder.itemView.setOnClickListener {
            listener(lista)
        }

    }

    override fun getItemCount(): Int = listas.size

    class ViewHolder(private val binding: ViewTrashItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: Lista) {

            binding.nombreLista.text = lista.nombre_lista
            binding.enableListaButton.setOnClickListener{
                DbFirestore.enableLista(lista)
            }

            Glide
                .with(binding.root.context)
                .load(lista.imagen_lista)
                .into(binding.imagenLista)

        }

    }

}
package com.nicorodgon.listapp.ui.adminPanelListas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicorodgon.listapp.databinding.ViewAdminListaItemBinding
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista

class AdapterAdminPanelListas(val listener: (Lista) -> Unit):
    RecyclerView.Adapter<AdapterAdminPanelListas.ViewHolder>() {

    var listas = emptyList<Lista>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewAdminListaItemBinding.inflate(
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

    class ViewHolder(private val binding: ViewAdminListaItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: Lista) {
            binding.nombreListaAdmin.text = lista.nombre_lista + "\n" + lista.creador

            binding.deleteListaAdminButton.setOnClickListener {
                DbFirestore.deleteListaAdmin(lista)
            }
        }
    }
}
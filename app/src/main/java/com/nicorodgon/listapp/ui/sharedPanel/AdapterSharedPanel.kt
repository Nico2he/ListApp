package com.nicorodgon.listapp.ui.sharedPanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.nicorodgon.listapp.databinding.ViewSharedListaItemBinding
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista

class AdapterSharedPanel(val listener: (Lista) -> Unit):
    RecyclerView.Adapter<AdapterSharedPanel.ViewHolder>() {

    var listas = emptyList<Lista>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewSharedListaItemBinding.inflate(
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

    class ViewHolder(private val binding: ViewSharedListaItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: Lista) {
            val email = FirebaseAuth.getInstance().currentUser?.email

            binding.nombreSharedLista.text = lista.nombre_lista + "\n(" + lista.creador + ")"

            binding.disableSharedListaButton.setOnClickListener{
                if (email != null) {
                    DbFirestore.disableSharedLista(email, lista)
                }
            }
        }
    }
}
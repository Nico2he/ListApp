package com.nicorodgon.listapp.ui.sharedTrash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.nicorodgon.listapp.databinding.ViewSharedTrashItemBinding
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista

class AdapterSharedTrash(val listener: (Lista) -> Unit):
    RecyclerView.Adapter<AdapterSharedTrash.ViewHolder>() {

    var listas = emptyList<Lista>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewSharedTrashItemBinding.inflate(
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

    class ViewHolder(private val binding: ViewSharedTrashItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: Lista) {
            val email = FirebaseAuth.getInstance().currentUser?.email

            binding.nombreSharedLista.text = lista.nombre_lista + "\n(" + lista.creador + ")"

            binding.enableSharedListaButton.setOnClickListener{
                if (email != null) {
                    DbFirestore.enableSharedLista(email, lista)
                }
            }
        }
    }
}
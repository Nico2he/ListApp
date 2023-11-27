package com.nicorodgon.listapp.ui.userPanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.ViewListaItemBinding
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista

class AdapterUserPanel(val listener: (Lista) -> Unit):
    RecyclerView.Adapter<AdapterUserPanel.ViewHolder>() {

    var listas = emptyList<Lista>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewListaItemBinding.inflate(
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

    class ViewHolder(private val binding: ViewListaItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: Lista) {
            val email = FirebaseAuth.getInstance().currentUser?.email

            binding.nombreLista.text = lista.nombre_lista

            binding.disableListaButton.setOnClickListener{
                if (email != null) {
                    DbFirestore.disableLista(email, lista)
                }
            }

            binding.shareListaButton.setOnClickListener{
                Navigation.findNavController(itemView).navigate(R.id.action_userPanelFragment_to_shareListaFragment, bundleOf(Pair("lista", lista)))
            }

            Glide
                .with(binding.root.context)
                .load(lista.imagen_lista)
                .into(binding.imagenLista)
        }
    }
}
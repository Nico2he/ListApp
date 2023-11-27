package com.nicorodgon.listapp.ui.adminPanelUsuarios

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicorodgon.listapp.databinding.ViewAdminUsuarioItemBinding
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Usuario

class AdapterAdminPanelUsuarios(val listener: (Usuario) -> Unit):
    RecyclerView.Adapter<AdapterAdminPanelUsuarios.ViewHolder>() {

    var usuarios = emptyList<Usuario>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewAdminUsuarioItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.bind(usuario)

        holder.itemView.setOnClickListener {
            listener(usuario)
        }
    }

    override fun getItemCount(): Int = usuarios.size

    class ViewHolder(private val binding: ViewAdminUsuarioItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(usuario: Usuario) {
            binding.nombreUsuarioAdmin.text = usuario.email

            binding.deleteUsuarioAdminButton.setOnClickListener {
                DbFirestore.deleteUsuarioAdmin(usuario)
            }
        }
    }
}
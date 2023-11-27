package com.nicorodgon.listapp.ui.adminPanelItems

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicorodgon.listapp.databinding.ViewAdminItemItemBinding
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.ItemLista

class AdapterAdminPanelItems(val listener: (ItemLista) -> Unit):
    RecyclerView.Adapter<AdapterAdminPanelItems.ViewHolder>() {

    var items = emptyList<ItemLista>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewAdminItemItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: ViewAdminItemItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemLista) {
            binding.nombreItemAdmin.text = item.nombre_item + " (" + item.nombre_lista_item + ")\n" + item.creador_lista_item

            binding.deleteItemAdminButton.setOnClickListener {
                DbFirestore.deleteItemAdmin(item)
            }
        }
    }
}
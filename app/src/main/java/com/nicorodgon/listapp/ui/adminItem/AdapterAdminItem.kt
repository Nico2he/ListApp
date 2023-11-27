package com.nicorodgon.listapp.ui.adminItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicorodgon.listapp.databinding.ViewAdminItemsItemBinding
import com.nicorodgon.listapp.model.ItemLista

class AdapterAdminItem(val listener: (ItemLista) -> Unit):
    RecyclerView.Adapter<AdapterAdminItem.ViewHolder>() {

    var items = emptyList<ItemLista>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewAdminItemsItemBinding.inflate(
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

    class ViewHolder(private val binding: ViewAdminItemsItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemLista) {
            binding.nombreAdminItemsItem.text = item.nombre_item
        }
    }
}
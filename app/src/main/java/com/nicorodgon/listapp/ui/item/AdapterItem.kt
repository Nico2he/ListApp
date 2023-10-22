package com.nicorodgon.listapp.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nicorodgon.listapp.databinding.ViewItemItemBinding
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.ItemLista

class AdapterItem(val listener: (ItemLista) -> Unit):
    RecyclerView.Adapter<AdapterItem.ViewHolder>() {

    var items = emptyList<ItemLista>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewItemItemBinding.inflate(
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

    class ViewHolder(private val binding: ViewItemItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemLista) {

            binding.nombreItem.text = item.nombre_item
            binding.disableItemButton.setOnClickListener{
                DbFirestore.disableItem(item)
            }

            Glide
                .with(binding.root.context)
                .load(item.imagen_item)
                .into(binding.imagenItem)

        }

    }

}
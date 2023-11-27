package com.nicorodgon.listapp.ui.item

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentItemBinding
import com.nicorodgon.listapp.model.Lista
import com.nicorodgon.listapp.ui.itemDetail.ItemDetailFragment

class ItemFragment : Fragment(R.layout.fragment_item) {

    private val viewModel: ItemViewModel by viewModels {
        ItemViewModelFactory(arguments?.getParcelable(EXTRA_LISTA)!!)
    }
    companion object {
        const val EXTRA_LISTA = "ItemFragment:Lista"
    }
    private lateinit var binding: FragmentItemBinding
    private val adapterItem = AdapterItem(){ itemLista -> viewModel.navigateToItem(itemLista)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentItemBinding.bind(view).apply {
            recyclerItem.adapter = adapterItem
        }

        val imagenListaDetalles = view.findViewById<ImageView>(R.id.imagenListaDetalles)
        val nombreListaDetalles = view.findViewById<TextView>(R.id.nombreListaDetalles)

        var nombreListaPadre = ""

        viewModel.lista.observe(viewLifecycleOwner){ lista: Lista ->
            (requireActivity() as AppCompatActivity).supportActionBar?.title = lista.nombre_lista
            Glide.with(this).load(lista.imagen_lista).into(imagenListaDetalles)
            nombreListaDetalles.text = lista.nombre_lista
            nombreListaPadre = lista.nombre_lista
        }

        viewModel.state.observe(viewLifecycleOwner){state ->
            binding.progress.visibility =  if (state.loading) View.VISIBLE else View.GONE
            state.items?.let {
                adapterItem.items = state.items
                adapterItem.notifyDataSetChanged()
            }

            state.navigateToItem?.let {
                findNavController().navigate(
                    R.id.action_itemFragment_to_itemDetailFragment,
                    bundleOf(ItemDetailFragment.EXTRA_ITEM to it)
                )
                viewModel.onNavigateDone()
            }
        }

        binding.addItemButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_itemFragment_to_createItemFragment,
                bundleOf(Pair("lista", nombreListaPadre), Pair("creador", viewModel.creador))
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ItemFragment", "onDestroy")
    }
}
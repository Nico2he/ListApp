package com.nicorodgon.listapp.ui.adminItem

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentAdminItemBinding
import com.nicorodgon.listapp.model.Lista

class AdminItemFragment : Fragment(R.layout.fragment_admin_item) {

    private val viewModel: AdminItemViewModel by viewModels {
        AdminItemViewModelFactory(arguments?.getParcelable(EXTRA_LISTA)!!)
    }

    companion object {
        const val EXTRA_LISTA = "ItemFragment:Lista"
    }

    private lateinit var binding: FragmentAdminItemBinding
    private val adapterAdminItem = AdapterAdminItem(){ itemLista -> viewModel.navigateToItem(itemLista)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAdminItemBinding.bind(view).apply {
            recyclerAdminItem.adapter = adapterAdminItem
        }

        viewModel.lista.observe(viewLifecycleOwner){ lista: Lista ->
            (requireActivity() as AppCompatActivity).supportActionBar?.title = lista.nombre_lista
        }

        viewModel.state.observe(viewLifecycleOwner){state ->
            binding.progressAdminItem.visibility =  if (state.loading) View.VISIBLE else View.GONE
            state.items?.let {
                adapterAdminItem.items = state.items
                adapterAdminItem.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AdminItemFragment", "onDestroy")
    }
}
package com.nicorodgon.listapp.ui.adminItemDetail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentAdminItemDetailBinding
import com.nicorodgon.listapp.model.ItemLista

class AdminItemDetailFragment : Fragment(R.layout.fragment_admin_item_detail) {

    private val viewModel: AdminItemDetailViewModel by viewModels {
        AdminItemDetailViewModelFactory(arguments?.getParcelable(EXTRA_ITEM)!!)
    }

    companion object {
        const val EXTRA_ITEM = "AdminItemDetailFragment:ItemLista"
    }

    private lateinit var binding: FragmentAdminItemDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminItemDetailBinding.bind(view)

        viewModel.item.observe(viewLifecycleOwner){ item: ItemLista ->
            (requireActivity() as AppCompatActivity).supportActionBar?.title = item.nombre_item
            binding.descripcionAdminItemDetail.text = item.descripcion
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AdminItemDetailFragment", "onDestroy")
    }
}
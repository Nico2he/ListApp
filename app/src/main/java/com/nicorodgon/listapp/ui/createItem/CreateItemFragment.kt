package com.nicorodgon.listapp.ui.createItem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentCreateItemBinding
import com.nicorodgon.listapp.model.ItemLista
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateItemFragment : Fragment(R.layout.fragment_create_item) {
    private val viewModel: CreateItemViewModel by viewModels()
    private var selectedUri = ""
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedUri = uri.toString()
        } else {
            Log.d("PhotoPicker", "No se ha seleccionado imagen")
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateItemBinding.bind(view)
        val nombreListaPadre = arguments?.getString("lista")!!

        binding.createItemButton.setOnClickListener {
            val calendar = Calendar.getInstance().time
            val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val dateString = date.format(calendar)
            val item = ItemLista(
                binding.editTextNombreItem.text.toString(),
                nombreListaPadre,
                binding.editTextDescripcionItem.text.toString(),
                dateString,
                "",
                selectedUri,
                0
            )
            viewModel.createItem(item)

            binding.verifyItemCreated.text = "Ítem creado con éxito"

        }

        binding.chooseImageItemButton.setOnClickListener {
            startGallery()
        }
    }

    private fun startGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}
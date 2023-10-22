package com.nicorodgon.listapp.ui.createLista

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentCreateListaBinding
import com.nicorodgon.listapp.model.Lista

class CreateListaFragment : Fragment(R.layout.fragment_create_lista) {
    private val viewModel: CreateListaViewModel by viewModels()
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
        val binding = FragmentCreateListaBinding.bind(view)

        binding.createListaButton.setOnClickListener {
            val lista = Lista(
                binding.editTextNombreLista.text.toString(),
                selectedUri,
                true
            )
            viewModel.createLista(lista)
            findNavController().navigate(
                R.id.action_createListaFragment_to_userPanelFragment)
        }

        binding.chooseImageListaButton.setOnClickListener {
            startGallery()
        }
    }

    private fun startGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}
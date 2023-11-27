package com.nicorodgon.listapp.ui.createLista

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentCreateListaBinding
import com.nicorodgon.listapp.model.Lista

class CreateListaFragment : Fragment(R.layout.fragment_create_lista) {

    private val viewModel: CreateListaViewModel by viewModels()
    private val email = FirebaseAuth.getInstance().currentUser?.email
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

        //Se crea la lista con la información proporcionada por el usuario
        binding.createListaButton.setOnClickListener {
            val lista = email?.let { it1 ->
                Lista(
                    it1,
                    binding.editTextNombreLista.text.toString(),
                    selectedUri,
                    true,
                    ""
                )
            }
            if (lista != null) {
                viewModel.createLista(lista, binding.verifyListaCreated)
            }
        }

        binding.chooseImageListaButton.setOnClickListener {
            startGallery()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CreateListaFragment", "onDestroy")
    }

    //La función startGallery abre la galería para que el usuario pueda seleccionar una imagen
    private fun startGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
package com.nicorodgon.listapp.ui.adminPanelUsuarios

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentAdminPanelUsuariosBinding
import com.nicorodgon.listapp.model.Usuario
import java.util.Locale
import java.util.Objects

class AdminPanelUsuariosFragment : Fragment(R.layout.fragment_admin_panel_usuarios) {

    private val REQUEST_CODE_SPEECH_INPUT = 1

    private val viewModel: AdminPanelUsuariosViewModel by viewModels()
    private lateinit var binding: FragmentAdminPanelUsuariosBinding
    private val adapterAdminPanelUsuarios = AdapterAdminPanelUsuarios(){ usuario -> viewModel.navigateTo(usuario)}
    private lateinit var usuarios: List<Usuario>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAdminPanelUsuariosBinding.bind(view).apply {
            recyclerAdminUsuarios.adapter = adapterAdminPanelUsuarios
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Panel de Administrador (Usuarios)"

        binding.searchImageView.setOnClickListener {
            filter(binding.searchEditText.text.toString())
        }

        binding.voiceSearchQuery.setOnClickListener {
            val permission = ContextCompat.checkSelfPermission(this.requireContext(),
                Manifest.permission.RECORD_AUDIO)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                makeRequest()
            } else {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                )

                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)

                } catch (e: Exception) {
                    Toast
                        .makeText(
                            context, " " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            }
        }

        viewModel.state.observe(viewLifecycleOwner){state ->
            binding.progressAdminUsuarios.visibility =  if (state.loading) VISIBLE else GONE
            state.usuarios?.let {
                usuarios = state.usuarios
                adapterAdminPanelUsuarios.usuarios = state.usuarios
                adapterAdminPanelUsuarios.notifyDataSetChanged()
            }
        }

        binding.adminUsuariosToListasButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_adminPanelUsuariosFragment_to_adminPanelListasFragment)
        }

        binding.adminUsuariosToItemsButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_adminPanelUsuariosFragment_to_adminPanelItemsFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AdminPanelUsuariosFragment", "onDestroy")
    }

    //La función filter nos permite filtrar los usuarios del administrador
    private fun filter(text: String) {
        val filteredlist: ArrayList<Usuario> = ArrayList()

        for (usuario in usuarios) {
            if (usuario.email.lowercase().contains(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(usuario)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No se ha encontrado ningún usuario", Toast.LENGTH_SHORT).show()
        } else {
            adapterAdminPanelUsuarios.usuarios = filteredlist
            adapterAdminPanelUsuarios.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
                binding.searchEditText.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }

    //La función makeRequest pide al usuario permiso para utilizar el micrófono
    private fun makeRequest() {
        this.activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE_SPEECH_INPUT)
        }
    }
}
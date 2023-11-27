package com.nicorodgon.listapp.ui.adminPanelListas

import android.Manifest
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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentAdminPanelListasBinding
import com.nicorodgon.listapp.model.Lista
import com.nicorodgon.listapp.ui.item.ItemFragment
import java.util.Locale
import java.util.Objects

class AdminPanelListasFragment : Fragment(R.layout.fragment_admin_panel_listas) {

    private val REQUEST_CODE_SPEECH_INPUT = 1

    private val viewModel: AdminPanelListasViewModel by viewModels()
    private lateinit var binding: FragmentAdminPanelListasBinding
    private val adapterAdminPanelListas = AdapterAdminPanelListas(){ lista -> viewModel.navigateTo(lista)}
    private lateinit var listas: List<Lista>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAdminPanelListasBinding.bind(view).apply {
            recyclerAdminListas.adapter = adapterAdminPanelListas
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Panel de Administrador (Listas)"

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
            binding.progressAdminListas.visibility =  if (state.loading) VISIBLE else GONE
            state.listas?.let {
                listas = state.listas
                adapterAdminPanelListas.listas = state.listas
                adapterAdminPanelListas.notifyDataSetChanged()
            }

            state.navigateTo?.let {
                findNavController().navigate(
                    R.id.action_adminPanelListasFragment_to_adminItemFragment,
                    bundleOf(ItemFragment.EXTRA_LISTA to it)
                )
                viewModel.onNavigateDone()
            }
        }

        binding.adminListasToUsuariosButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_adminPanelListasFragment_to_adminPanelUsuariosFragment)
        }

        binding.adminListasToItemsButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_adminPanelListasFragment_to_adminPanelItemsFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AdminPanelListasFragment", "onDestroy")
    }

    //La función filter nos permite filtrar las listas del administrador
    private fun filter(text: String) {
        val filteredlist: ArrayList<Lista> = ArrayList()

        for (lista in listas) {
            if (lista.nombre_lista.lowercase().contains(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(lista)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No se ha encontrado ninguna lista", Toast.LENGTH_SHORT).show()
        } else {
            adapterAdminPanelListas.listas = filteredlist
            adapterAdminPanelListas.notifyDataSetChanged()
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
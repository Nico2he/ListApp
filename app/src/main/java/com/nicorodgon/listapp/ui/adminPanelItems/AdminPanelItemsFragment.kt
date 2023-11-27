package com.nicorodgon.listapp.ui.adminPanelItems

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
import com.nicorodgon.listapp.databinding.FragmentAdminPanelItemsBinding
import com.nicorodgon.listapp.model.ItemLista
import com.nicorodgon.listapp.ui.adminItemDetail.AdminItemDetailFragment
import java.util.Locale
import java.util.Objects

class AdminPanelItemsFragment : Fragment(R.layout.fragment_admin_panel_items) {

    private val REQUEST_CODE_SPEECH_INPUT = 1

    private val viewModel: AdminPanelItemsViewModel by viewModels()
    private lateinit var binding: FragmentAdminPanelItemsBinding
    private val adapterAdminPanelItems = AdapterAdminPanelItems(){ item -> viewModel.navigateTo(item)}
    private lateinit var items: List<ItemLista>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAdminPanelItemsBinding.bind(view).apply {
            recyclerAdminItems.adapter = adapterAdminPanelItems
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Panel de Administrador (Ítems)"

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
            binding.progressAdminItems.visibility =  if (state.loading) VISIBLE else GONE
            state.items?.let {
                items = state.items
                adapterAdminPanelItems.items = state.items
                adapterAdminPanelItems.notifyDataSetChanged()
            }

            state.navigateTo?.let {
                findNavController().navigate(
                    R.id.action_adminPanelItemsFragment_to_adminItemDetailFragment,
                    bundleOf(AdminItemDetailFragment.EXTRA_ITEM to it)
                )
                viewModel.onNavigateDone()
            }
        }

        binding.adminItemsToListasButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_adminPanelItemsFragment_to_adminPanelListasFragment)
        }

        binding.adminItemsToUsuariosButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_adminPanelItemsFragment_to_adminPanelUsuariosFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AdminPanelItemsFragment", "onDestroy")
    }

    //La función filter nos permite filtrar los ítems del administrador
    private fun filter(text: String) {
        val filteredlist: ArrayList<ItemLista> = ArrayList()

        for (item in items) {
            if (item.nombre_item.lowercase().contains(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No se ha encontrado ningún ítem", Toast.LENGTH_SHORT).show()
        } else {
            adapterAdminPanelItems.items = filteredlist
            adapterAdminPanelItems.notifyDataSetChanged()
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
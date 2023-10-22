package com.nicorodgon.listapp.ui.userPanel

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentUserPanelBinding
import com.nicorodgon.listapp.model.Lista
import com.nicorodgon.listapp.ui.item.ItemFragment
import java.util.Locale
import java.util.Objects

class UserPanelFragment : Fragment(R.layout.fragment_user_panel) {

    private val REQUEST_CODE_SPEECH_INPUT = 1

    private val viewModel: UserPanelViewModel by viewModels()
    private lateinit var binding: FragmentUserPanelBinding
    private val adapterUserPanel = AdapterUserPanel(){ lista -> viewModel.navigateTo(lista)}
    private lateinit var listas: List<Lista>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserPanelBinding.bind(view).apply {
            recycler.adapter = adapterUserPanel
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)

        binding.searchImageView.setOnClickListener {
            filter(binding.searchEditText.text.toString())
        }

        binding.voiceSearchQuery.setOnClickListener {
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

        viewModel.state.observe(viewLifecycleOwner){state ->
            binding.progress.visibility =  if (state.loading) VISIBLE else GONE
            state.listas?.let {
                listas = state.listas
                adapterUserPanel.listas = state.listas
                adapterUserPanel.notifyDataSetChanged()
            }

            state.navigateTo?.let {
                findNavController().navigate(
                    R.id.action_userPanelFragment_to_itemFragment,
                    bundleOf(ItemFragment.EXTRA_LISTA to it)
                )
                viewModel.onNavigateDone()
            }

        }

        binding.trashButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_userPanelFragment_to_trashFragment)
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_userPanelFragment_to_createListaFragment)
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("UserPanelFragment", "onDestroy")
    }

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
            adapterUserPanel.listas = filteredlist
            adapterUserPanel.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
                binding.searchEditText.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }
}
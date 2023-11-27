package com.nicorodgon.listapp.ui.sharedTrash

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentSharedTrashBinding

class SharedTrashFragment : Fragment(R.layout.fragment_shared_trash) {

    private val viewModel: SharedTrashViewModel by viewModels()
    private lateinit var binding: FragmentSharedTrashBinding
    private val email = FirebaseAuth.getInstance().currentUser?.email
    private val adapterSharedTrash = AdapterSharedTrash(){ lista -> viewModel.navigateTo(lista)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSharedTrashBinding.bind(view).apply {
            recyclerSharedTrash.adapter = adapterSharedTrash
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)

        viewModel.state.observe(viewLifecycleOwner){state ->
            binding.progressSharedTrash.visibility =  if (state.loading) VISIBLE else GONE
            state.listas?.let {
                adapterSharedTrash.listas = state.listas
                adapterSharedTrash.notifyDataSetChanged()
            }
        }

        binding.emptySharedTrashButton.setOnClickListener {
            if (email != null) {
                AlertDialog.Builder(this.context)
                    .setTitle("¿Desea vaciar la papelera?")
                    .setMessage("Todas las listas compartidas desaparecerán permanentemente \nEsta acción no se puede revertir")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.emptySharedTrash()
                    }
                    .setNegativeButton("No"){_,_ ->}
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SharedTrashFragment", "onDestroy")
    }
}
package com.nicorodgon.listapp.ui.trash

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
import com.nicorodgon.listapp.databinding.FragmentTrashBinding

class TrashFragment : Fragment(R.layout.fragment_trash) {

    private val viewModel: TrashViewModel by viewModels()
    private lateinit var binding: FragmentTrashBinding
    private val email = FirebaseAuth.getInstance().currentUser?.email
    private val adapterTrash = AdapterTrash(){ lista -> viewModel.navigateTo(lista)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTrashBinding.bind(view).apply {
            recycler.adapter = adapterTrash
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)

        viewModel.state.observe(viewLifecycleOwner){state ->
            binding.progress.visibility =  if (state.loading) VISIBLE else GONE
            state.listas?.let {
                adapterTrash.listas = state.listas
                adapterTrash.notifyDataSetChanged()
            }
        }

        binding.emptyTrashButton.setOnClickListener {
            if (email != null) {
                AlertDialog.Builder(this.context)
                    .setTitle("¿Desea vaciar la papelera?")
                    .setMessage("Todas las listas y sus ítems desaparecerán permanentemente \nEsta acción no se puede revertir")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.emptyTrash()
                    }
                    .setNegativeButton("No"){_,_ ->}
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TrashFragment", "onDestroy")
    }
}
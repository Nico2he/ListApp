package com.nicorodgon.listapp.ui.trash

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentTrashBinding

class TrashFragment : Fragment(R.layout.fragment_trash) {

    private val viewModel: TrashViewModel by viewModels()
    private lateinit var binding: FragmentTrashBinding
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
            viewModel.emptyTrash()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("UserPanelFragment", "onDestroy")
    }
}
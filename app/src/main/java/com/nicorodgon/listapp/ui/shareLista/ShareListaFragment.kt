package com.nicorodgon.listapp.ui.shareLista

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentShareListaBinding
import com.nicorodgon.listapp.model.Lista

class ShareListaFragment : Fragment(R.layout.fragment_share_lista) {

    private val viewModel: ShareListaViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShareListaBinding.bind(view)
        val lista = arguments?.getParcelable<Lista>("lista")!!

        binding.shareListaUsuarioButton.setOnClickListener {
            //Creamos una lista compartida
            val lista = Lista(
                    lista.creador,
                    lista.nombre_lista,
                    lista.imagen_lista,
                    lista.habilitado,
                    binding.editTextEmail.text.toString()
                )
            if (binding.editTextEmail.text.toString() == "listapp2023.2024@gmail.com") {
                binding.verifyListaShared.text = "No se puede compartir una lista con ese usuario"
            } else if (lista.creador == binding.editTextEmail.text.toString()) {
                binding.verifyListaShared.text = "No puedes compartir una lista contigo mismo"
            } else {
                viewModel.createListaCompartida(lista, binding.verifyListaShared)
                AlertDialog.Builder(this.context)
                    .setTitle("¿Enviar correo?")
                    .setMessage("¿Le gustaría informar al usuario con un correo electrónico?")
                    .setPositiveButton("Sí") { _, _ ->
                        sendEmail(binding.editTextEmail.text.toString(), lista.nombre_lista)
                    }
                    .setNegativeButton("No"){_,_ ->}
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ShareListaFragment", "onDestroy")
    }

    //La función sendEmail abre la aplicación del usuario para enviar correos, con un mensaje predefinido, hacia el usuario con quien se comparte la lista
    private fun sendEmail(recipient: String, nombreLista: String) {
        val mIntent = Intent(Intent.ACTION_SENDTO)
        mIntent.data = Uri.parse("mailto:")
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "Lista Compartida")
        mIntent.putExtra(Intent.EXTRA_TEXT,
            "Buenos días.\n\nTe he compartido mi lista '$nombreLista' en ListApp.\n\nÉchale un vistazo."
        )

        try {
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        } catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
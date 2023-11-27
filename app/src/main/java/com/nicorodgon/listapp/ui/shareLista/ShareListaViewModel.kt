package com.nicorodgon.listapp.ui.shareLista

import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShareListaViewModel: ViewModel() {

    //La función createListaCompartida crea una lista compartida
    fun createListaCompartida(lista: Lista, verifyListaShared: TextView){
        viewModelScope.launch(Dispatchers.IO) {
            DbFirestore.createListaCompartida(lista, verifyListaShared)
        }
    }
}
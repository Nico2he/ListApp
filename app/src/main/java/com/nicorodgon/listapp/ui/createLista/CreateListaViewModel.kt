package com.nicorodgon.listapp.ui.createLista

import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateListaViewModel: ViewModel() {

    fun createLista(lista: Lista, verifyListaCreated: TextView){
        viewModelScope.launch(Dispatchers.IO) {
            DbFirestore.createLista(lista, verifyListaCreated)
        }
    }
}
package com.nicorodgon.listapp.ui.createLista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateListaViewModel: ViewModel() {

    fun createLista(lista: Lista){
        viewModelScope.launch(Dispatchers.IO) {
            DbFirestore.createLista(lista)
        }
    }
}
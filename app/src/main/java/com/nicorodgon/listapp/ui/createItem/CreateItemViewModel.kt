package com.nicorodgon.listapp.ui.createItem

import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.ItemLista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateItemViewModel : ViewModel() {

    //La función createItem crea el ítem pasado por parámetro
    fun createItem(item: ItemLista, verifyItemCreated: TextView){
        viewModelScope.launch(Dispatchers.IO) {
            DbFirestore.createItem(item, verifyItemCreated)
        }
    }
}
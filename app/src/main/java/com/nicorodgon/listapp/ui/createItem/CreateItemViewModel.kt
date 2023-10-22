package com.nicorodgon.listapp.ui.createItem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.ItemLista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateItemViewModel : ViewModel() {

    fun createItem(item: ItemLista){
        viewModelScope.launch(Dispatchers.IO) {
            DbFirestore.createItem(item)
        }

    }

}
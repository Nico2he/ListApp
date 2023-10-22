package com.nicorodgon.listapp.ui.itemDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nicorodgon.listapp.model.ItemLista

class ItemDetailViewModel(item: ItemLista): ViewModel() {
    private val _item = MutableLiveData(item)
    val item: LiveData<ItemLista> get() = _item

}

@Suppress("UNCHECKED_CAST")
class ItemDetailViewModelFactory(private val item: ItemLista): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ItemDetailViewModel(item) as T
    }

}
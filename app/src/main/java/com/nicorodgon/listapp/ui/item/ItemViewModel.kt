package com.nicorodgon.listapp.ui.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.ItemLista
import com.nicorodgon.listapp.model.Lista
import com.nicorodgon.listapp.ui.userPanel.UserPanelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemViewModel(lista: Lista): ViewModel() {
    private val _state = MutableLiveData(UserPanelViewModel.UiState())
    val state: LiveData<UserPanelViewModel.UiState> get() = _state
    private val _lista = MutableLiveData(lista)
    val lista: LiveData<Lista> get() = _lista

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            DbFirestore.getAllObservableItemsEnabled(lista.nombre_lista).observeForever {
                _state.value = _state.value?.copy(loading = false, items = it)
            }
        }

    }

    private suspend fun requestItems(): List<ItemLista>  = DbFirestore.getAllItems()

    fun navigateToItem(item: ItemLista) {
        _state.value = _state.value?.copy(navigateToItem = item)
    }

    fun onNavigateDone(){
        _state.value = _state.value?.copy(navigateToItem = null)
    }

}

@Suppress("UNCHECKED_CAST")
class ItemViewModelFactory(private val lista: Lista): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ItemViewModel(lista) as T
    }

}
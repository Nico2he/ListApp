package com.nicorodgon.listapp.ui.userPanel

import androidx.lifecycle.*
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.ItemLista
import com.nicorodgon.listapp.model.Lista
import kotlinx.coroutines.*

class UserPanelViewModel : ViewModel() {
    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> get() = _state

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            DbFirestore.getAllObservableListasEnabled().observeForever {
                _state.value = _state.value?.copy(loading = false, listas = it)
            }
        }

    }

    private suspend fun requestListas(): List<Lista>  = DbFirestore.getAllListas()

    fun navigateTo(lista: Lista) {
        _state.value = _state.value?.copy(navigateTo = lista)
    }

    fun navigateToItem(item: ItemLista) {
        _state.value = _state.value?.copy(navigateToItem = item)
    }

    fun onNavigateDone(){
        _state.value = _state.value?.copy(navigateTo = null)
    }

    data class UiState(
        val loading: Boolean = false,
        val listas: List<Lista>? = null,
        val items: List<ItemLista>? = null,
        val navigateTo: Lista? = null,
        val navigateToItem: ItemLista? = null,
        val navigateToCreate: Boolean = false
    )

}
package com.nicorodgon.listapp.ui.adminPanelItems

import androidx.lifecycle.*
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.ItemLista
import kotlinx.coroutines.*

class AdminPanelItemsViewModel : ViewModel() {

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> get() = _state

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            DbFirestore.getAllObservableItems().observeForever {
                _state.value = _state.value?.copy(loading = false, items = it)
            }
        }
    }

    fun navigateTo(item: ItemLista) {
        _state.value = _state.value?.copy(navigateTo = item)
    }

    fun onNavigateDone(){
        _state.value = _state.value?.copy(navigateTo = null)
    }

    data class UiState(
        val loading: Boolean = false,
        val items: List<ItemLista>? = null,
        val navigateTo: ItemLista? = null,
        val navigateToCreate: Boolean = false
    )
}
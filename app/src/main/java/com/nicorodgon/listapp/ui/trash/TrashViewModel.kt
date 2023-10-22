package com.nicorodgon.listapp.ui.trash

import androidx.lifecycle.*
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista
import kotlinx.coroutines.*

class TrashViewModel : ViewModel() {
    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> get() = _state

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            DbFirestore.getAllObservableListasDisabled().observeForever {
                _state.value = _state.value?.copy(loading = false, listas = it)
            }
        }

    }

    private suspend fun requestListas(): List<Lista>  = DbFirestore.getAllListas()

    fun navigateTo(lista: Lista) {
        _state.value = _state.value?.copy(navigateTo = lista)
    }

    fun onNavigateDone(){
        _state.value = _state.value?.copy(navigateTo = null)
    }

    data class UiState(
        val loading: Boolean = false,
        val listas: List<Lista>? = null,
        val navigateTo: Lista? = null,
        val navigateToCreate: Boolean = false
    )

    fun emptyTrash() {
        viewModelScope.launch(Dispatchers.IO) {
            DbFirestore.emptyTrash()
        }
    }

}
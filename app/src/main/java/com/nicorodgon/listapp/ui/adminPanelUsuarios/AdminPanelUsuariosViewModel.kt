package com.nicorodgon.listapp.ui.adminPanelUsuarios

import androidx.lifecycle.*
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.ItemLista
import com.nicorodgon.listapp.model.Usuario
import kotlinx.coroutines.*

class AdminPanelUsuariosViewModel : ViewModel() {

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> get() = _state

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            DbFirestore.getAllObservableUsuarios().observeForever {
                _state.value = _state.value?.copy(loading = false, usuarios = it)
            }
        }
    }

    fun navigateTo(usuario: Usuario) {
        _state.value = _state.value?.copy(navigateTo = usuario)
    }

    fun onNavigateDone(){
        _state.value = _state.value?.copy(navigateTo = null)
    }

    data class UiState(
        val loading: Boolean = false,
        val usuarios: List<Usuario>? = null,
        val items: List<ItemLista>? = null,
        val navigateTo: Usuario? = null,
        val navigateToCreate: Boolean = false
    )
}
package com.nicorodgon.listapp.ui.userPanel

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.ItemLista
import com.nicorodgon.listapp.model.Lista
import com.nicorodgon.listapp.model.Usuario
import kotlinx.coroutines.*

class UserPanelViewModel : ViewModel() {

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> get() = _state
    private val email = FirebaseAuth.getInstance().currentUser?.email

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            if (email != null) {
                DbFirestore.getAllObservableListasEnabled(email).observeForever {
                    _state.value = _state.value?.copy(loading = false, listas = it)
                }
            }
        }
    }

    //La función createUsuario añade al usuario pasado por parámetro a la base de datos
    fun createUsuario(usuario: Usuario) {
        DbFirestore.createUsuario(usuario)
    }

    fun navigateTo(lista: Lista) {
        _state.value = _state.value?.copy(navigateTo = lista)
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
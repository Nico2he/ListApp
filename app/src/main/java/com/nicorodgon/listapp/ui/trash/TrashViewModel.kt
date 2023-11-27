package com.nicorodgon.listapp.ui.trash

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.nicorodgon.listapp.model.DbFirestore
import com.nicorodgon.listapp.model.Lista
import kotlinx.coroutines.*

class TrashViewModel : ViewModel() {

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> get() = _state
    private val email = FirebaseAuth.getInstance().currentUser?.email

    init {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value?.copy(loading = true)
            if (email != null) {
                DbFirestore.getAllObservableListasDisabled(email).observeForever {
                    _state.value = _state.value?.copy(loading = false, listas = it)
                }
            }
        }
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
        val navigateTo: Lista? = null,
        val navigateToCreate: Boolean = false
    )

    //La función emptyTrash elimina las listas del usuario que se encuentran en la papelera
    fun emptyTrash() {
        viewModelScope.launch(Dispatchers.IO) {
            if (email != null) {
                DbFirestore.emptyTrash(email)
            }
        }
    }
}
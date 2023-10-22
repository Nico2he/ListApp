package com.nicorodgon.listapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DbFirestore {

    private const val COLLECTION_LISTAS = "listas"
    private const val COLLECTION_ITEMS = "items"

    suspend fun getAllListas(): List<Lista> {
        val snapshot = FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .get()
            .await()
        val listas = mutableListOf<Lista>()
        for (documentSnapshot in snapshot){
            val lista = documentSnapshot.toObject(Lista::class.java)
            listas.add(lista)
        }
        return listas
    }

    suspend fun getAllItems(): List<ItemLista> {
        val snapshot = FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
            .get()
            .await()
        val items = mutableListOf<ItemLista>()
        for (documentSnapshot in snapshot){
            val item = documentSnapshot.toObject(ItemLista::class.java)
            items.add(item)
        }
        return items
    }

    private suspend fun getItemMaxOrden(nombreListaPadre: String): Int {
        val snapshot = FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
            .whereEqualTo("fecha_baja", "")
            .whereEqualTo("nombre_lista_item", nombreListaPadre)
            .orderBy("orden", Query.Direction.DESCENDING).limit(1)
            .get()
            .await()
        val items = mutableListOf<ItemLista>()
        var orden = 0
        for (documentSnapshot in snapshot){
            val item = documentSnapshot.toObject(ItemLista::class.java)
            items.add(item)
        }
        if (items.size == 1) {
            orden = items[0].orden
        }
        return orden

    }

    fun createLista(lista: Lista){
        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .add(lista)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d(COLLECTION_LISTAS, it.result.id)
                }
            }
            .addOnFailureListener {
                Log.e(COLLECTION_LISTAS, it.toString())
            }

    }

    suspend fun createItem(item: ItemLista){
        item.orden = getItemMaxOrden(item.nombre_lista_item) + 1
        FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
            .add(item)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d(COLLECTION_LISTAS, it.result.id)
                }
            }
            .addOnFailureListener {
                Log.e(COLLECTION_LISTAS, it.toString())
            }

    }

    suspend fun emptyTrash() {
        val snapshot = FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("habilitado", false)
            .get()
            .await()
        for (documentSnapshot in snapshot){
            val lista = documentSnapshot.toObject(Lista::class.java)
            FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .document(documentSnapshot.id)
                .delete()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Log.d(COLLECTION_LISTAS, documentSnapshot.id)
                    }
                }
                .addOnFailureListener {
                    Log.e(COLLECTION_LISTAS, it.toString())
                }
            val snapshotItems = FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                .whereEqualTo("nombre_lista_item", lista.nombre_lista)
                .get()
                .await()
            for (documentSnapshotItems in snapshotItems) {
                val calendar = Calendar.getInstance().time
                val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val dateString = date.format(calendar)
                FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                    .document(documentSnapshotItems.id)
                    .update("fecha_baja", dateString)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(COLLECTION_LISTAS, documentSnapshot.id)
                        }
                    }
                    .addOnFailureListener {
                        Log.e(COLLECTION_LISTAS, it.toString())
                    }
            }
        }
    }

    fun disableLista(lista: Lista?) {
        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("nombre_lista", lista?.nombre_lista)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                        .document(id)
                        .update("habilitado", false)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(COLLECTION_LISTAS, id)
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_LISTAS, it.toString())
                        }
                }
            }
    }

    fun enableLista(lista: Lista?) {
        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("nombre_lista", lista?.nombre_lista)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                        .document(id)
                        .update("habilitado", true)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(COLLECTION_LISTAS, id)
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_LISTAS, it.toString())
                        }
                }
            }
    }

    fun disableItem(item: ItemLista?) {
        FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
            .whereEqualTo("nombre_item", item?.nombre_item)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    val calendar = Calendar.getInstance().time
                    val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val dateString = date.format(calendar)
                    FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                        .document(id)
                        .update("fecha_baja", dateString)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(COLLECTION_ITEMS, id)
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_ITEMS, it.toString())
                        }
                }
            }
    }

    suspend fun getAllObservableListasEnabled(): LiveData<MutableList<Lista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<Lista>>()
            FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .whereEqualTo("habilitado", true)
                .addSnapshotListener { snapshot, _ ->
                    var listas = mutableListOf<Lista>()
                    if (snapshot != null) {
                        listas = snapshot.toObjects(Lista::class.java)
                    }
                    mutableData.value = listas
                }

            return@withContext mutableData
        }
    }

    suspend fun getAllObservableItemsEnabled(nombreListaPadre: String): LiveData<MutableList<ItemLista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<ItemLista>>()
            FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                .orderBy("orden", Query.Direction.DESCENDING)
                .whereEqualTo("fecha_baja", "")
                .whereEqualTo("nombre_lista_item", nombreListaPadre)
                .addSnapshotListener { snapshot, _ ->
                    var items = mutableListOf<ItemLista>()
                    if (snapshot != null) {
                        items = snapshot.toObjects(ItemLista::class.java)
                    }
                    mutableData.value = items
                }

            return@withContext mutableData
        }
    }

    suspend fun getAllObservableListasDisabled(): LiveData<MutableList<Lista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<Lista>>()
            FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .whereEqualTo("habilitado", false)
                .addSnapshotListener { snapshot, _ ->
                    var listas = mutableListOf<Lista>()
                    if (snapshot != null) {
                        listas = snapshot.toObjects(Lista::class.java)
                    }
                    mutableData.value = listas
                }

            return@withContext mutableData
        }
    }

}
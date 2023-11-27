package com.nicorodgon.listapp.model

import android.util.Log
import android.widget.TextView
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

//Clase que contiene todas las funciones relacionadas con consultas a la base de datos
object DbFirestore {

    private const val COLLECTION_LISTAS = "listas"
    private const val COLLECTION_ITEMS = "items"
    private const val COLLECTION_USUARIOS = "usuarios"

    //La función getItemMaxOrden devuelve el mayor número de orden de los ítems de una lista y usuario específicos
    private suspend fun getItemMaxOrden(nombreListaPadre: String, creadorListaItem: String): Int {
        val snapshot = FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
            .whereEqualTo("fecha_baja", "")
            .whereEqualTo("creador_lista_item", creadorListaItem)
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

    //La función createLista crea una lista asociada a un usuario específico a menos de que ya exista.
    fun createLista(lista: Lista, verifyListaCreated: TextView){
        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("creador", lista.creador)
            .whereEqualTo("nombre_lista", lista.nombre_lista)
            .whereEqualTo("usuario_compartido", "")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.isEmpty) {
                        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                            .add(lista)
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    verifyListaCreated.text = "Se ha creado la lista correctamente"
                                    Log.e(COLLECTION_LISTAS, "Se ha creado la lista correctamente")
                                }
                            }
                            .addOnFailureListener {
                                verifyListaCreated.text = "Error al intentar crear la lista en la base de datos"
                                Log.e(COLLECTION_LISTAS, "Error al intentar crear la lista en la base de datos")
                            }
                    } else {
                        verifyListaCreated.text = "Una lista con el mismo nombre ya existe para este usuario"
                        Log.e(COLLECTION_LISTAS, "Una lista con el mismo nombre ya existe para este usuario")
                    }
                }
            }
            .addOnFailureListener {
                verifyListaCreated.text = "Error al obtener las listas"
                Log.e(COLLECTION_LISTAS, "Error al obtener las listas")
            }
    }

    //La función createListaCompartida crea una lista que se ha compartido con un usuario existente de la aplicación.
    //Si el usuario ya tiene esa misma lista compartida entonces no se crea.
    fun createListaCompartida(lista: Lista, verifyListaShared: TextView){
        FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
            .whereEqualTo("email", lista.usuario_compartido)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (!it.result.isEmpty) {
                        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                            .whereEqualTo("creador", lista.creador)
                            .whereEqualTo("nombre_lista", lista.nombre_lista)
                            .whereEqualTo("usuario_compartido", lista.usuario_compartido)
                            .get()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    if (it.result.isEmpty) {
                                        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                                            .add(lista)
                                            .addOnCompleteListener {
                                                if (it.isSuccessful){
                                                    verifyListaShared.text = "Se ha compartido la lista con el usuario"
                                                    Log.e(COLLECTION_LISTAS, "Se ha compartido la lista con el usuario")
                                                }
                                            }
                                            .addOnFailureListener {
                                                verifyListaShared.text = "No se ha podido compartir la lista con el usuario"
                                                Log.e(COLLECTION_LISTAS, "No se ha podido compartir la lista con el usuario")
                                            }
                                    } else {
                                        verifyListaShared.text = "Ya has compartido esta lista con el usuario"
                                        Log.e(COLLECTION_LISTAS, "Ya has compartido esta lista con el usuario")
                                    }
                                }
                            }
                            .addOnFailureListener {
                                verifyListaShared.text = "Error al obtener las listas compartidas"
                                Log.e(COLLECTION_LISTAS, "Error al obtener las listas compartidas")
                            }
                    } else {
                        verifyListaShared.text = "El usuario no está registrado en ListApp"
                        Log.e(COLLECTION_USUARIOS, "El usuario no está registrado en ListApp")
                    }
                }
            }
            .addOnFailureListener {
                verifyListaShared.text = "Error al obtener los usuarios"
                Log.e(COLLECTION_USUARIOS, "Error al obtener los usuarios")
            }

    }

    //La función createItem crea un ítem asociado a una lista de un usuario específico a menos de que ya exista.
    suspend fun createItem(item: ItemLista, verifyItemCreated: TextView){
        item.orden = getItemMaxOrden(item.nombre_lista_item, item.creador_lista_item) + 1
        FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
            .whereEqualTo("nombre_item", item.nombre_item)
            .whereEqualTo("creador_lista_item", item.creador_lista_item)
            .whereEqualTo("nombre_lista_item", item.nombre_lista_item)
            .whereEqualTo("fecha_baja", "")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.isEmpty) {
                        FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                            .add(item)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    verifyItemCreated.text = "El ítem se ha creado con éxito"
                                    Log.d(COLLECTION_LISTAS, "El ítem se ha creado con éxito")
                                }
                            }
                            .addOnFailureListener {
                                verifyItemCreated.text = "Error al intentar crear el ítem en la base de datos"
                                Log.e(COLLECTION_LISTAS, "Error al intentar crear el ítem en la base de datos"
                                )
                            }
                    } else {
                        verifyItemCreated.text = "Un ítem con el mismo nombre ya existe en esta lista"
                        Log.e(COLLECTION_LISTAS, "Un ítem con el mismo nombre ya existe en esta lista")
                    }
                }
            }
            .addOnFailureListener {
                verifyItemCreated.text = "Error al obtener los ítems"
                Log.e(COLLECTION_LISTAS, "Error al obtener los ítems")
            }
    }

    //La función createUsuario comprueba si existe el email del usuario pasado por parámetro en la base de datos.
    //Si no existe lo crea.
    fun createUsuario(usuario: Usuario){
        FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
            .whereEqualTo("email", usuario.email)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val document = it.result
                    if (document.documents.isNotEmpty()) {
                        Log.d(COLLECTION_USUARIOS, "El usuario ya existe")
                    } else {
                        FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
                            .add(usuario)
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    Log.d(COLLECTION_USUARIOS, "Se ha creado el usuario con éxito")
                                } else {
                                    Log.d(COLLECTION_USUARIOS, "No se ha podido crear el usuario")
                                }
                            }
                    }
                } else {
                    Log.e(COLLECTION_USUARIOS, "No se han podido obtener los usuarios")
                }
            }
    }

    //La función emptyTrash elimina las listas deshabilitadas de un usuario específico.
    //Además deshabilita todos los ítems que estas listas pueden contener.
    suspend fun emptyTrash(usuario: String) {
        val snapshot = FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("creador", usuario)
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
                .whereEqualTo("creador_lista_item", lista.creador)
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
            val snapshotListas = FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .whereEqualTo("creador", lista.creador)
                .whereEqualTo("nombre_lista", lista.nombre_lista)
                .get()
                .await()
            for (documentSnapshotListas in snapshotListas) {
                FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                    .document(documentSnapshotListas.id)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Log.d(COLLECTION_LISTAS, documentSnapshot.id)
                        }
                    }
                    .addOnFailureListener {
                        Log.e(COLLECTION_LISTAS, it.toString())
                    }
            }
        }
    }

    //La función emptySharedTrash elimina las listas deshabilitadas compartidas con un usuario específico.
    suspend fun emptySharedTrash(usuario: String) {
        val snapshot = FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("habilitado", false)
            .whereEqualTo("usuario_compartido", usuario)
            .get()
            .await()
        for (documentSnapshot in snapshot) {
            FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .document(documentSnapshot.id)
                .delete()
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

    //La función disableLista da de baja a una lista creada de un usuario específico a partir del nombre.
    fun disableLista(usuario: String, lista: Lista?) {
        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("creador", usuario)
            .whereEqualTo("nombre_lista", lista?.nombre_lista)
            .whereEqualTo("usuario_compartido", "")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                        .document(id)
                        .update("habilitado", false)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(COLLECTION_LISTAS, "Se ha deshabilitado la lista")
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_LISTAS, "No se ha podido deshabilitar la lista")
                        }
                }
            }
    }

    //La función disableSharedLista da de baja a una lista compartida con un usuario específico a partir del nombre.
    fun disableSharedLista(usuario: String, lista: Lista?) {
        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("creador", lista?.creador)
            .whereEqualTo("nombre_lista", lista?.nombre_lista)
            .whereEqualTo("usuario_compartido", usuario)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                        .document(id)
                        .update("habilitado", false)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(COLLECTION_LISTAS, "Se ha deshabilitado la lista compartida")
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_LISTAS, "No se ha podido deshabilitar la lista compartida")
                        }
                }
            }
    }

    //La función enableLista da de alta a una lista creada de un usuario específico a partir del nombre.
    fun enableLista(usuario: String, lista: Lista?) {
        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("creador", usuario)
            .whereEqualTo("nombre_lista", lista?.nombre_lista)
            .whereEqualTo("usuario_compartido", "")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                        .document(id)
                        .update("habilitado", true)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(COLLECTION_LISTAS, "Se ha habilitado la lista")
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_LISTAS, "No se ha podido habilitar la lista")
                        }
                }
            }
    }

    //La función enableSharedLista da de alta a una lista compartida con un usuario específico a partir del nombre.
    fun enableSharedLista(usuario: String, lista: Lista?) {
        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("creador", lista?.creador)
            .whereEqualTo("nombre_lista", lista?.nombre_lista)
            .whereEqualTo("usuario_compartido", usuario)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                        .document(id)
                        .update("habilitado", true)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(COLLECTION_LISTAS, "Se ha habilitado la lista compartida")
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_LISTAS, "No se ha podido habilitar la lista compartida")
                        }
                }
            }
    }

    //La función disableItem da de baja a un ítem perteneciente a una lista y usuario específicos a partir del nombre.
    //Al dar de baja el ítem se le añade como fecha de baja la fecha en la que ha sido deshabilitado.
    fun disableItem(item: ItemLista?) {
        if (item != null) {
            FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                .whereEqualTo("creador_lista_item", item.creador_lista_item)
                .whereEqualTo("nombre_lista_item", item.nombre_lista_item)
                .whereEqualTo("nombre_item", item.nombre_item)
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
    }

    //La función deleteItemAdmin elimina permanentemente una lista de la base de datos.
    //(Administrador).
    fun deleteListaAdmin(lista: Lista) {
        FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
            .whereEqualTo("creador", lista.creador)
            .whereEqualTo("nombre_lista", lista.nombre_lista)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                        .document(id)
                        .delete()
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(COLLECTION_LISTAS, "El administrador ha eliminado la lista correctamente")
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_LISTAS, "El administrador no ha podido eliminar la lista correctamente")
                        }
                }
            }
    }

    //La función deleteItemAdmin elimina permanentemente un usuario de la base de datos.
    //(Administrador).
    fun deleteUsuarioAdmin(usuario: Usuario) {
        FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
            .whereEqualTo("email", usuario.email)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
                        .document(id)
                        .delete()
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(COLLECTION_USUARIOS, "El administrador ha eliminado al usuario correctamente")
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_USUARIOS, "El administrador no ha podido eliminar al usuario correctamente")
                        }
                }
            }
    }

    //La función deleteItemAdmin elimina permanentemente un ítem de la base de datos.
    //(Administrador).
    fun deleteItemAdmin(item: ItemLista) {
        FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
            .whereEqualTo("nombre_item", item.nombre_item)
            .whereEqualTo("creador_lista_item", item.creador_lista_item)
            .whereEqualTo("nombre_lista_item", item.nombre_lista_item)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                        .document(id)
                        .delete()
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d(COLLECTION_ITEMS, "El administrador ha eliminado el ítem correctamente")
                            }
                        }
                        .addOnFailureListener {
                            Log.e(COLLECTION_ITEMS, "El administrador no ha podido eliminar el ítem correctamente")
                        }
                }
            }
    }

    //La función getAllObservableListasEnabled devuelve una lista de observables de listas habilitadas.
    //Estas listas son devueltas en función del usuario pasado por parámetro, recuperando así las listas creadas
    //por este.
    suspend fun getAllObservableListasEnabled(usuario: String): LiveData<MutableList<Lista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<Lista>>()
            var listas = mutableListOf<Lista>()
            FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .whereEqualTo("creador", usuario)
                .whereEqualTo("habilitado", true)
                .whereEqualTo("usuario_compartido", "")
                .addSnapshotListener { snapshot, _ ->
                    Log.d(COLLECTION_LISTAS, "Se han recuperado las listas observables habilitadas creadas por el usuario")
                    if (snapshot != null) {
                        listas = snapshot.toObjects(Lista::class.java)
                    }
                    mutableData.value = listas
                }
            return@withContext mutableData
        }
    }

    //La función getAllObservableSharedListasEnabled devuelve una lista de observables de listas habilitadas.
    //Estas listas son devueltas en función del usuario pasado por parámetro, recuperando así las listas compartidas
    //con este.
    suspend fun getAllObservableSharedListasEnabled(usuario: String): LiveData<MutableList<Lista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<Lista>>()
            var listas = mutableListOf<Lista>()
            FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .whereEqualTo("habilitado", true)
                .whereEqualTo("usuario_compartido", usuario)
                .addSnapshotListener { snapshot, _ ->
                    Log.d(COLLECTION_LISTAS, "Se han recuperado las listas observables habilitadas compartidas con el usuario")
                    if (snapshot != null) {
                        listas = snapshot.toObjects(Lista::class.java)
                    }
                    mutableData.value = listas
                }
            return@withContext mutableData
        }
    }

    //La función getAllObservableListasDisabled devuelve una lista de observables de listas deshabilitadas.
    //Estas listas son devueltas en función del usuario pasado por parámetro, recuperando así las listas creadas
    //por este.
    suspend fun getAllObservableListasDisabled(usuario: String): LiveData<MutableList<Lista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<Lista>>()
            var listas = mutableListOf<Lista>()
            FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .whereEqualTo("creador", usuario)
                .whereEqualTo("habilitado", false)
                .addSnapshotListener { snapshot, _ ->
                    Log.d(COLLECTION_LISTAS, "Se han recuperado las listas observables deshabilitadas creadas por el usuario")
                    if (snapshot != null) {
                        listas = snapshot.toObjects(Lista::class.java)
                    }
                    mutableData.value = listas
                }
            return@withContext mutableData
        }
    }

    //La función getAllObservableSharedListasDisabled devuelve una lista de observables de listas deshabilitadas.
    //Estas listas son devueltas en función del usuario pasado por parámetro, recuperando así las listas compartidas
    //con este.
    suspend fun getAllObservableSharedListasDisabled(usuario: String): LiveData<MutableList<Lista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<Lista>>()
            var listas = mutableListOf<Lista>()
            FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .whereEqualTo("usuario_compartido", usuario)
                .whereEqualTo("habilitado", false)
                .addSnapshotListener { snapshot, _ ->
                    Log.d(COLLECTION_LISTAS, "Se han recuperado las listas observables deshabilitadas creadas por el usuario")
                    if (snapshot != null) {
                        listas = snapshot.toObjects(Lista::class.java)
                    }
                    mutableData.value = listas
                }
            return@withContext mutableData
        }
    }

    //La función getAllObservableItemsEnabled devuelve una lista de observables de items habilitados según el email del usuario
    //y nombre de la lista que lo contiene. Ordenados por número de orden de forma descendente.
    suspend fun getAllObservableItemsEnabled(email: String, nombreListaPadre: String): LiveData<MutableList<ItemLista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<ItemLista>>()
            FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                .orderBy("orden", Query.Direction.DESCENDING)
                .whereEqualTo("fecha_baja", "")
                .whereEqualTo("nombre_lista_item", nombreListaPadre)
                .whereEqualTo("creador_lista_item", email)
                .addSnapshotListener { snapshot, _ ->
                    Log.d(COLLECTION_LISTAS, "Se han recuperado los ítems observables habilitados")
                    var items = mutableListOf<ItemLista>()
                    if (snapshot != null) {
                        items = snapshot.toObjects(ItemLista::class.java)
                    }
                    mutableData.value = items
                }
            return@withContext mutableData
        }
    }

    //La función getAllObservableListas devuelve una lista de observables de todas las listas.
    //(Administrador).
    suspend fun getAllObservableListas(): LiveData<MutableList<Lista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<Lista>>()
            var listas = mutableListOf<Lista>()
            FirebaseFirestore.getInstance().collection(COLLECTION_LISTAS)
                .whereEqualTo("usuario_compartido", "")
                .addSnapshotListener { snapshot, _ ->
                    Log.d(COLLECTION_LISTAS, "Se han recuperado las listas observables")
                    if (snapshot != null) {
                        listas = snapshot.toObjects(Lista::class.java)
                    }
                    mutableData.value = listas
                }
            return@withContext mutableData
        }
    }

    //La función getAllObservableUsuarios devuelve una lista de observables de todos los usuarios.
    //(Administrador).
    suspend fun getAllObservableUsuarios(): LiveData<MutableList<Usuario>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<Usuario>>()
            var usuarios = mutableListOf<Usuario>()
            FirebaseFirestore.getInstance().collection(COLLECTION_USUARIOS)
                .addSnapshotListener { snapshot, _ ->
                    Log.d(COLLECTION_USUARIOS, "Se han recuperado los usuarios observables")
                    if (snapshot != null) {
                        usuarios = snapshot.toObjects(Usuario::class.java)
                    }
                    mutableData.value = usuarios
                }
            return@withContext mutableData
        }
    }

    //La función getAllObservableItems devuelve una lista de observables de todos los ítems.
    //(Administrador).
    suspend fun getAllObservableItems(): LiveData<MutableList<ItemLista>> {
        return withContext(Dispatchers.IO) {
            val mutableData = MutableLiveData<MutableList<ItemLista>>()
            var items = mutableListOf<ItemLista>()
            FirebaseFirestore.getInstance().collection(COLLECTION_ITEMS)
                .addSnapshotListener { snapshot, _ ->
                    Log.d(COLLECTION_ITEMS, "Se han recuperado los ítems observables")
                    if (snapshot != null) {
                        items = snapshot.toObjects(ItemLista::class.java)
                    }
                    mutableData.value = items
                }
            return@withContext mutableData
        }
    }
}
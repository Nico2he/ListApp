package com.nicorodgon.listapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemLista(
    val nombre_item: String = "",
    val creador_lista_item: String = "",
    val nombre_lista_item: String = "",
    val descripcion: String = "",
    val fecha_alta: String = "",
    val fecha_baja: String = "",
    val imagen_item: String = "",
    var orden: Int = 0
): Parcelable
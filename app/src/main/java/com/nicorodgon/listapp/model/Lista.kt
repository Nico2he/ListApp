package com.nicorodgon.listapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lista(
    val creador: String = "",
    val nombre_lista: String = "",
    val imagen_lista: String = "",
    val habilitado: Boolean = true,
    val usuario_compartido: String = ""
): Parcelable
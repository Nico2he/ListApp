package com.nicorodgon.listapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lista(
    val nombre_lista: String = "",
    val imagen_lista: String = "",
    val habilitado: Boolean = true
): Parcelable
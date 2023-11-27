package com.nicorodgon.listapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    val email: String = ""
): Parcelable
package com.openclassrooms.vitesseapp.ui.model

import android.net.Uri

data class CandidateDisplay(
    val candidateId: Long? = 0,
    val firstname: String,
    val lastname: String,
    val photoUri: Uri?,
    val phone: String,
    val email: String,
    val birthdate: String,
    val age: Int,
    val salaryInEur: String?,
    val salaryInGbp: String? = null,
    val notes: String?,
    var isFavorite: Boolean = false,
)
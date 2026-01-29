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
    val salaryInEur: String?,
    val notes: String?,
    val isFavorite: Boolean = false,
)
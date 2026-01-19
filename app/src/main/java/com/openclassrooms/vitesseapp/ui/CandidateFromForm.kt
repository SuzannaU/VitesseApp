package com.openclassrooms.vitesseapp.ui

import android.net.Uri

data class CandidateFromForm(
    val firstname: String,
    val lastname: String,
    val photoUri: Uri?,
    val phone: String,
    val email: String,
    val birthdate: Long,
    val salaryInEur: Int?,
    val notes: String?,
)

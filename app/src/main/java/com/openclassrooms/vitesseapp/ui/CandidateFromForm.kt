package com.openclassrooms.vitesseapp.ui

data class CandidateFromForm(
    val firstname: String,
    val lastname: String,
    val photo: String?,
    val phone: String,
    val email: String,
    val birthdate: Long,
    val salaryInEur: Int?,
    val notes: String?,
)

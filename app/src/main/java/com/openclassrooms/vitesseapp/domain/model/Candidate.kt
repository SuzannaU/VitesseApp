package com.openclassrooms.vitesseapp.domain.model

data class Candidate(
    val candidateId: Long? = 0,
    val firstname: String,
    val lastname: String,
    val photoPath: String?,
    val phone: String,
    val email: String,
    val birthdate: Long,
    val age: Int?,
    val salaryCentsInEur: Long?,
    val notes: String?,
    val isFavorite: Boolean = false,
)
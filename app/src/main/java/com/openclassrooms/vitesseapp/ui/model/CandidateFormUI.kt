package com.openclassrooms.vitesseapp.ui.model

import android.graphics.Bitmap

data class CandidateFormUI(
    var candidateId: Long = 0,
    val firstname: String,
    val lastname: String,
    val photoBitmap: Bitmap? = null,
    val phone: String,
    val email: String,
    val birthdate: Long,
    val salaryInEur: Long?,
    val notes: String?,
    var isFavorite: Boolean = false,
)
package com.openclassrooms.vitesseapp.ui

import android.net.Uri
import com.openclassrooms.vitesseapp.data.entity.CandidateDto

data class CandidateUI(
    val firstname: String,
    val lastname: String,
    val photoUri: Uri?,
    val phone: String,
    val email: String,
    val birthdate: Long,
    val salaryInEur: Int?,
    val notes: String?,
){
    fun toDto(photoPath: String?): CandidateDto {
        return CandidateDto(
            firstname = this.firstname,
            lastname = this.lastname,
            photo = photoPath,
            phone = this.phone,
            email = this.email,
            birthdate = this.birthdate,
            salaryInEur = this.salaryInEur,
            notes = this.notes,
        )
    }
}

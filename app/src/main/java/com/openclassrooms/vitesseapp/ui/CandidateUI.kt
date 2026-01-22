package com.openclassrooms.vitesseapp.ui

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.model.Candidate
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

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

    fun toDomain(photoPath: String?): Candidate {
        val age = calculateAge(this.birthdate)
        return Candidate(
            firstname = this.firstname,
            lastname = this.lastname,
            photoPath = photoPath,
            phone = this.phone,
            email = this.email,
            birthdate = this.birthdate,
            age = age,
            salaryInEur = this.salaryInEur,
            notes = this.notes,
        )
    }

    private fun calculateAge(birthdate: Long): Int {
        val birthdateLocalDate = Instant.ofEpochMilli(birthdate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return Period.between(birthdateLocalDate, LocalDate.now()).years
    }
}

package com.openclassrooms.vitesseapp.ui

import android.net.Uri
import androidx.core.net.toUri
import com.openclassrooms.vitesseapp.domain.calculateAge
import com.openclassrooms.vitesseapp.domain.model.Candidate

data class CandidateUI(
    val candidateId: Long? = 0,
    val firstname: String,
    val lastname: String,
    val photoUri: Uri?,
    val phone: String,
    val email: String,
    val birthdate: Long,
    val salaryInEur: Int?,
    val notes: String?,
    val isFavorite: Boolean = false,
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

    companion object {

        fun fromDomain(candidate: Candidate): CandidateUI {

            return CandidateUI(
                candidateId = candidate.candidateId,
                firstname = candidate.firstname,
                lastname = candidate.lastname,
                photoUri = candidate.photoPath?.toUri(),
                phone = candidate.phone,
                email = candidate.email,
                birthdate = candidate.birthdate,
                salaryInEur = candidate.salaryInEur,
                notes = candidate.notes,
                isFavorite = candidate.isFavorite,
            )
        }
    }
}

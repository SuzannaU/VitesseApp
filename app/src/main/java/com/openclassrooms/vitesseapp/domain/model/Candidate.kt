package com.openclassrooms.vitesseapp.domain.model

import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.ui.CandidateUI
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

data class Candidate(
    val candidateId: Long? = 0,
    val firstname: String,
    val lastname: String,
    val photo: String?,
    val phone: String,
    val email: String,
    val birthdate: Long,
    val age: Int?,
    val salaryInEur: Int?,
    //val salaryInGbp: Int?,
    val notes: String?,
    val isFavorite: Boolean = false,
) {

    fun toDto(): CandidateDto {
        return CandidateDto(
            firstname = this.firstname,
            lastname = this.lastname,
            photo = this.photo,
            phone = this.phone,
            email = this.email,
            birthdate = this.birthdate,
            salaryInEur = this.salaryInEur,
            notes = this.notes,
        )
    }

    companion object {
        fun fromUi(candidateUi: CandidateUI, photoPath: String, rateToGbp: Int): Candidate {
            val age = calculateAge(candidateUi.birthdate)
            //val salaryInGbp = candidateUi.salaryInEur?.let { it * rateToGbp }

            return Candidate(
                firstname = candidateUi.firstname,
                lastname = candidateUi.lastname,
                photo = photoPath,
                phone = candidateUi.phone,
                email = candidateUi.email,
                birthdate = candidateUi.birthdate,
                age = age,
                salaryInEur = candidateUi.salaryInEur,
                //salaryInGbp = salaryInGbp,
                notes = candidateUi.notes,
            )
        }

        fun fromDto(candidateDto: CandidateDto, rateToGbp: Int): Candidate {
            val age = calculateAge(candidateDto.birthdate)
            //val salaryInGbp = candidateDto.salaryInEur?.let { it * rateToGbp }

            return Candidate(
                candidateDto.candidateId,
                candidateDto.firstname,
                candidateDto.lastname,
                candidateDto.photo,
                candidateDto.phone,
                candidateDto.email,
                candidateDto.birthdate,
                age,
                candidateDto.salaryInEur,
                //salaryInGbp,
                candidateDto.notes,
                candidateDto.isFavorite,
            )
        }

        private fun calculateAge(birthdate: Long): Int {
            val birthdateLocalDate = Instant.ofEpochMilli(birthdate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            return Period.between(birthdateLocalDate, LocalDate.now()).years
        }
    }

}
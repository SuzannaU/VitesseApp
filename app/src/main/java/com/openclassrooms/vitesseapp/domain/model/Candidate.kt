package com.openclassrooms.vitesseapp.domain.model

import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

data class Candidate(
    val candidateId: Long? = 0,
    val firstname: String,
    val lastname: String,
    val photoPath: String?,
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
            photoPath = this.photoPath,
            phone = this.phone,
            email = this.email,
            birthdate = this.birthdate,
            salaryInEur = this.salaryInEur,
            notes = this.notes,
        )
    }

    companion object {

        fun fromDto(candidateDto: CandidateDto, rateToGbp: Int): Candidate {
            val age = calculateAge(candidateDto.birthdate)
            //val salaryInGbp = candidateDto.salaryInEur?.let { it * rateToGbp }

            return Candidate(
                candidateDto.candidateId,
                candidateDto.firstname,
                candidateDto.lastname,
                candidateDto.photoPath,
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
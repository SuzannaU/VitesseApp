package com.openclassrooms.vitesseapp.domain.model

import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset

data class Candidate(
    var candidateId: Long = 0,
    var firstname: String,
    var lastname: String,
    var photo: String,
    var phone: String,
    var email: String,
    var birthdate: LocalDate,
    var age: Int,
    var salaryInEur: Int,
    var salaryInGbp: Int,
    var notes: String,
    var isFavorite: Boolean = false,
) {

    fun toDto(): CandidateDto {
        return CandidateDto(
            this.candidateId,
            this.firstname,
            this.lastname,
            this.photo,
            this.phone,
            this.email,
            this.birthdate.atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant()
                .toEpochMilli(),
            this.salaryInEur,
            this.notes,
            this.isFavorite
        )
    }

    companion object {
        fun fromDto(candidateDto: CandidateDto, rateToGbp: Int): Candidate {
            val instant = Instant.ofEpochMilli(candidateDto.birthdate)
            val birthdate = LocalDate.ofInstant(instant, ZoneOffset.systemDefault())
            val age = Period.between(birthdate, LocalDate.now()).years
            val salaryInGbp = candidateDto.salaryInEur * rateToGbp

            return Candidate(
                candidateDto.candidateId,
                candidateDto.firstname,
                candidateDto.lastname,
                candidateDto.photo,
                candidateDto.phone,
                candidateDto.email,
                birthdate,
                age,
                candidateDto.salaryInEur,
                salaryInGbp,
                candidateDto.notes,
                candidateDto.isFavorite,
            )
        }
    }

}
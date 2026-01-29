package com.openclassrooms.vitesseapp.data

import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.domain.calculateAge
import com.openclassrooms.vitesseapp.domain.model.Candidate

fun Candidate.toDto() : CandidateDto {
    return CandidateDto(
        firstname = this.firstname,
        lastname = this.lastname,
        photoPath = this.photoPath,
        phone = this.phone,
        email = this.email,
        birthdate = this.birthdate,
        salaryCentsInEur = this.salaryCentsInEur,
        notes = this.notes,
    )
}

fun CandidateDto.toDomain() : Candidate {
    val age = calculateAge(this.birthdate)
    return Candidate(
        this.candidateId,
        this.firstname,
        this.lastname,
        this.photoPath,
        this.phone,
        this.email,
        this.birthdate,
        age,
        this.salaryCentsInEur,
        this.notes,
        this.isFavorite,
    )
}
package com.openclassrooms.vitesseapp.data.entity

import com.openclassrooms.vitesseapp.domain.calculateAge
import com.openclassrooms.vitesseapp.domain.model.Candidate

fun Candidate.toDto() : CandidateDto {
    return CandidateDto(
        candidateId = this.candidateId,
        firstname = this.firstname,
        lastname = this.lastname,
        photoByteArray = this.photoByteArray,
        phone = this.phone,
        email = this.email,
        birthdate = this.birthdate,
        salaryCentsInEur = this.salaryCentsInEur,
        notes = this.notes,
        isFavorite = this.isFavorite,
    )
}

fun CandidateDto.toDomain() : Candidate {
    val age = calculateAge(this.birthdate)
    return Candidate(
        this.candidateId,
        this.firstname,
        this.lastname,
        this.photoByteArray,
        this.phone,
        this.email,
        this.birthdate,
        age,
        this.salaryCentsInEur,
        this.notes,
        this.isFavorite,
    )
}
package com.openclassrooms.vitesseapp.domain.model

import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.ui.CandidateFromForm

fun CandidateFromForm.toDto(photoPath: String?): CandidateDto {
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

fun Candidate.toDto(): CandidateDto {
    return CandidateDto(
        this.candidateId,
        this.firstname,
        this.lastname,
        this.photo,
        this.phone,
        this.email,
        this.birthdate,
        this.salaryInEur,
        this.notes,
        this.isFavorite
    )
}
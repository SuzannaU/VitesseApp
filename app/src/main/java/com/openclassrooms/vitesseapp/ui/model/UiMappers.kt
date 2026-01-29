package com.openclassrooms.vitesseapp.ui.model

import androidx.core.net.toUri
import com.openclassrooms.vitesseapp.domain.calculateAge
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.ui.formatBirthdateToString
import com.openclassrooms.vitesseapp.ui.formatSalaryToString

fun Candidate.toCandidateDisplay() : CandidateDisplay {
    return CandidateDisplay(
        candidateId = this.candidateId,
        firstname = this.firstname,
        lastname = this.lastname,
        photoUri = this.photoPath?.toUri(),
        phone = this.phone,
        email = this.email,
        birthdate = formatBirthdateToString(this.birthdate),
        salaryInEur = this.salaryInEur?.let { formatSalaryToString(it) },
        notes = this.notes,
        isFavorite = this.isFavorite,
    )
}

fun CandidateFormUI.toDomain(photoPath: String?) : Candidate {
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
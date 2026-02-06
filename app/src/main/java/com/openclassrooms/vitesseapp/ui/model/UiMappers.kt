package com.openclassrooms.vitesseapp.ui.model

import android.graphics.Bitmap
import com.openclassrooms.vitesseapp.domain.calculateAge
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.ui.helpers.formatBirthdateToString
import com.openclassrooms.vitesseapp.ui.helpers.formatSalaryToString
import java.io.ByteArrayOutputStream

fun Candidate.toCandidateDisplay(
    salaryInCentsGbp: Long?,
    bitmapDecoder: BitmapDecoder
): CandidateDisplay {
    val bitmap = this.photoByteArray?.let { bitmapDecoder.decode(it) }

    return CandidateDisplay(
        candidateId = this.candidateId,
        firstname = this.firstname,
        lastname = this.lastname,
        photoBitmap = bitmap,
        phone = this.phone,
        email = this.email,
        birthdate = formatBirthdateToString(this.birthdate),
        age = calculateAge(this.birthdate),
        salaryInEur = this.salaryCentsInEur?.let { formatSalaryToString(it / 100) },
        salaryInGbp = salaryInCentsGbp?.let { formatSalaryToString(it / 100) },
        notes = this.notes,
        isFavorite = this.isFavorite,
    )
}

fun Candidate.toCandidateFormUI(
    bitmapDecoder: BitmapDecoder
): CandidateFormUI {
    val bitmap = this.photoByteArray?.let { bitmapDecoder.decode(it) }

    return CandidateFormUI(
        candidateId = this.candidateId,
        firstname = this.firstname,
        lastname = this.lastname,
        photoBitmap = bitmap,
        phone = this.phone,
        email = this.email,
        birthdate = this.birthdate,
        salaryInEur = this.salaryCentsInEur?.div(100L),
        notes = this.notes,
        isFavorite = this.isFavorite,
    )
}

fun CandidateFormUI.toDomain(): Candidate {
    val byteArray = this.photoBitmap?.let {
        val stream = ByteArrayOutputStream()
        it.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        stream.toByteArray()
    }
    val age = calculateAge(this.birthdate)

    return Candidate(
        candidateId = this.candidateId,
        firstname = this.firstname,
        lastname = this.lastname,
        photoByteArray = byteArray,
        phone = this.phone,
        email = this.email,
        birthdate = this.birthdate,
        age = age,
        salaryCentsInEur = this.salaryInEur?.times(100L),
        notes = this.notes,
    )
}
package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.data.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.model.toDto
import com.openclassrooms.vitesseapp.ui.CandidateFromForm

class SaveCandidateUseCase(private val candidateRepository: CandidateRepository) {
    suspend fun execute(candidate: CandidateFromForm) {
        candidateRepository.saveCandidate(candidate.toDto())
    }
}
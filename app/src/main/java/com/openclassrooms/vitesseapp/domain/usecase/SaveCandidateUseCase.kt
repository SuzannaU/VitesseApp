package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository

class SaveCandidateUseCase(
    private val candidateRepository: CandidateRepository,
) {
    suspend fun execute(candidate: Candidate) {
        candidateRepository.saveCandidate(candidate.toDto())
    }
}
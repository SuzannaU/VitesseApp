package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository

class DeleteCandidateUseCase(
    private val candidateRepository: CandidateRepository,
) {
    suspend fun execute(candidateId: Long) {
        candidateRepository.deleteCandidate(candidateId)
    }
}
package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.model.Candidate

class LoadCandidateUseCase(private val candidateRepository: CandidateRepository) {
    suspend fun execute(candidateId: Long): Candidate {
         return candidateRepository.fetchCandidate(candidateId)
    }
}
package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.model.CandidateDto

class LoadCandidateUseCase(private val candidateRepository: CandidateRepository) {
    suspend fun execute(candidateId: Long): CandidateDto? {
         return candidateRepository.fetchCandidate(candidateId)
    }
}
package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository

class SaveCandidateUseCase(
    private val candidateRepository: CandidateRepository,
) {
    suspend fun execute(candidateDto: CandidateDto) {
        candidateRepository.saveCandidate(candidateDto)
    }
}
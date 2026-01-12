package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.data.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.model.Candidate

class SaveCandidateUseCase(private val candidateRepository: CandidateRepository) {
    suspend fun execute(candidate: CandidateDto) {
        candidateRepository.saveCandidate(candidate)
    }
}
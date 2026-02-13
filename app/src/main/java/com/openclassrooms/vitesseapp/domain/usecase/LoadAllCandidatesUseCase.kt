package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import kotlinx.coroutines.flow.Flow

class LoadAllCandidatesUseCase(
    private val candidateRepository: CandidateRepository,
) {
    fun execute(): Flow<List<CandidateDto>> {
        return candidateRepository.fetchAllCandidates()
    }
}
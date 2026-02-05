package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository

class UpdateFavoriteUseCase(
    private val candidateRepository: CandidateRepository
) {
    suspend fun execute(candidateId: Long, isFavorite: Boolean) {
        candidateRepository.updateCandidateIsFavorite(candidateId, isFavorite)
    }
}
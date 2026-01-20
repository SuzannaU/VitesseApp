package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.repository.ImageRepository
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.ui.CandidateUI

class SaveCandidateUseCase(
    private val candidateRepository: CandidateRepository,
    private val imageRepository: ImageRepository,
) {
    suspend fun execute(candidate: CandidateUI) {

        val photoPath = candidate.photoUri?.let {
            imageRepository.saveImage(it)
        }

        val candidateDto = candidate.toDto(photoPath)
        candidateRepository.saveCandidate(candidateDto)
    }
}
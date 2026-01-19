package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.data.repository.CandidateRepository
import com.openclassrooms.vitesseapp.data.storage.ImageStorage
import com.openclassrooms.vitesseapp.domain.model.toDto
import com.openclassrooms.vitesseapp.ui.CandidateFromForm

class SaveCandidateUseCase(
    private val candidateRepository: CandidateRepository,
    private val imageStorage: ImageStorage,
) {
    suspend fun execute(candidate: CandidateFromForm) {

        val photoPath = candidate.photoUri?.let {
            imageStorage.saveImage(it)
        }

        val candidateDto = candidate.toDto(photoPath)
        candidateRepository.saveCandidate(candidateDto)
    }
}
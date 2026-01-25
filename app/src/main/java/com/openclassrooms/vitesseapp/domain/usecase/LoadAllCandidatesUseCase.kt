package com.openclassrooms.vitesseapp.domain.usecase

import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoadAllCandidatesUseCase(
    private val candidateRepository: CandidateRepository,
) {
    fun execute(): Flow<List<Candidate>> {
        return candidateRepository.fetchAllCandidates()
            .map { candidatesDto ->
                candidatesDto.map { dto ->
                    Candidate.fromDto(dto)
                }
            }
    }
}
package com.openclassrooms.vitesseapp.domain.repository

import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import kotlinx.coroutines.flow.Flow

interface CandidateRepository {

    suspend fun fetchCandidate(candidateId: Long): CandidateDto?
    suspend fun saveCandidate(candidateDto: CandidateDto)

    suspend fun updateCandidateIsFavorite(candidateId: Long, isFavorite: Boolean)
    suspend fun deleteCandidate(candidateId: Long)
    fun fetchAllCandidates(): Flow<List<CandidateDto>>
}
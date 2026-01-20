package com.openclassrooms.vitesseapp.domain.repository

import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.domain.model.Candidate
import kotlinx.coroutines.flow.Flow

interface CandidateRepository {

    suspend fun fetchCandidate(candidateId: Long): Candidate
    suspend fun saveCandidate(candidate: CandidateDto)
    suspend fun deleteCandidate(candidateId: Long)
    fun fetchAllCandidates(): Flow<List<Candidate>>
}
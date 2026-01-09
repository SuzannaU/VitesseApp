package com.openclassrooms.vitesseapp.data.repository

import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.domain.model.Candidate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CandidateRepository(private val candidateDao: CandidateDao) {

    suspend fun fetchCandidate(candidateId: Long): Candidate {
        val candidateDto = candidateDao.getCandidateById(candidateId)
        return Candidate.fromDto(candidateDto, 98)
    }

    suspend fun saveCandidate(candidate: Candidate) {
        candidateDao.saveCandidate(candidate.toDto())
    }

    suspend fun deleteCandidate(candidateId: Long) {
        candidateDao.deleteCandidateById(candidateId)
    }

    // at this point the list could be empty
    fun fetchAllCandidates(): Flow<List<Candidate>> {
        return candidateDao.getAllCandidates()
            .map { dtoList ->
                dtoList.map { dto ->
                    Candidate.fromDto(dto, 98)
                }
            }
    }
}

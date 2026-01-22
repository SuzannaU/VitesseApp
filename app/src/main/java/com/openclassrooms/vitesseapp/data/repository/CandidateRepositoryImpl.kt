package com.openclassrooms.vitesseapp.data.repository

import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.model.Candidate
import kotlinx.coroutines.flow.Flow

class CandidateRepositoryImpl(private val candidateDao: CandidateDao) : CandidateRepository{

    override suspend fun fetchCandidate(candidateId: Long): Candidate {
        val candidateDto = candidateDao.getCandidateById(candidateId)
        return Candidate.fromDto(candidateDto)
    }

    override suspend fun saveCandidate(candidate: CandidateDto) {
        candidateDao.saveCandidate(candidate)
    }

    override suspend fun deleteCandidate(candidateId: Long) {
        candidateDao.deleteCandidateById(candidateId)
    }

    override fun fetchAllCandidates(): Flow<List<CandidateDto>> {
        return candidateDao.getAllCandidates()
    }
}

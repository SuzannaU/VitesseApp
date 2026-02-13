package com.openclassrooms.vitesseapp.data.repository

import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.entity.toDomain
import com.openclassrooms.vitesseapp.data.entity.toEntity
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CandidateRepositoryImpl(private val candidateDao: CandidateDao) : CandidateRepository{

    override suspend fun fetchCandidate(candidateId: Long): CandidateDto? {
        val candidateDto = candidateDao.getCandidateById(candidateId)
        return candidateDto?.toDomain()
    }

    override suspend fun saveCandidate(candidateDto: CandidateDto) {
        candidateDao.saveCandidate(candidateDto.toEntity())
    }

    override suspend fun updateCandidateIsFavorite(candidateId: Long, isFavorite: Boolean) {
        candidateDao.updateCandidateIsFavorite(candidateId, isFavorite)
    }

    override suspend fun deleteCandidate(candidateId: Long) {
        candidateDao.deleteCandidateById(candidateId)
    }

    override fun fetchAllCandidates(): Flow<List<CandidateDto>> {
        return candidateDao.getAllCandidates()
            .map { candidatesDto ->
                candidatesDto.map { dto ->
                    dto.toDomain()
                }
            }
    }
}

package com.openclassrooms.vitesseapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {

    @Upsert
    suspend fun saveCandidate(candidate: CandidateDto)

    @Query("SELECT * FROM candidates")
    fun getAllCandidates(): Flow<List<CandidateDto>>

    @Query("SELECT * FROM candidates WHERE candidateId = :id")
    suspend fun getCandidateById(id: Long): CandidateDto?

    @Query("DELETE FROM candidates WHERE candidateId = :id")
    suspend fun deleteCandidateById(id: Long)

}
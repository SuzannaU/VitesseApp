package com.openclassrooms.vitesseapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.openclassrooms.vitesseapp.data.entity.CandidateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {

    @Upsert
    suspend fun saveCandidate(candidate: CandidateEntity)

    @Query("SELECT * FROM candidates")
    fun getAllCandidates(): Flow<List<CandidateEntity>>

    @Query("SELECT * FROM candidates WHERE candidateId = :id")
    suspend fun getCandidateById(id: Long): CandidateEntity?

    @Query("DELETE FROM candidates WHERE candidateId = :id")
    suspend fun deleteCandidateById(id: Long)

    @Query("UPDATE candidates SET is_favorite= :isFavorite WHERE candidateId = :id")
    suspend fun updateCandidateIsFavorite(id: Long, isFavorite: Boolean)

}
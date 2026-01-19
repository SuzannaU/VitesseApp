package com.openclassrooms.vitesseapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.entity.CandidateDto

@Database(
    entities = [CandidateDto::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCandidateDao(): CandidateDao
}
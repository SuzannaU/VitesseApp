package com.openclassrooms.vitesseapp.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [CandidateDto::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCandidateDao(): CandidateDao

//    companion object {
//
//        suspend fun populateDatabase(candidateDao: CandidateDao) {
//            candidateDao.saveCandidate(
//                CandidateDto(
//                    1, "me"
//                )
//            )
//            Log.d("AppDatabase", "populateDatabase done")
//        }
//    }
//
//    class AppDatabaseCallback(
//        private val scope: CoroutineScope,
//        private val databaseProvider: () -> AppDatabase
//    ) : Callback() {
//
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//                scope.launch {
//                    val database = databaseProvider()
//                    populateDatabase(database.getCandidateDao())
//                }
//            }
//
//    }
}
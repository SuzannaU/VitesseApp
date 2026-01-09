package com.openclassrooms.vitesseapp

import android.app.Application
import androidx.room.Room
import com.openclassrooms.vitesseapp.data.AppDatabase
import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.ui.AddViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

//fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
fun provideDatabase(application: Application): AppDatabase {
//    val scope = provideCoroutineScope()
//    lateinit var database: AppDatabase

    val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "app_database"
    )
        //.addCallback(AppDatabaseCallback(scope) { database })
        .build()
    return database
}
fun provideCandidateDao(appDatabase: AppDatabase): CandidateDao = appDatabase.getCandidateDao()

val appModule = module {

    single { provideDatabase(get()) }
    single { provideCandidateDao(get()) }
    single { CandidateRepository(get()) }
    single { LoadCandidateUseCase(get()) }
    single { SaveCandidateUseCase(get()) }
    viewModel { AddViewModel(get()) }

}
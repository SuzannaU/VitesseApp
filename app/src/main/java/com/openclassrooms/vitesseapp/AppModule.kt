package com.openclassrooms.vitesseapp

import android.app.Application
import androidx.room.Room
import com.openclassrooms.vitesseapp.data.AppDatabase
import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.repository.CandidateRepositoryImpl
import com.openclassrooms.vitesseapp.domain.repository.ImageRepository
import com.openclassrooms.vitesseapp.data.storage.InternalImageStorage
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import com.openclassrooms.vitesseapp.ui.add.AddViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun provideDatabase(application: Application): AppDatabase {

    val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "app_database"
    )
        .fallbackToDestructiveMigration(true)
        .build()
    return database
}
fun provideCandidateDao(appDatabase: AppDatabase): CandidateDao = appDatabase.getCandidateDao()

val appModule = module {

    single { provideDatabase(get()) }

    single { provideCandidateDao(get()) }

    single<CandidateRepository> { CandidateRepositoryImpl(get()) }
    single<ImageRepository> { InternalImageStorage(context = androidContext()) }

    factory { LoadCandidateUseCase(get()) }
    factory { SaveCandidateUseCase(get()) }
    factory { SaveImageUseCase(get()) }

    viewModel { AddViewModel(get(), get()) }

}
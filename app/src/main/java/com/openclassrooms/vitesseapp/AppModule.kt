package com.openclassrooms.vitesseapp

import android.app.Application
import androidx.room.Room
import com.openclassrooms.vitesseapp.data.AppDatabase
import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.dao.RateApiService
import com.openclassrooms.vitesseapp.data.repository.CandidateRepositoryImpl
import com.openclassrooms.vitesseapp.data.repository.RateRepositoryImpl
import com.openclassrooms.vitesseapp.domain.repository.ImageRepository
import com.openclassrooms.vitesseapp.data.storage.InternalImageStorage
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.repository.RateRepository
import com.openclassrooms.vitesseapp.domain.usecase.ConvertEurToGbpUseCase
import com.openclassrooms.vitesseapp.domain.usecase.DeleteCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import com.openclassrooms.vitesseapp.ui.add.AddViewModel
import com.openclassrooms.vitesseapp.ui.detail.DetailViewModel
import com.openclassrooms.vitesseapp.ui.edit.EditViewModel
import com.openclassrooms.vitesseapp.ui.home.HomeViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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

fun provideRetrofit() : Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/")
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            )
        )
        .build()
}

fun provideRateApiService(retrofit: Retrofit) : RateApiService = retrofit.create(RateApiService::class.java)

val appModule = module {

    single { provideDatabase(get()) }

    single { provideRetrofit()}

    single { provideCandidateDao(get()) }
    single { provideRateApiService(get()) }

    single<CandidateRepository> { CandidateRepositoryImpl(get()) }
    single<ImageRepository> { InternalImageStorage(context = androidContext()) }
    single<RateRepository> { RateRepositoryImpl(get()) }

    factory { LoadCandidateUseCase(get()) }
    factory { LoadAllCandidatesUseCase(get()) }
    factory { SaveCandidateUseCase(get()) }
    factory { SaveImageUseCase(get()) }
    factory { DeleteCandidateUseCase(get()) }
    factory { ConvertEurToGbpUseCase(get()) }

    viewModel { AddViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { DetailViewModel(get(), get(), get(), get()) }
    viewModel { EditViewModel(get(), get(), get()) }
}
package com.openclassrooms.vitesseapp.ui

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import com.openclassrooms.vitesseapp.ui.add.AddViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class AddViewModelTest {

    val saveCandidateUseCase = mockk<SaveCandidateUseCase>()
    val saveImageUseCase = mockk<SaveImageUseCase>()
    val viewModel = AddViewModel(saveCandidateUseCase, saveImageUseCase)

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun saveCandidateTest_shouldCallUseCases() = runTest {

        val uri = mockk<Uri>()
        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)

        val candidateUi = CandidateUI(
            firstname = "firstname",
            lastname = "lastname",
            photoUri = uri,
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            salaryInEur = 1,
            notes = null,
        )

        val expectedCandidate = Candidate(
            candidateId = 0,
            firstname = "firstname",
            lastname = "lastname",
            photoPath = "path",
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            salaryInEur = 1,
            notes = null,
            age = expectedAge,
            isFavorite = false,
        )

        val candidateCapture = slot<Candidate>()

        coEvery { saveImageUseCase.execute(any()) } returns "path"
        coEvery { saveCandidateUseCase.execute(capture(candidateCapture)) } returns Unit

        viewModel.saveCandidate(candidateUi)
        advanceUntilIdle()

        assertEquals(expectedCandidate, candidateCapture.captured)
        coVerify {
            saveImageUseCase.execute(any())
            saveCandidateUseCase.execute(any())
        }
    }

    @Test
    fun saveCandidateTest_withNullPhotoUri_shouldOnlyCallCandidateUseCase() = runTest {

        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)

        val candidateUi = CandidateUI(
            firstname = "firstname",
            lastname = "lastname",
            photoUri = null,
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            salaryInEur = null,
            notes = null,
        )

        val expectedCandidate = Candidate(
            candidateId = 0,
            firstname = "firstname",
            lastname = "lastname",
            photoPath = null,
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            salaryInEur = null,
            notes = null,
            age = expectedAge,
            isFavorite = false,
        )

        val candidateCapture = slot<Candidate>()

        coEvery { saveImageUseCase.execute(any()) } returns "path"
        coEvery { saveCandidateUseCase.execute(capture(candidateCapture)) } returns Unit

        viewModel.saveCandidate(candidateUi)
        advanceUntilIdle()

        assertEquals(expectedCandidate, candidateCapture.captured)
        coVerify(exactly = 0) { saveImageUseCase.execute(any()) }
        coVerify { saveCandidateUseCase.execute(any()) }
    }

    private fun createBirthdateForAge(age: Int): Long {
        return LocalDate.now()
            .minusYears(age.toLong())
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
    }
}
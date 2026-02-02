package com.openclassrooms.vitesseapp.ui

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import com.openclassrooms.vitesseapp.ui.add.AddViewModel
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AddViewModelTest {

    val saveCandidateUseCase = mockk<SaveCandidateUseCase>()
    val saveImageUseCase = mockk<SaveImageUseCase>()
    val viewModel = AddViewModel(saveCandidateUseCase, saveImageUseCase)
    lateinit var testScope: TestScope
    lateinit var candidateFormUi: CandidateFormUI
    lateinit var expectedCandidate: Candidate
    val uri = mockk<Uri>(relaxed = true)

    @BeforeEach
    fun setup() {
        testScope = TestScope()
        Dispatchers.setMain(UnconfinedTestDispatcher(testScope.testScheduler))

        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)

        candidateFormUi = CandidateFormUI(
            firstname = "firstname",
            lastname = "lastname",
            photoUri = uri,
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            salaryInEur = 1,
            notes = null,
        )

        expectedCandidate = Candidate(
            candidateId = 0,
            firstname = "firstname",
            lastname = "lastname",
            photoPath = "path",
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            salaryCentsInEur = 100,
            notes = null,
            age = expectedAge,
            isFavorite = false,
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveCandidateTest_shouldCallUseCasesAndUpdateState() = testScope.runTest {

        val candidateCapture = slot<Candidate>()
        coEvery { saveImageUseCase.execute(any()) } returns "path"
        coEvery { saveCandidateUseCase.execute(capture(candidateCapture)) } returns Unit

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.addUiState.value
        assertTrue(state is AddViewModel.AddUiState.LoadedState)
        assertEquals(expectedCandidate, candidateCapture.captured)
        coVerify {
            saveImageUseCase.execute(any())
            saveCandidateUseCase.execute(any())
        }
    }

    @Test
    fun saveCandidateTest_withNullPhotoUri_shouldOnlyCallCandidateUseCase() = testScope.runTest {

        val candidateCapture = slot<Candidate>()
        val candUi = candidateFormUi.copy(photoUri = null)
        val expCand = expectedCandidate.copy(photoPath = null)
        coEvery { saveImageUseCase.execute(any()) } returns "path"
        coEvery { saveCandidateUseCase.execute(capture(candidateCapture)) } returns Unit

        viewModel.saveCandidate(candUi)
        advanceUntilIdle()

        val state = viewModel.addUiState.value
        assertTrue(state is AddViewModel.AddUiState.LoadedState)
        assertEquals(expCand, candidateCapture.captured)
        coVerify(exactly = 0) { saveImageUseCase.execute(any()) }
        coVerify { saveCandidateUseCase.execute(any()) }
    }

    @Test
    fun saveCandidateTest_withErrorWhileSavingImage_shouldUpdateUiState() = testScope.runTest {

        coEvery { saveImageUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.addUiState.value
        assertTrue(state is AddViewModel.AddUiState.ErrorState)
        coVerify { saveImageUseCase.execute(any()) }
        coVerify(exactly = 0) { saveCandidateUseCase.execute(any()) }
    }

    @Test
    fun saveCandidateTest_withErrorWhileSavingCandidate_shouldUpdateUiState() = testScope.runTest {

        coEvery { saveImageUseCase.execute(any()) } returns "path"
        coEvery { saveCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.addUiState.value
        assertTrue(state is AddViewModel.AddUiState.ErrorState)
        coVerify { saveImageUseCase.execute(any()) }
        coVerify { saveCandidateUseCase.execute(any()) }
    }
}
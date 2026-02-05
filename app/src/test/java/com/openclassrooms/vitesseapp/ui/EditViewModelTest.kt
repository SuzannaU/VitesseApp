package com.openclassrooms.vitesseapp.ui

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveImageUseCase
import com.openclassrooms.vitesseapp.ui.edit.EditViewModel
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import com.openclassrooms.vitesseapp.ui.model.toCandidateFormUI
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditViewModelTest {

    val loadCandidateUseCase = mockk<LoadCandidateUseCase>()
    val saveCandidateUseCase = mockk<SaveCandidateUseCase>()
    val saveImageUseCase = mockk<SaveImageUseCase>()
    val viewModel = EditViewModel(loadCandidateUseCase, saveImageUseCase, saveCandidateUseCase)
    lateinit var testScope: TestScope
    lateinit var candidate: Candidate
    lateinit var candidateFormUi: CandidateFormUI
    val uri = mockk<Uri>(relaxed = true)

    @BeforeEach
    fun setup() {
        testScope = TestScope()
        Dispatchers.setMain(UnconfinedTestDispatcher(testScope.testScheduler))

        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns uri

        val age = 50
        val birthdate = createBirthdateForAge(age)
        val salaryCentsInEur = 100L
        val salaryInEur = salaryCentsInEur.div(salaryCentsInEur)

        candidate = Candidate(
            candidateId = 1,
            firstname = "firstname",
            lastname = "lastname",
            photoPath = "path",
            phone = "123456",
            email = "email",
            birthdate = birthdate,
            notes = null,
            age = age,
            salaryCentsInEur = salaryCentsInEur,
        )

        candidateFormUi = CandidateFormUI(
            candidateId = 1,
            firstname = "firstname",
            lastname = "lastname",
            photoUri = uri,
            phone = "123456",
            email = "email",
            birthdate = birthdate,
            salaryInEur = salaryInEur,
            notes = null,
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadCandidate_shouldCallUseCasesAndUpdateUiState() = testScope.runTest {
        val expectedCandidateFormUI = candidate.toCandidateFormUI()
        coEvery { loadCandidateUseCase.execute(any()) } returns candidate

        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        assertTrue(state is EditViewModel.EditUiState.CandidateFound)

        val foundState = state as EditViewModel.EditUiState.CandidateFound
        assertEquals(expectedCandidateFormUI, foundState.candidateFormUI)
        coVerify { loadCandidateUseCase.execute(any()) }
    }

    @Test
    fun loadCandidate_withNoCandidate_shouldCallUseCasesAndUpdateUiState() = testScope.runTest {
        coEvery { loadCandidateUseCase.execute(any()) } returns null

        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        assertTrue(state is EditViewModel.EditUiState.NoCandidateFound)
        coVerify { loadCandidateUseCase.execute(any()) }
    }

    @Test
    fun loadCandidate_withFailureToLoad_shouldCallUseCasesAndUpdateUiState() = testScope.runTest {
        coEvery { loadCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        assertTrue(state is EditViewModel.EditUiState.ErrorState)
        coVerify { loadCandidateUseCase.execute(any()) }
    }

    @Test
    fun saveCandidateTest_shouldCallUseCasesAndUpdateState() = testScope.runTest {

        val candidateCapture = slot<Candidate>()
        coEvery { saveImageUseCase.execute(any()) } returns "path"
        coEvery { saveCandidateUseCase.execute(capture(candidateCapture)) } returns Unit

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        println("state is $state")
        assertTrue(state is EditViewModel.EditUiState.SaveSuccess)
        assertEquals(candidate, candidateCapture.captured)
        coVerify {
            saveImageUseCase.execute(any())
            saveCandidateUseCase.execute(any())
        }
    }

    @Test
    fun saveCandidateTest_withNullPhotoUri_shouldOnlyCallCandidateUseCase() = testScope.runTest {

        val candidateCapture = slot<Candidate>()
        val candUi = candidateFormUi.copy(photoUri = null)
        val expCand = candidate.copy(photoPath = null)
        coEvery { saveImageUseCase.execute(any()) } returns "path"
        coEvery { saveCandidateUseCase.execute(capture(candidateCapture)) } returns Unit

        viewModel.saveCandidate(candUi)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        println("state (null uri) is $state")
        assertTrue(state is EditViewModel.EditUiState.SaveSuccess)
        assertEquals(expCand, candidateCapture.captured)
        coVerify(exactly = 0) { saveImageUseCase.execute(any()) }
        coVerify { saveCandidateUseCase.execute(any()) }
    }

    @Test
    fun saveCandidateTest_withErrorWhileSavingImage_shouldUpdateUiState() = testScope.runTest {

        coEvery { saveImageUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        assertTrue(state is EditViewModel.EditUiState.ErrorState)
        coVerify { saveImageUseCase.execute(any()) }
        coVerify(exactly = 0) { saveCandidateUseCase.execute(any()) }
    }

    @Test
    fun saveCandidateTest_withErrorWhileSavingCandidate_shouldUpdateUiState() = testScope.runTest {

        coEvery { saveImageUseCase.execute(any()) } returns "path"
        coEvery { saveCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        assertTrue(state is EditViewModel.EditUiState.ErrorState)
        coVerify {
            saveImageUseCase.execute(any())
            saveCandidateUseCase.execute(any())
        }
    }
}
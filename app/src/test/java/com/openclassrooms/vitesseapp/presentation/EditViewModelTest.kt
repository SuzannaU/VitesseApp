package com.openclassrooms.vitesseapp.presentation

import android.graphics.Bitmap
import com.openclassrooms.vitesseapp.TestDispatcherProvider
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.presentation.viewmodel.EditViewModel
import com.openclassrooms.vitesseapp.presentation.BitmapDecoder
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import com.openclassrooms.vitesseapp.presentation.mapper.toCandidateFormUI
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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

    private val testDispatcher = StandardTestDispatcher()
    private val dispatcher = TestDispatcherProvider(testDispatcher)
    private lateinit var bitmapDecoder: BitmapDecoder
    private lateinit var loadCandidateUseCase: LoadCandidateUseCase
    private lateinit var saveCandidateUseCase: SaveCandidateUseCase
    private lateinit var viewModel: EditViewModel
    private lateinit var candidate: Candidate
    private lateinit var candidateFormUi: CandidateFormUI

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        bitmapDecoder = mockk<BitmapDecoder>()
        loadCandidateUseCase = mockk<LoadCandidateUseCase>()
        saveCandidateUseCase = mockk<SaveCandidateUseCase>()
        viewModel = EditViewModel(
            dispatcher, 
            loadCandidateUseCase, 
            saveCandidateUseCase, 
            bitmapDecoder
        )

        val age = 50
        val birthdate = createBirthdateForAge(age)
        val salaryCentsInEur = 100L
        val salaryInEur = salaryCentsInEur.div(salaryCentsInEur)
        val bytes = ByteArray(0)
        val bitmap = mockk<Bitmap>(relaxed = true)
        every { bitmapDecoder.decode(any()) } returns bitmap

        candidate = Candidate(
            candidateId = 1,
            firstname = "firstname",
            lastname = "lastname",
            photoByteArray = bytes,
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
            photoBitmap = bitmap,
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
    fun loadCandidate_shouldCallUseCasesAndUpdateUiState() = runTest {
        val expectedCandidateFormUI = candidate.toCandidateFormUI(bitmapDecoder)
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
    fun loadCandidate_withNoCandidate_shouldCallUseCasesAndUpdateUiState() = runTest {
        coEvery { loadCandidateUseCase.execute(any()) } returns null

        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        assertTrue(state is EditViewModel.EditUiState.NoCandidateFound)
        coVerify { loadCandidateUseCase.execute(any()) }
    }

    @Test
    fun loadCandidate_withFailureToLoad_shouldCallUseCasesAndUpdateUiState() = runTest {
        coEvery { loadCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        assertTrue(state is EditViewModel.EditUiState.ErrorState)
        coVerify { loadCandidateUseCase.execute(any()) }
    }

    @Test
    fun saveCandidateTest_shouldCallUseCasesAndUpdateState() = runTest {

        val candidateCapture = slot<Candidate>()
        coEvery { saveCandidateUseCase.execute(capture(candidateCapture)) } returns Unit

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        println("state is $state")
        assertTrue(state is EditViewModel.EditUiState.SaveSuccess)
        assertEquals(candidate, candidateCapture.captured)
        coVerify { saveCandidateUseCase.execute(any()) }
    }

    @Test
    fun saveCandidateTest_withErrorWhileSavingCandidate_shouldUpdateUiState() = runTest {

        coEvery { saveCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.editUiState.value
        assertTrue(state is EditViewModel.EditUiState.ErrorState)
        coVerify { saveCandidateUseCase.execute(any()) }
    }
}
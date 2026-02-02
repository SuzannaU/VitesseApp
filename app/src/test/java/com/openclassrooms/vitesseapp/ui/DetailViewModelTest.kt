package com.openclassrooms.vitesseapp.ui

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.ConvertEurToGbpUseCase
import com.openclassrooms.vitesseapp.domain.usecase.DeleteCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.ui.detail.DetailViewModel
import com.openclassrooms.vitesseapp.ui.model.toCandidateDisplay
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
class DetailViewModelTest {

    val loadCandidateUseCase = mockk<LoadCandidateUseCase>()
    val convertEurToGbpUseCase = mockk<ConvertEurToGbpUseCase>()
    val saveCandidateUseCase = mockk<SaveCandidateUseCase>()
    val deleteCandidateUseCase = mockk<DeleteCandidateUseCase>()
    val viewModel = DetailViewModel(
        loadCandidateUseCase,
        convertEurToGbpUseCase,
        saveCandidateUseCase,
        deleteCandidateUseCase
    )
    lateinit var testScope: TestScope
    lateinit var candidate: Candidate
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
        candidate = Candidate(
            candidateId = 1,
            firstname = "firstname1",
            lastname = "lastname1",
            photoPath = "path",
            phone = "123456",
            email = "email",
            birthdate = birthdate,
            notes = null,
            age = age,
            salaryCentsInEur = salaryCentsInEur,
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadCandidate_shouldCallUseCasesAndUpdateUiState() = testScope.runTest {
        val convertedSalary = 150L
        val expectedCandidateDisplay = candidate.toCandidateDisplay(convertedSalary)
        coEvery { loadCandidateUseCase.execute(any()) } returns candidate
        coEvery { convertEurToGbpUseCase.execute(any()) } returns convertedSalary

        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.CandidateFound)

        val foundState = state as DetailViewModel.DetailUiState.CandidateFound
        assertEquals(expectedCandidateDisplay, foundState.candidateDisplay)

        coVerify {
            loadCandidateUseCase.execute(any())
            convertEurToGbpUseCase.execute(any())
        }
    }

    @Test
    fun loadCandidate_withNoCandidate_shouldCallUseCasesAndUpdateUiState() = testScope.runTest {
        coEvery { loadCandidateUseCase.execute(any()) } returns null

        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.NoCandidateFound)

        coVerify { loadCandidateUseCase.execute(any()) }
    }

    @Test
    fun loadCandidate_withFailureToLoad_shouldCallUseCasesAndUpdateUiState() = testScope.runTest {
        coEvery { loadCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.ErrorState)

        coVerify { loadCandidateUseCase.execute(any()) }
    }

    @Test
    fun toggleFavoriteStatus_shouldUpdateCandidate() = testScope.runTest {
        preloadCandidate()
        advanceUntilIdle()
        val candidateCapture = slot<Candidate>()
        coEvery { saveCandidateUseCase.execute(capture(candidateCapture)) } returns Unit

        viewModel.toggleFavoriteStatus()
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.CandidateFound)
        assertEquals(!candidate.isFavorite, candidateCapture.captured.isFavorite)
        coVerify { saveCandidateUseCase.execute(any()) }
    }

    @Test
    fun toggleFavoriteStatus_withErrorWhileSaving_shouldUpdateCandidate() = testScope.runTest {
        preloadCandidate()
        advanceUntilIdle()
        coEvery { saveCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.toggleFavoriteStatus()
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.ErrorState)
        coVerify { saveCandidateUseCase.execute(any()) }
    }

    @Test
    fun toggleFavoriteStatus_whileInWrongState_shouldReturn() = testScope.runTest {
        coEvery { loadCandidateUseCase.execute(any()) } returns null
        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()

        viewModel.toggleFavoriteStatus()
        advanceUntilIdle()

        coVerify(exactly = 0) { saveCandidateUseCase.execute(any()) }
    }

    @Test
    fun deleteCandidate_shouldCallUseCaseAndUpdateState() = testScope.runTest {
        preloadCandidate()
        advanceUntilIdle()
        coEvery { deleteCandidateUseCase.execute(any()) } returns Unit

        viewModel.deleteCandidate(1L)
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.DeleteSuccess)
        coVerify { deleteCandidateUseCase.execute(any()) }
    }

    @Test
    fun deleteCandidate_withFailure_shouldCallUseCaseAndUpdateState() = testScope.runTest {
        preloadCandidate()
        advanceUntilIdle()
        coEvery { deleteCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.deleteCandidate(1L)
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.ErrorState)
        coVerify { deleteCandidateUseCase.execute(any()) }
    }

    @Test
    fun deleteCandidate_withWrongState_shouldReturnAndNotCallUseCase() = testScope.runTest {
        coEvery { loadCandidateUseCase.execute(any()) } returns null
        viewModel.loadCandidate(candidate.candidateId)
        advanceUntilIdle()
        coEvery { deleteCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.deleteCandidate(1L)
        advanceUntilIdle()

        coVerify(exactly = 0) { deleteCandidateUseCase.execute(any()) }
    }

    private fun preloadCandidate() {
        val convertedSalary = 150L
        coEvery { loadCandidateUseCase.execute(any()) } returns candidate
        coEvery { convertEurToGbpUseCase.execute(any()) } returns convertedSalary
        viewModel.loadCandidate(candidate.candidateId)
    }
}
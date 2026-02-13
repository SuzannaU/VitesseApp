package com.openclassrooms.vitesseapp.presentation

import android.graphics.Bitmap
import com.openclassrooms.vitesseapp.TestDispatcherProvider
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import com.openclassrooms.vitesseapp.domain.usecase.ConvertEurToGbpUseCase
import com.openclassrooms.vitesseapp.domain.usecase.DeleteCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.UpdateFavoriteUseCase
import com.openclassrooms.vitesseapp.presentation.viewmodel.DetailViewModel
import com.openclassrooms.vitesseapp.presentation.mapper.toCandidateDisplay
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
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatcher = TestDispatcherProvider(testDispatcher)
    private lateinit var bitmapDecoder: BitmapDecoder
    private lateinit var loadCandidateUseCase: LoadCandidateUseCase
    private lateinit var convertEurToGbpUseCase: ConvertEurToGbpUseCase
    private lateinit var updateFavoriteUseCase: UpdateFavoriteUseCase
    private lateinit var deleteCandidateUseCase: DeleteCandidateUseCase
    private lateinit var viewModel: DetailViewModel
    private lateinit var candidateDto: CandidateDto

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        bitmapDecoder = mockk<BitmapDecoder>()
        loadCandidateUseCase = mockk<LoadCandidateUseCase>()
        convertEurToGbpUseCase = mockk<ConvertEurToGbpUseCase>()
        updateFavoriteUseCase = mockk<UpdateFavoriteUseCase>()
        deleteCandidateUseCase = mockk<DeleteCandidateUseCase>()
        viewModel = DetailViewModel(
            dispatcher,
            loadCandidateUseCase,
            convertEurToGbpUseCase,
            updateFavoriteUseCase,
            deleteCandidateUseCase,
            bitmapDecoder
        )

        val age = 50
        val birthdate = createBirthdateForAge(age)
        val salaryCentsInEur = 100L
        val bytes = ByteArray(1)
        val bitmap = mockk<Bitmap>(relaxed = true)
        every { bitmapDecoder.decode(any()) } returns bitmap

        candidateDto = CandidateDto(
            candidateId = 1,
            firstname = "firstname1",
            lastname = "lastname1",
            photoByteArray = bytes,
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
    fun loadCandidate_shouldCallUseCasesAndUpdateUiState() = runTest {
        val convertedSalary = 150L
        val expectedCandidateDisplay = candidateDto.toCandidateDisplay(convertedSalary, bitmapDecoder)
        coEvery { loadCandidateUseCase.execute(any()) } returns candidateDto
        coEvery { convertEurToGbpUseCase.execute(any()) } returns convertedSalary

        viewModel.loadCandidate(candidateDto.candidateId)
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
    fun loadCandidate_withNoCandidate_shouldCallUseCasesAndUpdateUiState() = runTest {
        coEvery { loadCandidateUseCase.execute(any()) } returns null

        viewModel.loadCandidate(candidateDto.candidateId)
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.NoCandidateFound)

        coVerify { loadCandidateUseCase.execute(any()) }
    }

    @Test
    fun loadCandidate_withFailureToLoad_shouldCallUseCasesAndUpdateUiState() = runTest {
        coEvery { loadCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.loadCandidate(candidateDto.candidateId)
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.ErrorState)

        coVerify { loadCandidateUseCase.execute(any()) }
    }

    @Test
    fun toggleFavoriteStatus_shouldUpdateCandidate() = runTest {
        preloadCandidate()
        advanceUntilIdle()
        val idCapture = slot<Long>()
        val isFavoriteCapture = slot<Boolean>()
        coEvery {
            updateFavoriteUseCase.execute(
                capture(idCapture),
                capture(isFavoriteCapture)
            )
        } returns Unit

        viewModel.toggleFavoriteStatus()
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.CandidateFound)
        assertEquals(candidateDto.candidateId, idCapture.captured)
        assertEquals(!candidateDto.isFavorite, isFavoriteCapture.captured)
        coVerify { updateFavoriteUseCase.execute(any(), any()) }
    }

    @Test
    fun toggleFavoriteStatus_withErrorWhileUpdating_shouldUpdateState() = runTest {
        preloadCandidate()
        advanceUntilIdle()
        coEvery { updateFavoriteUseCase.execute(any(), any()) } throws Exception("Fake exception")

        viewModel.toggleFavoriteStatus()
        advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertTrue(state is DetailViewModel.DetailUiState.ErrorState)
        coVerify { updateFavoriteUseCase.execute(any(), any()) }
    }

    @Test
    fun toggleFavoriteStatus_whileInWrongState_shouldReturn() = runTest {
        coEvery { loadCandidateUseCase.execute(any()) } returns null
        viewModel.loadCandidate(candidateDto.candidateId)
        advanceUntilIdle()

        viewModel.toggleFavoriteStatus()
        advanceUntilIdle()

        coVerify(exactly = 0) { updateFavoriteUseCase.execute(any(), any()) }
    }

    @Test
    fun deleteCandidate_shouldCallUseCaseAndUpdateState() = runTest {
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
    fun deleteCandidate_withFailure_shouldCallUseCaseAndUpdateState() = runTest {
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
    fun deleteCandidate_withWrongState_shouldReturnAndNotCallUseCase() = runTest {
        coEvery { loadCandidateUseCase.execute(any()) } returns null
        viewModel.loadCandidate(candidateDto.candidateId)
        advanceUntilIdle()
        coEvery { deleteCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.deleteCandidate(1L)
        advanceUntilIdle()

        coVerify(exactly = 0) { deleteCandidateUseCase.execute(any()) }
    }

    private fun preloadCandidate() {
        val convertedSalary = 150L
        coEvery { loadCandidateUseCase.execute(any()) } returns candidateDto
        coEvery { convertEurToGbpUseCase.execute(any()) } returns convertedSalary
        viewModel.loadCandidate(candidateDto.candidateId)
    }
}
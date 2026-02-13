package com.openclassrooms.vitesseapp.presentation

import android.graphics.Bitmap
import com.openclassrooms.vitesseapp.TestDispatcherProvider
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.presentation.viewmodel.AddViewModel
import com.openclassrooms.vitesseapp.presentation.model.CandidateFormUI
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AddViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatcher = TestDispatcherProvider(testDispatcher)
    private lateinit var saveCandidateUseCase: SaveCandidateUseCase
    private lateinit var viewModel: AddViewModel
    private lateinit var candidateFormUi: CandidateFormUI
    private lateinit var expectedCandidateDto: CandidateDto

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        saveCandidateUseCase = mockk<SaveCandidateUseCase>()
        viewModel = AddViewModel(dispatcher, saveCandidateUseCase)

        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)
        val bytes = ByteArray(1)
        val bitmap = mockk<Bitmap>(relaxed = true)

        candidateFormUi = CandidateFormUI(
            firstname = "firstname",
            lastname = "lastname",
            photoBitmap = bitmap,
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            salaryInEur = 1,
            notes = null,
        )

        expectedCandidateDto = CandidateDto(
            candidateId = 0,
            firstname = "firstname",
            lastname = "lastname",
            photoByteArray = ByteArray(0),
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
    fun saveCandidateTest_shouldCallUseCasesAndUpdateState() = runTest {

        val candidateDtoCapture = slot<CandidateDto>()
        coEvery { saveCandidateUseCase.execute(capture(candidateDtoCapture)) } returns Unit

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.addUiState.value
        assertTrue(state is AddViewModel.AddUiState.LoadedState)
        assertEquals(expectedCandidateDto, candidateDtoCapture.captured)
        coVerify { saveCandidateUseCase.execute(any()) }
    }

    @Test
    fun saveCandidateTest_withErrorWhileSavingCandidate_shouldUpdateUiState() = runTest {

        coEvery { saveCandidateUseCase.execute(any()) } throws Exception("Fake exception")

        viewModel.saveCandidate(candidateFormUi)
        advanceUntilIdle()

        val state = viewModel.addUiState.value
        assertTrue(state is AddViewModel.AddUiState.ErrorState)
        coVerify { saveCandidateUseCase.execute(any()) }
    }
}
package com.openclassrooms.vitesseapp.presentation

import android.graphics.Bitmap
import com.openclassrooms.vitesseapp.TestDispatcherProvider
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import com.openclassrooms.vitesseapp.presentation.mapper.formatBirthdateToString
import com.openclassrooms.vitesseapp.presentation.viewmodel.HomeViewModel
import com.openclassrooms.vitesseapp.presentation.model.CandidateDisplay
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatcher = TestDispatcherProvider(testDispatcher)
    private lateinit var bitmapDecoder: BitmapDecoder
    private lateinit var loadAllCandidatesUseCase: LoadAllCandidatesUseCase
    private lateinit var viewModel: HomeViewModel
    private lateinit var allCandidatesDto: List<CandidateDto>
    private lateinit var filteredCandidates: List<CandidateDisplay>
    private lateinit var filter: String

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        bitmapDecoder = mockk<BitmapDecoder>()
        loadAllCandidatesUseCase = mockk<LoadAllCandidatesUseCase>()
        viewModel = HomeViewModel(
            dispatcher,
            loadAllCandidatesUseCase,
            bitmapDecoder
        )

        filter = "firstname1"
        val age = 50
        val birthdate = createBirthdateForAge(age)
        val bytes = ByteArray(0)
        val bitmap = mockk<Bitmap>(relaxed = true)
        every { bitmapDecoder.decode(any()) } returns bitmap

        allCandidatesDto = listOf(
            CandidateDto(
                candidateId = 1,
                firstname = filter,
                lastname = "lastname1",
                photoByteArray = bytes,
                phone = "123456",
                email = "email",
                birthdate = birthdate,
                notes = null,
                age = age,
                salaryCentsInEur = null,
            ),
            CandidateDto(
                candidateId = 2,
                firstname = "firstname2",
                lastname = "lastname2",
                photoByteArray = bytes,
                phone = "123456",
                email = "email",
                birthdate = 1L,
                age = 1,
                notes = null,
                salaryCentsInEur = null,
                isFavorite = true,
            ),
        )

        filteredCandidates = listOf(
            CandidateDisplay(
                candidateId = 1,
                firstname = filter,
                lastname = "lastname1",
                photoBitmap = bitmap,
                phone = "123456",
                email = "email",
                birthdate = formatBirthdateToString(birthdate),
                notes = null,
                salaryInEur = null,
                age = age,
            ),
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadAllCandidates_shouldUpdateUiState() = runTest {

        every { loadAllCandidatesUseCase.execute() } returns flowOf(allCandidatesDto)

        viewModel.loadAllCandidates()
        advanceUntilIdle()

        val state = viewModel.homeUiState.value
        assertTrue(state is HomeViewModel.HomeUiState.CandidatesFound)

        val foundState = state as HomeViewModel.HomeUiState.CandidatesFound
        assertEquals(allCandidatesDto.size, foundState.candidates.size)

        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadAllCandidates_withNoCandidates_shouldUpdateUiState() = runTest {

        every { loadAllCandidatesUseCase.execute() } returns flowOf(emptyList())

        viewModel.loadAllCandidates()
        advanceUntilIdle()

        val state = viewModel.homeUiState.value
        assertTrue(state is HomeViewModel.HomeUiState.NoCandidateFound)

        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadAllCandidates_withErrorWhileLoading_shouldUpdateUiState() = runTest {

        every { loadAllCandidatesUseCase.execute() } returns flow { throw Exception("Fake Exception") }

        viewModel.loadAllCandidates()
        advanceUntilIdle()

        val state = viewModel.homeUiState.value
        println(state.toString())
        assertTrue(state is HomeViewModel.HomeUiState.ErrorState)
        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadFavoritesTab_ShouldReturnFavoriteCandidates() = runTest {

        preloadCandidates()
        advanceUntilIdle()

        val result = viewModel.loadFavoritesTab()

        assertTrue(result.size == 1)
        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadFavoritesTab_fromWrongState_ShouldReturnEmptyList() = runTest {

        every { loadAllCandidatesUseCase.execute() } returns flowOf(emptyList())
        viewModel.loadAllCandidates()
        advanceUntilIdle()

        val result = viewModel.loadFavoritesTab()

        assertTrue(result.isEmpty())
        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadFilteredCandidates_shouldUpdateUiState() = runTest {

        preloadCandidates()
        advanceUntilIdle()

        viewModel.loadFilteredCandidates(filter)
        advanceUntilIdle()

        val state = viewModel.homeUiState.value
        assertTrue(state is HomeViewModel.HomeUiState.CandidatesFound)
        state as HomeViewModel.HomeUiState.CandidatesFound
        val result = state.candidates
        println(result)
        assertEquals(filteredCandidates, state.candidates)
        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadFilteredCandidates_withNoFilteredCandidates_ShouldUpdateUiState() = runTest {

        preloadCandidates()
        advanceUntilIdle()

        viewModel.loadFilteredCandidates("wrong filter")
        advanceUntilIdle()

        val state = viewModel.homeUiState.value
        assertTrue(state is HomeViewModel.HomeUiState.NoCandidateFound)
        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadFilteredCandidates_withNoFilter_ShouldNotFilter() = runTest {

        preloadCandidates()
        advanceUntilIdle()

        viewModel.loadFilteredCandidates(" ")
        advanceUntilIdle()

        val state = viewModel.homeUiState.value
        assertTrue(state is HomeViewModel.HomeUiState.CandidatesFound)
        state as HomeViewModel.HomeUiState.CandidatesFound
        assertEquals(allCandidatesDto.size, state.candidates.size)
        verify { loadAllCandidatesUseCase.execute() }
    }

    private fun preloadCandidates() {
        every { loadAllCandidatesUseCase.execute() } returns flowOf(allCandidatesDto)
        viewModel.loadAllCandidates()
    }
}
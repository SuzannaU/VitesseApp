package com.openclassrooms.vitesseapp.ui

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.FilterByNameUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import com.openclassrooms.vitesseapp.ui.home.HomeViewModel
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    val loadAllCandidatesUseCase = mockk<LoadAllCandidatesUseCase>()
    val filterByNameUseCase = mockk<FilterByNameUseCase>()
    val uri = mockk<Uri>(relaxed = true)
    val viewModel = HomeViewModel(loadAllCandidatesUseCase, filterByNameUseCase)
    lateinit var candidates: List<Candidate>
    lateinit var filteredCandidates: List<CandidateDisplay>

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns uri

        candidates = listOf(
            Candidate(
                candidateId = 1,
                firstname = "firstname1",
                lastname = "lastname1",
                photoPath = "path",
                phone = "123456",
                email = "email",
                birthdate = 1L,
                notes = null,
                age = 1,
                salaryCentsInEur = 1,
            ),
            Candidate(
                candidateId = 2,
                firstname = "firstname2",
                lastname = "lastname2",
                photoPath = "path",
                phone = "123456",
                email = "email",
                birthdate = 1L,
                age = 1,
                notes = null,
                salaryCentsInEur = 1,
            ),
        )

        filteredCandidates = listOf(
            CandidateDisplay(
                candidateId = 1,
                firstname = "firstname1",
                lastname = "lastname1",
                photoUri = uri,
                phone = "123456",
                email = "email",
                birthdate = "birthdate",
                notes = null,
                salaryInEur = "salary",
                age = 1,
            ),
        )
    }

    @Test
    fun loadAllCandidates_shouldUpdateUiState() = runTest {

        every { loadAllCandidatesUseCase.execute() } returns flowOf(candidates)

        viewModel.loadAllCandidates()
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        assertTrue(state is HomeViewModel.HomeUiState.CandidatesFound)

        val foundState = state as HomeViewModel.HomeUiState.CandidatesFound
        assertEquals(candidates.size, foundState.candidates.size)

        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadAllCandidates_withNoCandidates_shouldUpdateUiState() = runTest {

        every { loadAllCandidatesUseCase.execute() } returns flowOf(emptyList())

        viewModel.loadAllCandidates()
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        assertTrue(state is HomeViewModel.HomeUiState.NoCandidateFound)

        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadFilteredCandidates_shouldUpdateUiState() = runTest {

        every { loadAllCandidatesUseCase.execute() } returns flowOf(candidates)
        every { filterByNameUseCase.execute(any(), any()) } returns filteredCandidates
        viewModel.loadAllCandidates()

        viewModel.loadFilteredCandidates("filter")
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        assertTrue(state is HomeViewModel.HomeUiState.CandidatesFound)

        val foundState = state as HomeViewModel.HomeUiState.CandidatesFound
        assertEquals(filteredCandidates, foundState.candidates)

        verify { loadAllCandidatesUseCase.execute() }
        verify { filterByNameUseCase.execute(any(), any()) }
    }

    @Test
    fun loadFilteredCandidates_withNoCandidates_ShouldUpdateUiState() = runTest {

        every { loadAllCandidatesUseCase.execute() } returns flowOf(candidates)
        every { filterByNameUseCase.execute(any(), any()) } returns emptyList()
        viewModel.loadAllCandidates()

        viewModel.loadFilteredCandidates("filter")
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        assertTrue(state is HomeViewModel.HomeUiState.NoCandidateFound)

        verify { loadAllCandidatesUseCase.execute() }
        verify { filterByNameUseCase.execute(any(), any()) }
    }

}
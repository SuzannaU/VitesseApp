package com.openclassrooms.vitesseapp.ui

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import com.openclassrooms.vitesseapp.ui.home.HomeViewModel
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
class HomeViewModelTest {

    val loadAllCandidatesUseCase = mockk<LoadAllCandidatesUseCase>()
    val viewModel = HomeViewModel(loadAllCandidatesUseCase)
    lateinit var testScope: TestScope
    lateinit var allCandidates: List<Candidate>
    lateinit var filteredCandidates: List<CandidateDisplay>
    lateinit var filter: String
    val uri = mockk<Uri>(relaxed = true)

    @BeforeEach
    fun setup() {
        testScope = TestScope()
        Dispatchers.setMain(UnconfinedTestDispatcher(testScope.testScheduler))

        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns uri

        filter = "firstname1"
        val age = 50
        val birthdate = createBirthdateForAge(age)

        allCandidates = listOf(
            Candidate(
                candidateId = 1,
                firstname = filter,
                lastname = "lastname1",
                photoPath = "path",
                phone = "123456",
                email = "email",
                birthdate = birthdate,
                notes = null,
                age = age,
                salaryCentsInEur = null,
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
                salaryCentsInEur = null,
            ),
        )

        filteredCandidates = listOf(
            CandidateDisplay(
                candidateId = 1,
                firstname = filter,
                lastname = "lastname1",
                photoUri = uri,
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
    fun loadAllCandidates_shouldUpdateUiState() = testScope.runTest {

        every { loadAllCandidatesUseCase.execute() } returns flowOf(allCandidates)

        viewModel.loadAllCandidates()
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        assertTrue(state is HomeViewModel.HomeUiState.CandidatesFound)

        val foundState = state as HomeViewModel.HomeUiState.CandidatesFound
        assertEquals(allCandidates.size, foundState.candidates.size)

        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadAllCandidates_withNoCandidates_shouldUpdateUiState() = testScope.runTest {

        every { loadAllCandidatesUseCase.execute() } returns flowOf(emptyList())

        viewModel.loadAllCandidates()
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        assertTrue(state is HomeViewModel.HomeUiState.NoCandidateFound)

        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadAllCandidates_withErrorWhileLoading_shouldUpdateUiState() = testScope.runTest {

        every { loadAllCandidatesUseCase.execute() } returns flow { throw Exception("Fake Exception") }

        viewModel.loadAllCandidates()
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        println(state.toString())
        assertTrue(state is HomeViewModel.HomeUiState.ErrorState)
        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadFilteredCandidates_shouldUpdateUiState() = testScope.runTest {

        preloadCandidates()

        viewModel.loadFilteredCandidates(filter)
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        assertTrue(state is HomeViewModel.HomeUiState.CandidatesFound)
        state as HomeViewModel.HomeUiState.CandidatesFound
        assertEquals(filteredCandidates, state.candidates)
        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadFilteredCandidates_withNoFilteredCandidates_ShouldUpdateUiState() = testScope.runTest {

        preloadCandidates()

        viewModel.loadFilteredCandidates("wrong filter")
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        assertTrue(state is HomeViewModel.HomeUiState.NoCandidateFound)
        verify { loadAllCandidatesUseCase.execute() }
    }

    @Test
    fun loadFilteredCandidates_withNoFilter_ShouldNotFilter() = testScope.runTest {

        preloadCandidates()

        viewModel.loadFilteredCandidates(" ")
        advanceUntilIdle()

        val state = viewModel.homeStateFlow.value
        assertTrue(state is HomeViewModel.HomeUiState.CandidatesFound)
        state as HomeViewModel.HomeUiState.CandidatesFound
        assertEquals(allCandidates.size, state.candidates.size)
        verify { loadAllCandidatesUseCase.execute() }
    }

    private fun preloadCandidates() {
        every { loadAllCandidatesUseCase.execute() } returns flowOf(allCandidates)
        viewModel.loadAllCandidates()
    }
}
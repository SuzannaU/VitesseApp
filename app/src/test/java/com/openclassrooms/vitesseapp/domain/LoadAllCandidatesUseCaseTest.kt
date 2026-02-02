package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LoadAllCandidatesUseCaseTest {

    val candidateRepository: CandidateRepository = mockk()
    val loadAllCandidatesUseCase = LoadAllCandidatesUseCase(candidateRepository)
    lateinit var candidates: List<Candidate>

    @Test
    fun execute_shouldReturnFlowOfCandidates() = runTest {
        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)

        candidates = listOf(
            Candidate(
                candidateId = 1,
                firstname = "firstname",
                lastname = "lastname",
                photoPath = "path",
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                notes = null,
                age = expectedAge,
                salaryCentsInEur = 1,
            ),
            Candidate(
                candidateId = 2,
                firstname = "firstname",
                lastname = "lastname",
                photoPath = "path",
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                age = expectedAge,
                notes = null,
                salaryCentsInEur = 1,
            ),
        )

        every { candidateRepository.fetchAllCandidates() } returns flowOf(candidates)

        val result = loadAllCandidatesUseCase.execute().first()

        assertEquals(candidates, result)
        verify { candidateRepository.fetchAllCandidates() }
    }
}
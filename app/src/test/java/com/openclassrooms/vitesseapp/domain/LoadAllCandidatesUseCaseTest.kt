package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.data.entity.CandidateDto
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId

class LoadAllCandidatesUseCaseTest {

    val candidateRepository: CandidateRepository = mockk()
    val loadAllCandidatesUseCase = LoadAllCandidatesUseCase(candidateRepository)
    lateinit var candidatesDto: List<CandidateDto>
    lateinit var candidates: List<Candidate>

    @BeforeEach
    fun init() {
        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)

        candidatesDto = listOf(
            CandidateDto(
                candidateId = 1,
                firstname = "firstname",
                lastname = "lastname",
                photoPath = "path",
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                notes = null,
                salaryInEur = 1,
            ),
            CandidateDto(
                candidateId = 2,
                firstname = "firstname",
                lastname = "lastname",
                photoPath = "path",
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                notes = null,
                salaryInEur = 1,
            ),
        )

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
                salaryInEur = 1,
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
                salaryInEur = 1,
            ),
        )
    }

    @Test
    fun execute_shouldReturnFlowOfCandidates() = runTest {

        every { candidateRepository.fetchAllCandidates() } returns flowOf(candidatesDto)

        val result = loadAllCandidatesUseCase.execute().first()

        assertEquals(candidates, result)
        verify { candidateRepository.fetchAllCandidates() }


    }

    private fun createBirthdateForAge(age: Int): Long {
        return LocalDate.now()
            .minusYears(age.toLong())
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
    }
}
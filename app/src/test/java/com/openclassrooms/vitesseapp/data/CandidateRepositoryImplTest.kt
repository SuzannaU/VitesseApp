package com.openclassrooms.vitesseapp.data

import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.data.repository.CandidateRepositoryImpl
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CandidateRepositoryImplTest {

    val candidateDao: CandidateDao = mockk()
    val repository: CandidateRepository = CandidateRepositoryImpl(candidateDao = candidateDao)

    @Test
    fun saveCandidate_shouldSendCandidateToDao() = runTest {

        val candidate = Candidate(
            firstname = "firstname",
            lastname = "lastname",
            photoPath = "path",
            phone = "123456",
            email = "email",
            birthdate = 1L,
            notes = null,
            salaryCentsInEur = 1,
            age = null,
        )

        val expectedCandidateDto = CandidateDto(
            firstname = "firstname",
            lastname = "lastname",
            photoPath = "path",
            phone = "123456",
            email = "email",
            birthdate = 1L,
            notes = null,
            salaryCentsInEur = 1,
        )

        val candidateDtoCapture = slot<CandidateDto>()
        coEvery { candidateDao.saveCandidate(capture(candidateDtoCapture)) } returns Unit

        repository.saveCandidate(candidate)

        assertEquals(expectedCandidateDto, candidateDtoCapture.captured)
        coVerify { candidateDao.saveCandidate(any()) }
    }

    @Test
    fun fetchAllCandidates_shouldCallDaoAndReturnFlowOfCandidate() = runTest {
        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)

        val candidatesDto = listOf(
            CandidateDto(
                candidateId = 1,
                firstname = "firstname",
                lastname = "lastname",
                photoPath = "path",
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                notes = null,
                salaryCentsInEur = 1,
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
                salaryCentsInEur = 1,
            ),
        )

        val candidates = listOf(
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

        every { candidateDao.getAllCandidates() } returns flowOf(candidatesDto)

        val result = repository.fetchAllCandidates().first()

        assertEquals(candidates, result)
        verify { candidateDao.getAllCandidates() }
    }
}
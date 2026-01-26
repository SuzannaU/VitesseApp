package com.openclassrooms.vitesseapp.data

import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.data.repository.CandidateRepositoryImpl
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CandidateRepositoryImplTest {

    val candidateDao: CandidateDao = mockk()
    val repository: CandidateRepository = CandidateRepositoryImpl(candidateDao = candidateDao)
    lateinit var candidateDto: CandidateDto
    lateinit var candidatesDto: List<CandidateDto>

    @BeforeEach
    fun init() {
        candidateDto = CandidateDto(
            candidateId = 1,
            firstname = "firstname",
            lastname = "lastname",
            photoPath = "path",
            phone = "123456",
            email = "email",
            birthdate = 1L,
            notes = null,
            salaryInEur = 1,
        )
        candidatesDto = listOf(
            CandidateDto(
                candidateId = 1,
                firstname = "firstname",
                lastname = "lastname",
                photoPath = "path",
                phone = "123456",
                email = "email",
                birthdate = 1L,
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
                birthdate = 1L,
                notes = null,
                salaryInEur = 1,
            ),
        )
    }

    @Test
    fun saveCandidate_shouldCallDao() = runTest {

        coEvery { candidateDao.saveCandidate(any()) } returns Unit

        repository.saveCandidate(candidateDto)

        coVerify { candidateDao.saveCandidate(any()) }
    }

    @Test
    fun fetchAllCandidates_shouldCallDaoAndReturnFlowOfCandidateDto() = runTest {

        every { candidateDao.getAllCandidates() } returns flowOf(candidatesDto)

        val result = repository.fetchAllCandidates().first()

        assertEquals(candidatesDto, result)
        verify { candidateDao.getAllCandidates() }
    }
}
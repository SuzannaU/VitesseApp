package com.openclassrooms.vitesseapp.data

import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.data.repository.CandidateRepositoryImpl
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CandidateRepositoryImplTest {

    val candidateDao: CandidateDao = mockk()
    val repository: CandidateRepository = CandidateRepositoryImpl(candidateDao = candidateDao)

    @Test
    fun saveCandidateTest_shouldCallDao() = runTest {
        val candidateDto = CandidateDto(
            candidateId = 1,
            firstname = "firstname",
            lastname = "lastname",
            photo = "path",
            phone = "123456",
            email = "email",
            birthdate = 1L,
            notes = null,
            salaryInEur = 1,
        )

        coEvery { candidateDao.saveCandidate(any()) } returns Unit

        repository.saveCandidate(candidateDto)

        coVerify { candidateDao.saveCandidate(any()) }
    }
}
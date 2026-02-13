package com.openclassrooms.vitesseapp.data

import com.openclassrooms.vitesseapp.data.dao.CandidateDao
import com.openclassrooms.vitesseapp.data.entity.CandidateEntity
import com.openclassrooms.vitesseapp.data.repository.CandidateRepositoryImpl
import com.openclassrooms.vitesseapp.domain.createBirthdateForAge
import com.openclassrooms.vitesseapp.domain.model.CandidateDto
import com.openclassrooms.vitesseapp.domain.repository.CandidateRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CandidateRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val candidateDao: CandidateDao = mockk()
    private val repository: CandidateRepository = CandidateRepositoryImpl(candidateDao = candidateDao)

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchCandidate_shouldCallDaoAndReturnCandidate() = runTest {
        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)
        val bytes = ByteArray(1)
        val candidateEntity = CandidateEntity(
            candidateId = 1,
            firstname = "firstname",
            lastname = "lastname",
            photoByteArray = bytes,
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            notes = null,
            salaryCentsInEur = 1,
        )
        val candidateDto = CandidateDto(
            candidateId = 1,
            firstname = "firstname",
            lastname = "lastname",
            photoByteArray = bytes,
            phone = "123456",
            email = "email",
            birthdate = birthdateMillis,
            notes = null,
            age = expectedAge,
            salaryCentsInEur = 1,
        )
        val idCapture = slot<Long>()
        coEvery { candidateDao.getCandidateById(capture(idCapture)) } returns candidateEntity

        val result = repository.fetchCandidate(1L)
        assertEquals(candidateDto, result)
        assertEquals(1L, idCapture.captured)
        coVerify { candidateDao.getCandidateById(any()) }
    }

    @Test
    fun fetchCandidate_withNoCandidateFound_shouldCallDaoAndReturnNull() = runTest {
        val idCapture = slot<Long>()
        coEvery { candidateDao.getCandidateById(capture(idCapture)) } returns null

        val result = repository.fetchCandidate(1L)
        assertNull(result)
        assertEquals(1L, idCapture.captured)
        coVerify { candidateDao.getCandidateById(any()) }
    }

    @Test
    fun saveCandidate_shouldSendCandidateToDao() = runTest {

        val bytes = ByteArray(1)
        val candidateDto = CandidateDto(
            firstname = "firstname",
            lastname = "lastname",
            photoByteArray = bytes,
            phone = "123456",
            email = "email",
            birthdate = 1L,
            notes = null,
            salaryCentsInEur = 1,
            age = null,
        )
        val expectedCandidateEntity = CandidateEntity(
            firstname = "firstname",
            lastname = "lastname",
            photoByteArray = bytes,
            phone = "123456",
            email = "email",
            birthdate = 1L,
            notes = null,
            salaryCentsInEur = 1,
        )
        val candidateEntityCapture = slot<CandidateEntity>()
        coEvery { candidateDao.saveCandidate(capture(candidateEntityCapture)) } returns Unit

        repository.saveCandidate(candidateDto)

        assertEquals(expectedCandidateEntity, candidateEntityCapture.captured)
        coVerify { candidateDao.saveCandidate(any()) }
    }

    @Test
    fun updateCandidateIsFavorite_shouldSendCandidateDataToDao() = runTest {

        val candidateId = 1L
        val isFavorite = true
        val idCapture = slot<Long>()
        val isFavoriteCapture = slot<Boolean>()
        coEvery { candidateDao.updateCandidateIsFavorite(capture(idCapture), capture(isFavoriteCapture)) } returns Unit

        repository.updateCandidateIsFavorite(candidateId, isFavorite)

        assertEquals(candidateId, idCapture.captured)
        assertEquals(isFavorite, isFavoriteCapture.captured)
        coVerify { candidateDao.updateCandidateIsFavorite(any(), any()) }
    }

    @Test
    fun deleteCandidate_shouldSendCandidateIdToDao() = runTest {

        val candidateId = 1L
        val idCapture = slot<Long>()
        coEvery { candidateDao.deleteCandidateById(capture(idCapture)) } returns Unit

        repository.deleteCandidate(candidateId)

        assertEquals(candidateId, idCapture.captured)
        coVerify { candidateDao.deleteCandidateById(any()) }
    }

    @Test
    fun fetchAllCandidates_shouldCallDaoAndReturnFlowOfCandidate() = runTest {
        val expectedAge = 30
        val birthdateMillis = createBirthdateForAge(expectedAge)
        val bytes = ByteArray(1)

        val candidatesDto = listOf(
            CandidateEntity(
                candidateId = 1,
                firstname = "firstname",
                lastname = "lastname",
                photoByteArray = bytes,
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                notes = null,
                salaryCentsInEur = 1,
            ),
            CandidateEntity(
                candidateId = 2,
                firstname = "firstname",
                lastname = "lastname",
                photoByteArray = bytes,
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                notes = null,
                salaryCentsInEur = 1,
            ),
        )

        val candidateDtos = listOf(
            CandidateDto(
                candidateId = 1,
                firstname = "firstname",
                lastname = "lastname",
                photoByteArray = bytes,
                phone = "123456",
                email = "email",
                birthdate = birthdateMillis,
                notes = null,
                age = expectedAge,
                salaryCentsInEur = 1,
            ),
            CandidateDto(
                candidateId = 2,
                firstname = "firstname",
                lastname = "lastname",
                photoByteArray = bytes,
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

        assertEquals(candidateDtos, result)
        verify { candidateDao.getAllCandidates() }
    }
}
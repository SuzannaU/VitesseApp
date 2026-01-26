package com.openclassrooms.vitesseapp.domain

import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.FilterByNameUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class FilterByNameUseCaseTest {

    val filterByNameUseCase = FilterByNameUseCase()

    @ParameterizedTest
    @MethodSource("provider")
    fun execute_shouldFilterCandidates(
        candidates: List<Candidate>,
        filter: String?,
        expectedSize: Int,
    ) {

        val result = filterByNameUseCase.execute(candidates, filter)
        assertEquals(expectedSize, result.size)
    }


    companion object {
        @JvmStatic
        fun provider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    candidates,
                    "lastname1",
                    1
                ),
                Arguments.of(
                    candidates,
                    "firstname1",
                    1
                ),
                Arguments.of(
                    candidates,
                    "noName",
                    0
                ),
                Arguments.of(
                    candidates,
                    "",
                    2
                ),
                Arguments.of(
                    candidates,
                    null,
                    2
                ),
            )
        }

        val candidates = listOf(
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
                salaryInEur = 1,
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
                salaryInEur = 1,
            ),
        )
    }
}
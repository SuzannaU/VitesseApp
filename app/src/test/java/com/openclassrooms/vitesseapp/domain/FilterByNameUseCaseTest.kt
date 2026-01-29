package com.openclassrooms.vitesseapp.domain

import android.net.Uri
import com.openclassrooms.vitesseapp.domain.usecase.FilterByNameUseCase
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import io.mockk.mockk
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
        candidates: List<CandidateDisplay>,
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

        val uri = mockk<Uri>()
        val candidates = listOf(
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
            ),
            CandidateDisplay(
                candidateId = 2,
                firstname = "firstname2",
                lastname = "lastname2",
                photoUri = uri,
                phone = "123456",
                email = "email",
                birthdate = "birthdate",
                notes = null,
                salaryInEur = "salary",
            ),
        )
    }
}
package com.openclassrooms.vitesseapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.ConvertEurToGbpUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import com.openclassrooms.vitesseapp.ui.model.toCandidateDisplay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val loadCandidateUseCase: LoadCandidateUseCase,
    private val convertEurToGbpUseCase: ConvertEurToGbpUseCase,
) : ViewModel() {

    private val _detailStateFlow = MutableStateFlow<DetailUiState>(DetailUiState.LoadingState)
    val detailUiState = _detailStateFlow.asStateFlow()
    private lateinit var candidateDisplay: CandidateDisplay

    fun loadCandidate(candidateId: Long) {
        viewModelScope.launch {
            delay(500)         // for demonstration purposes
            val candidate = loadCandidateUseCase.execute(candidateId)
            val salaryGbp = convertEurToGbpUseCase.execute(candidate.salaryCentsInEur)

            candidateDisplay = candidate.toCandidateDisplay(salaryGbp)
            _detailStateFlow.value = DetailUiState.CandidateFound(candidateDisplay)
        }
    }

    sealed class DetailUiState {
        object LoadingState : DetailUiState()

        data class CandidateFound(
            val candidate: CandidateDisplay,
        ) : DetailUiState()
    }
}
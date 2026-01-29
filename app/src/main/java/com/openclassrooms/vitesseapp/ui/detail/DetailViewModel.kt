package com.openclassrooms.vitesseapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import com.openclassrooms.vitesseapp.ui.model.toCandidateDisplay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val loadCandidateUseCase: LoadCandidateUseCase,
) : ViewModel() {

    private val _detailStateFlow = MutableStateFlow<DetailUiState>(DetailUiState.LoadingState)
    val detailUiState = _detailStateFlow.asStateFlow()
    private lateinit var candidate: CandidateDisplay

    fun loadCandidate(candidateId: Long) {
        viewModelScope.launch {
            delay(1000)         // for demonstration purposes
            candidate = loadCandidateUseCase.execute(candidateId).toCandidateDisplay()
            _detailStateFlow.value = DetailUiState.CandidateFound(candidate)
        }
    }

    sealed class DetailUiState {
        object LoadingState : DetailUiState()

        data class CandidateFound(
            val candidate: CandidateDisplay,
        ) : DetailUiState()
    }
}
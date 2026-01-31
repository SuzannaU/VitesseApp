package com.openclassrooms.vitesseapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.ConvertEurToGbpUseCase
import com.openclassrooms.vitesseapp.domain.usecase.DeleteCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import com.openclassrooms.vitesseapp.ui.model.toCandidateDisplay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val loadCandidateUseCase: LoadCandidateUseCase,
    private val convertEurToGbpUseCase: ConvertEurToGbpUseCase,
    private val saveCandidateUseCase: SaveCandidateUseCase,
    private val deleteCandidateUseCase: DeleteCandidateUseCase,
) : ViewModel() {

    private val _detailStateFlow = MutableStateFlow<DetailUiState>(DetailUiState.LoadingState)
    val detailUiState = _detailStateFlow.asStateFlow()
    private lateinit var loadedCandidate: Candidate
    private lateinit var candidateDisplay: CandidateDisplay

    fun loadCandidate(candidateId: Long) {
        viewModelScope.launch {
            delay(500)         // for demonstration purposes
            loadedCandidate = loadCandidateUseCase.execute(candidateId)
            val salaryGbp = convertEurToGbpUseCase.execute(loadedCandidate.salaryCentsInEur)

            candidateDisplay = loadedCandidate.toCandidateDisplay(salaryGbp)
            _detailStateFlow.value = DetailUiState.CandidateFound(candidateDisplay)
        }
    }

    fun updateFavoriteStatus() {
        _detailStateFlow.value = DetailUiState.LoadingState
        val previousFavoriteStatus = candidateDisplay.isFavorite
        candidateDisplay.isFavorite = !previousFavoriteStatus
        viewModelScope.launch {
            saveCandidateUseCase.execute(
                loadedCandidate.copy(
                    isFavorite = !previousFavoriteStatus
                )
            )
            _detailStateFlow.value = DetailUiState.CandidateFound(candidateDisplay)
        }
    }

    fun deleteCandidate(candidateId: Long) {
        _detailStateFlow.value = DetailUiState.LoadingState
        viewModelScope.launch {
            deleteCandidateUseCase.execute(candidateId)
        }
    }

    sealed class DetailUiState {
        object LoadingState : DetailUiState()

        data class CandidateFound(
            val candidate: CandidateDisplay,
        ) : DetailUiState()
    }
}
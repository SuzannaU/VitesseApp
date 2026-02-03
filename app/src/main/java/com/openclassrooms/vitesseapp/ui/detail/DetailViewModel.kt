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
    private var loadedCandidate: Candidate? = null

    fun loadCandidate(candidateId: Long) {
        viewModelScope.launch {
            _detailStateFlow.value = DetailUiState.LoadingState

            runCatching {
                val candidate = loadCandidateUseCase.execute(candidateId) ?: return@runCatching null
                val salaryInCentsGbp = candidate.salaryCentsInEur?.let {
                    convertEurToGbpUseCase.execute(candidate.salaryCentsInEur)
                }
                candidate to candidate.toCandidateDisplay(salaryInCentsGbp)
            }.onSuccess { result ->
                result?.let { (candidate, display) ->
                    loadedCandidate = candidate
                    _detailStateFlow.value = DetailUiState.CandidateFound(display)
                } ?: run {
                    loadedCandidate = null
                    _detailStateFlow.value = DetailUiState.NoCandidateFound
                }
            }.onFailure {
                loadedCandidate = null
                _detailStateFlow.value = DetailUiState.ErrorState
            }
        }
    }

    fun toggleFavoriteStatus() {
        val currentState = _detailStateFlow.value
        if (currentState !is DetailUiState.CandidateFound) return
        val candidate = loadedCandidate ?: return

        val newFavoriteStatus = !currentState.candidateDisplay.isFavorite

        viewModelScope.launch {
            val updatedDisplay = currentState.candidateDisplay.copy(
                isFavorite = newFavoriteStatus
            )
            val updatedCandidate = candidate.copy(
                isFavorite = newFavoriteStatus
            )

            runCatching {
                saveCandidateUseCase.execute(updatedCandidate)
            }.onSuccess {
                _detailStateFlow.value = DetailUiState.CandidateFound(updatedDisplay)
            }.onFailure {
                _detailStateFlow.value = DetailUiState.ErrorState
            }
        }
    }

    fun deleteCandidate(candidateId: Long) {
        val currentState = _detailStateFlow.value
        if (currentState !is DetailUiState.CandidateFound) return

        viewModelScope.launch {
            runCatching {
                deleteCandidateUseCase.execute(candidateId)
            }.onSuccess {
                _detailStateFlow.value = DetailUiState.DeleteSuccess
            }.onFailure {
                _detailStateFlow.value = DetailUiState.ErrorState
            }
        }
    }

    sealed class DetailUiState {
        object LoadingState : DetailUiState()
        object NoCandidateFound : DetailUiState()
        object ErrorState : DetailUiState()
        object DeleteSuccess : DetailUiState()

        data class CandidateFound(
            val candidateDisplay: CandidateDisplay
        ) : DetailUiState()
    }
}
package com.openclassrooms.vitesseapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.ConvertEurToGbpUseCase
import com.openclassrooms.vitesseapp.domain.usecase.DeleteCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.UpdateFavoriteUseCase
import com.openclassrooms.vitesseapp.presentation.DispatcherProvider
import com.openclassrooms.vitesseapp.presentation.BitmapDecoder
import com.openclassrooms.vitesseapp.presentation.model.CandidateDisplay
import com.openclassrooms.vitesseapp.presentation.mapper.toCandidateDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val loadCandidateUseCase: LoadCandidateUseCase,
    private val convertEurToGbpUseCase: ConvertEurToGbpUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val deleteCandidateUseCase: DeleteCandidateUseCase,
    private val bitmapDecoder: BitmapDecoder,
) : ViewModel() {

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.LoadingState)
    val detailUiState = _detailUiState.asStateFlow()

    fun loadCandidate(candidateId: Long) {
        viewModelScope.launch {
            _detailUiState.value = DetailUiState.LoadingState
            runCatching {
                withContext(dispatcherProvider.io) {
                    val candidate = loadCandidateUseCase.execute(candidateId)
                    val salaryInCentsGbp = candidate?.salaryCentsInEur?.let {
                        convertEurToGbpUseCase.execute(candidate.salaryCentsInEur)
                    }
                    candidate?.toCandidateDisplay(salaryInCentsGbp, bitmapDecoder)
                }
            }.onSuccess { candidateDisplay ->
                if (candidateDisplay != null) {
                    _detailUiState.value = DetailUiState.CandidateFound(candidateDisplay)
                } else {
                    _detailUiState.value = DetailUiState.NoCandidateFound
                }
            }.onFailure {
                _detailUiState.value = DetailUiState.ErrorState
            }
        }
    }

    fun toggleFavoriteStatus() {
        val currentState = _detailUiState.value
        if (currentState !is DetailUiState.CandidateFound) return

        val newFavoriteStatus = !currentState.candidateDisplay.isFavorite

        viewModelScope.launch {
            val updatedDisplay = currentState.candidateDisplay.copy(
                isFavorite = newFavoriteStatus
            )

            runCatching {
                withContext(dispatcherProvider.io) {
                    updateFavoriteUseCase.execute(updatedDisplay.candidateId, newFavoriteStatus)
                }
            }.onSuccess {
                _detailUiState.value = DetailUiState.CandidateFound(updatedDisplay)
            }.onFailure {
                _detailUiState.value = DetailUiState.ErrorState
            }
        }
    }

    fun deleteCandidate(candidateId: Long) {
        val currentState = _detailUiState.value
        if (currentState !is DetailUiState.CandidateFound) return

        viewModelScope.launch {
            runCatching {
                withContext(dispatcherProvider.io) {
                    deleteCandidateUseCase.execute(candidateId)
                }
            }.onSuccess {
                _detailUiState.value = DetailUiState.DeleteSuccess
            }.onFailure {
                _detailUiState.value = DetailUiState.ErrorState
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
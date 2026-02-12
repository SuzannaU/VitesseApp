package com.openclassrooms.vitesseapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.LoadCandidateUseCase
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.presentation.DispatcherProvider
import com.openclassrooms.vitesseapp.presentation.BitmapDecoder
import com.openclassrooms.vitesseapp.presentation.model.CandidateFormUI
import com.openclassrooms.vitesseapp.presentation.mapper.toCandidateFormUI
import com.openclassrooms.vitesseapp.presentation.mapper.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val loadCandidateUseCase: LoadCandidateUseCase,
    private val saveCandidateUseCase: SaveCandidateUseCase,
    private val bitmapDecoder: BitmapDecoder,
) : ViewModel() {

    private val _editUiState = MutableStateFlow<EditUiState>(EditUiState.LoadingState)
    val editUiState = _editUiState.asStateFlow()

    fun loadCandidate(candidateId: Long) {
        viewModelScope.launch {
            _editUiState.value = EditUiState.LoadingState
            runCatching {
                withContext(dispatcherProvider.io) {
                    loadCandidateUseCase.execute(candidateId)?.toCandidateFormUI(bitmapDecoder)
                }
            }.onSuccess { candidateFormUI ->
                if (candidateFormUI != null) {
                    _editUiState.value = EditUiState.CandidateFound(candidateFormUI)
                } else {
                    _editUiState.value = EditUiState.NoCandidateFound
                }
            }.onFailure {
                _editUiState.value = EditUiState.ErrorState
            }
        }
    }

    fun saveCandidate(candidateFormUI: CandidateFormUI) {
        viewModelScope.launch {
            _editUiState.value = EditUiState.LoadingState
            runCatching {
                withContext(dispatcherProvider.io) {
                    saveCandidateUseCase.execute(candidateFormUI.toDomain())
                }
            }.onFailure {
                _editUiState.value = EditUiState.ErrorState
                return@launch
            }.onSuccess {
                _editUiState.value = EditUiState.SaveSuccess
            }
        }
    }

    sealed class EditUiState {
        object LoadingState : EditUiState()
        object NoCandidateFound : EditUiState()
        object ErrorState : EditUiState()
        object SaveSuccess : EditUiState()

        data class CandidateFound(
            val candidateFormUI: CandidateFormUI
        ) : EditUiState()
    }
}
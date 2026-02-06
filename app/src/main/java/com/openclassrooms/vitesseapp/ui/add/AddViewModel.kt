package com.openclassrooms.vitesseapp.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.SaveCandidateUseCase
import com.openclassrooms.vitesseapp.ui.DispatcherProvider
import com.openclassrooms.vitesseapp.ui.model.BitmapDecoder
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import com.openclassrooms.vitesseapp.ui.model.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val saveCandidateUseCase: SaveCandidateUseCase,
) : ViewModel() {

    private val _addUiState = MutableStateFlow<AddUiState>(AddUiState.LoadedState)
    val addUiState = _addUiState.asStateFlow()

    fun saveCandidate(candidateFormUI: CandidateFormUI) {
        viewModelScope.launch {
            runCatching {
                withContext(dispatcherProvider.io) {
                    saveCandidateUseCase.execute(candidateFormUI.toDomain())
                }
            }.onFailure {
                _addUiState.value = AddUiState.ErrorState
                return@launch
            }
        }
    }

    sealed class AddUiState {
        object LoadedState : AddUiState()
        object ErrorState : AddUiState()
    }
}
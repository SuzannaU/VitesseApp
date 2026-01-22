package com.openclassrooms.vitesseapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadAllCandidatesUseCase: LoadAllCandidatesUseCase
) : ViewModel() {

    private val _homeStateFlow = MutableStateFlow<HomeUiState>(HomeUiState.LoadingState)
    val homeStateFlow = _homeStateFlow.asStateFlow()

    fun loadAllCandidates() {
        viewModelScope.launch {
            // delay(1000)
            loadAllCandidatesUseCase.execute()
                .collect { candidates ->
                    if (candidates.isEmpty()) {
                        _homeStateFlow.value = HomeUiState.NoCandidateFound
                    } else {
                        _homeStateFlow.value = HomeUiState.CandidatesFound(candidates)
                    }
                }
        }
    }

    sealed class HomeUiState {
        object LoadingState : HomeUiState()

        data class CandidatesFound(
            val candidates: List<Candidate>
        ) : HomeUiState()

        object NoCandidateFound : HomeUiState()
    }
}
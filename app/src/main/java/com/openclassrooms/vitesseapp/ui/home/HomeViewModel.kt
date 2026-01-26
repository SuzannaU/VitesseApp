package com.openclassrooms.vitesseapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.model.Candidate
import com.openclassrooms.vitesseapp.domain.usecase.FilterByNameUseCase
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadAllCandidatesUseCase: LoadAllCandidatesUseCase,
    private val filterByNameUseCase: FilterByNameUseCase,
) : ViewModel() {

    private val _homeStateFlow = MutableStateFlow<HomeUiState>(HomeUiState.LoadingState)
    val homeStateFlow = _homeStateFlow.asStateFlow()
    var allCandidates = emptyList<Candidate>()

    fun loadAllCandidates() {
        viewModelScope.launch {
            delay(1000)         // for demonstration purposes
            loadAllCandidatesUseCase.execute()
                .collect { loadedCandidates ->
                    if (loadedCandidates.isEmpty()) {
                        _homeStateFlow.value = HomeUiState.NoCandidateFound
                    } else {
                        allCandidates = loadedCandidates
                        _homeStateFlow.value = HomeUiState.CandidatesFound(loadedCandidates)
                    }
                }
        }
    }

    fun loadFilteredCandidates(searchedText: String?) {
        _homeStateFlow.value = HomeUiState.LoadingState
        viewModelScope.launch {
            delay(1000)         // for demonstration purposes
            val filteredCandidates = filterByNameUseCase.execute(allCandidates, searchedText)
            if (filteredCandidates.isEmpty()) {
                _homeStateFlow.value = HomeUiState.NoCandidateFound
            } else {
                _homeStateFlow.value =
                    HomeUiState.CandidatesFound(filteredCandidates)
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
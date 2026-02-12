package com.openclassrooms.vitesseapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesseapp.domain.usecase.LoadAllCandidatesUseCase
import com.openclassrooms.vitesseapp.presentation.DispatcherProvider
import com.openclassrooms.vitesseapp.presentation.BitmapDecoder
import com.openclassrooms.vitesseapp.presentation.model.CandidateDisplay
import com.openclassrooms.vitesseapp.presentation.model.filterByName
import com.openclassrooms.vitesseapp.presentation.mapper.toCandidateDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val loadAllCandidatesUseCase: LoadAllCandidatesUseCase,
    private val bitmapDecoder: BitmapDecoder,
) : ViewModel() {

    private val _homeUiState = MutableStateFlow<HomeUiState>(HomeUiState.LoadingState)
    val homeUiState = _homeUiState.asStateFlow()
    private var allCandidates = emptyList<CandidateDisplay>()

    fun loadAllCandidates() {
        viewModelScope.launch {
            _homeUiState.value = HomeUiState.LoadingState
            withContext(dispatcherProvider.io) {
                loadAllCandidatesUseCase.execute()
            }
                .catch {
                    _homeUiState.value = HomeUiState.ErrorState
                    return@catch
                }
                .collect { loadedCandidates ->
                    if (loadedCandidates.isEmpty()) {
                        _homeUiState.value = HomeUiState.NoCandidateFound
                    } else {
                        allCandidates =
                            loadedCandidates.map { candidate -> candidate.toCandidateDisplay(null, bitmapDecoder) }
                        _homeUiState.value = HomeUiState.CandidatesFound(allCandidates)
                    }
                }
        }
    }

    fun loadFavoritesTab() : List<CandidateDisplay> {
        if(_homeUiState.value is HomeUiState.CandidatesFound) {
            val currentCandidates = (_homeUiState.value as HomeUiState.CandidatesFound).candidates
            return currentCandidates.filter { it.isFavorite }
        }
        return emptyList()
    }

    fun loadFilteredCandidates(searchedText: String?) {
        viewModelScope.launch {
            if (searchedText.isNullOrBlank()) {
                _homeUiState.value = HomeUiState.CandidatesFound(allCandidates)
                return@launch
            }
            val filteredCandidates = allCandidates.filterByName(searchedText)
            if (filteredCandidates.isEmpty()) {
                _homeUiState.value = HomeUiState.NoCandidateFound
            } else {
                _homeUiState.value =
                    HomeUiState.CandidatesFound(filteredCandidates)
            }
        }
    }

    sealed class HomeUiState {
        object LoadingState : HomeUiState()
        object NoCandidateFound : HomeUiState()
        object ErrorState : HomeUiState()

        data class CandidatesFound(
            val candidates: List<CandidateDisplay>
        ) : HomeUiState()
    }
}
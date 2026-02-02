package com.openclassrooms.vitesseapp.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentHomeBinding
import com.openclassrooms.vitesseapp.ui.add.AddFragment
import com.openclassrooms.vitesseapp.ui.detail.DetailFragment
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ALL_CANDIDATES_TAB = 0
private const val FAVORITES_TAB = 1

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var adapter: CandidateAdapter
    var candidates: List<CandidateDisplay> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()
        setupTabLayout()
        setupButtons()
        observeUiState()
        viewModel.loadAllCandidates()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.homeStateFlow.collect { uiState ->
                binding.apply {
                    barLoading.isVisible = uiState is HomeViewModel.HomeUiState.LoadingState
                    tvNoCandidates.isVisible = uiState is HomeViewModel.HomeUiState.NoCandidateFound
                    tvError.isVisible = uiState is HomeViewModel.HomeUiState.ErrorState
                    when (uiState) {

                        is HomeViewModel.HomeUiState.CandidatesFound -> {
                            candidates = uiState.candidates
                            adapter.updateCandidates(candidates)
                        }

                        HomeViewModel.HomeUiState.LoadingState -> {
                            candidates = emptyList()
                            adapter.updateCandidates(candidates)
                        }

                        HomeViewModel.HomeUiState.NoCandidateFound -> {
                            candidates = emptyList()
                            adapter.updateCandidates(candidates)
                        }

                        HomeViewModel.HomeUiState.ErrorState -> {
                            candidates = emptyList()
                            adapter.updateCandidates(candidates)
                        }
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        val addFab = binding.fabAdd
        addFab.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, AddFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupTabLayout() {
        val tabLayout = binding.tabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    ALL_CANDIDATES_TAB -> adapter.updateCandidates(candidates)
                    FAVORITES_TAB -> adapter.updateCandidates(
                        candidates.filter { it.isFavorite }
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun setupSearchBar() {
        binding.tietSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.loadFilteredCandidates(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun setupRecyclerView() {
        adapter = CandidateAdapter(candidates, object : OnItemClickListener {
            override fun onItemCLick(item: CandidateDisplay) {
                parentFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        DetailFragment.newInstance(item.candidateId ?: 0)
                    )
                    .addToBackStack(null)
                    .commit()
            }

        })

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.adapter = adapter
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
package com.openclassrooms.vitesseapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentDetailBinding
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ARG_CANDIDATE_ID = "candidateId"

class DetailFragment : Fragment() {

    lateinit var binding: FragmentDetailBinding
    private val viewModel: DetailViewModel by viewModel()
    private var candidateId: Long = 0
    private var loadedCandidate: CandidateDisplay? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            candidateId = it.getLong(ARG_CANDIDATE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        observeUiState()
        viewModel.loadCandidate(candidateId)
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.detailUiState.collect { uiState ->
                binding.barLoading.isVisible = uiState is DetailViewModel.DetailUiState.LoadingState
                if (uiState is DetailViewModel.DetailUiState.CandidateFound) {
                    loadedCandidate = uiState.candidate
                    bindCandidate()
                }
            }
        }
    }

    private fun bindCandidate() {
        loadedCandidate?.let {
            binding.apply {

                val fullname = buildString {
                    append(it.firstname)
                    append(" ")
                    append(it.lastname.uppercase())
                }
                toolbar.title = fullname

                birthdayField.text = getString(
                    R.string.birthdate_age,
                    it.birthdate,
                    it.age
                )
                salaryField.text = getString(
                    R.string.salary_eur,
                    it.salaryInEur
                )
                convertedSalaryField.text = getString(
                    R.string.salary_gbp,
                    it.salaryInGbp
                )
                notesFields.text = it.notes
            }
        }
    }

    private fun setupNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
    }

    companion object {
        fun newInstance(candidateId: Long) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CANDIDATE_ID, candidateId)
                }
            }
    }
}
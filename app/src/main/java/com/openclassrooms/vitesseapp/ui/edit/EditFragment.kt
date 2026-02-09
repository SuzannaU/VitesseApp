package com.openclassrooms.vitesseapp.ui.edit

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil3.imageLoader
import coil3.load
import coil3.request.ImageRequest
import coil3.size.Scale
import coil3.size.ViewSizeResolver
import coil3.toBitmap
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentEditBinding
import com.openclassrooms.vitesseapp.presentation.viewmodel.EditViewModel
import com.openclassrooms.vitesseapp.presentation.mapper.formatBirthdateToString
import com.openclassrooms.vitesseapp.presentation.mapper.formatSalaryToString
import com.openclassrooms.vitesseapp.ui.home.HomeFragment
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import com.openclassrooms.vitesseapp.ui.helpers.setupFormDatePicker
import com.openclassrooms.vitesseapp.ui.helpers.setupFormEmailListener
import com.openclassrooms.vitesseapp.ui.helpers.setupFormPhotoPicker
import com.openclassrooms.vitesseapp.ui.helpers.validateFormField
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ARG_CANDIDATE_ID = "candidateId"

class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private val viewModel: EditViewModel by viewModel()

    private var birthdateMillis: Long = 0
    private var photoBitmap: Bitmap? = null
    private var candidateId: Long = 0
    private var loadedCandidate: CandidateFormUI? = null

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
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUiState()
        viewModel.loadCandidate(candidateId)
        setupNavigation()
        setupDatePicker()
        setupPhotoPicker()
        setupSaveButton()
        setupEmailListener()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.editUiState.collect { uiState ->
                binding.barLoading.isVisible = uiState is EditViewModel.EditUiState.LoadingState
                binding.tvError.isVisible = uiState is EditViewModel.EditUiState.ErrorState
                binding.tvNoCandidate.isVisible =
                    uiState is EditViewModel.EditUiState.NoCandidateFound
                binding.includeForm.formScrollview.isVisible =
                    uiState is EditViewModel.EditUiState.CandidateFound
                when (uiState) {
                    is EditViewModel.EditUiState.CandidateFound -> {
                        loadedCandidate = uiState.candidateFormUI
                        bindCandidate()
                    }

                    is EditViewModel.EditUiState.SaveSuccess -> {
                        parentFragmentManager
                            .beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                HomeFragment.newInstance()
                            )
                            .commit()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun bindCandidate() {
        loadedCandidate?.let { candidate ->
            binding.includeForm.apply {

                if (candidate.photoBitmap == null) {
                    ivProfilePhoto.setImageResource(R.drawable.photo_library_72dp)
                } else {
                    ivProfilePhoto.load(candidate.photoBitmap) {
                        scale(Scale.FIT)
                        size(ViewSizeResolver(ivProfilePhoto))
                    }
                    photoBitmap = candidate.photoBitmap
                }

                tietFirstname.setText(candidate.firstname)
                tietLastname.setText(candidate.lastname)
                tietPhone.setText(candidate.phone)
                tietEmail.setText(candidate.email)
                tietBirthdate.setText(formatBirthdateToString(candidate.birthdate))
                birthdateMillis = candidate.birthdate

                candidate.salaryInEur?.let {
                    tietSalary.setText(formatSalaryToString(candidate.salaryInEur))
                }

                tietNotes.setText(candidate.notes)
            }
        }
    }

    private fun setupNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
    }

    private fun setupDatePicker() {
        setupFormDatePicker(
            view = binding.includeForm.tietBirthdate,
        ) { selection ->
            birthdateMillis = selection
        }
    }

    private fun setupPhotoPicker() {
        setupFormPhotoPicker(
            imageView = binding.includeForm.ivProfilePhoto,
        ) { uri ->
            val request = ImageRequest.Builder(requireContext())
                .data(uri)
                .build()
            lifecycleScope.launch {
                photoBitmap = requireContext().imageLoader.execute(request).image?.toBitmap()
            }
        }
    }

    private fun setupSaveButton() {
        binding.apply {
            btnSave.setOnClickListener {

                val salaryString = includeForm.tietSalary.text.toString()
                val salaryInEur =
                    if (salaryString.isNotEmpty()) Integer.parseInt(salaryString.trim())
                        .toLong() else null

                val candidate = CandidateFormUI(
                    candidateId = loadedCandidate?.candidateId ?: 0L,
                    firstname = includeForm.tietFirstname.text.toString(),
                    lastname = includeForm.tietLastname.text.toString(),
                    photoBitmap = photoBitmap,
                    phone = includeForm.tietPhone.text.toString(),
                    email = includeForm.tietEmail.text.toString(),
                    birthdate = birthdateMillis,
                    salaryInEur = salaryInEur,
                    notes = includeForm.tietNotes.text.toString(),
                )

                if (validateCandidate(candidate)) {
                    lifecycleScope.launch {
                        viewModel.saveCandidate(candidate)
                    }
                }
            }
        }
    }

    private fun validateCandidate(candidate: CandidateFormUI): Boolean {
        var isValid = true
        binding.includeForm.apply {
            isValid = validateFormField(!candidate.firstname.isBlank(), tipFirstname) && isValid
            isValid = validateFormField(!candidate.lastname.isBlank(), tipLastname) && isValid
            isValid = validateFormField(!candidate.phone.isBlank(), tipPhone) && isValid
            isValid = validateFormField(!candidate.email.isBlank(), tipEmail) && isValid
            isValid = validateFormField(candidate.birthdate != 0L, tipBirthdate) && isValid

            if(Patterns.EMAIL_ADDRESS.matcher(tietEmail.text.toString()).matches()) {
                isValid = true && isValid
            } else {
                tipEmail.error = getString(R.string.invalid_format)
                isValid = false
            }
        }
        return isValid
    }

    private fun setupEmailListener() {
        setupFormEmailListener(
            binding.includeForm.tipEmail,
            binding.includeForm.tietEmail
        )
    }

    companion object {
        fun newInstance(candidateId: Long) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CANDIDATE_ID, candidateId)
                }
            }
    }
}
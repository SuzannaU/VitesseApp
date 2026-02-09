package com.openclassrooms.vitesseapp.ui.add

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
import coil3.request.ImageRequest
import coil3.toBitmap
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentAddBinding
import com.openclassrooms.vitesseapp.presentation.viewmodel.AddViewModel
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import com.openclassrooms.vitesseapp.ui.helpers.setupFormDatePicker
import com.openclassrooms.vitesseapp.ui.helpers.setupFormEmailListener
import com.openclassrooms.vitesseapp.ui.helpers.setupFormPhotoPicker
import com.openclassrooms.vitesseapp.ui.helpers.validateFormField
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private val viewModel: AddViewModel by viewModel()

    private var birthdateMillis: Long = 0
    private var photoBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUiState()
        setupNavigation()
        setupDatePicker()
        setupPhotoPicker()
        setupSaveButton()
        setupEmailListener()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.addUiState.collect { uiState ->
                binding.tvError.isVisible = uiState is AddViewModel.AddUiState.ErrorState
                binding.includeForm.formScrollview.isVisible =
                    uiState is AddViewModel.AddUiState.LoadedState
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

                val salaryString = includeForm.tietSalary.text.toString().trim()
                val salaryInEur =
                    if (salaryString.isNotEmpty()) Integer.parseInt(salaryString.trim()).toLong() else null

                val candidate = CandidateFormUI(
                    firstname = includeForm.tietFirstname.text.toString().trim(),
                    lastname = includeForm.tietLastname.text.toString().trim(),
                    photoBitmap = photoBitmap,
                    phone = includeForm.tietPhone.text.toString().trim(),
                    email = includeForm.tietEmail.text.toString().trim(),
                    birthdate = birthdateMillis,
                    salaryInEur = salaryInEur,
                    notes = includeForm.tietNotes.text.toString().trim(),
                )

                if (validateCandidate(candidate)) {
                    lifecycleScope.launch {
                        viewModel.saveCandidate(candidate)
                    }
                    parentFragmentManager.popBackStackImmediate()
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
        fun newInstance() = AddFragment()
    }
}
package com.openclassrooms.vitesseapp.ui.edit

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil3.load
import coil3.size.Scale
import coil3.size.ViewSizeResolver
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentEditBinding
import com.openclassrooms.vitesseapp.ui.formatBirthdateToString
import com.openclassrooms.vitesseapp.ui.formatSalaryToString
import com.openclassrooms.vitesseapp.ui.model.CandidateFormUI
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ARG_CANDIDATE_ID = "candidateId"

class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private val viewModel: EditViewModel by viewModel()

    private var birthdateMillis: Long = 0
    private var photoUri: Uri? = null
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

                    else -> Unit
                }
            }
        }
    }

    private fun bindCandidate() {
        loadedCandidate?.let { candidate ->
            binding.includeForm.apply {

                candidate.photoUri?.let { uri ->
                    ivProfilePhoto.load(uri) {
                        scale(Scale.FIT)
                        size(ViewSizeResolver(ivProfilePhoto))
                    }
                    photoUri = uri
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

        val constraintsBuilder = CalendarConstraints.Builder().setValidator(
            DateValidatorPointBackward.now()
        )

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.select_a_date)
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        binding.includeForm.tietBirthdate.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            birthdateMillis = selection
            binding.includeForm.tietBirthdate.setText(datePicker.headerText)
        }
    }

    private fun setupPhotoPicker() {

        binding.includeForm.ivProfilePhoto.apply {
            setImageResource(R.drawable.photo_library_72dp)

            val pickPhotoLauncher =
                registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    if (uri != null) {
                        photoUri = uri
                        setImageURI(uri)
                    }
                }

            setOnClickListener {
                pickPhotoLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }

    private fun setupSaveButton() {
        binding.apply {
            btnSave.setOnClickListener {

                val salaryString = includeForm.tietSalary.text.toString()
                val salaryInEur =
                    if (salaryString.isNotEmpty()) Integer.parseInt(salaryString.trim()) else null

                val candidate = CandidateFormUI(
                    candidateId = loadedCandidate?.candidateId ?: 0L,
                    firstname = includeForm.tietFirstname.text.toString(),
                    lastname = includeForm.tietLastname.text.toString(),
                    photoUri = photoUri,
                    phone = includeForm.tietPhone.text.toString(),
                    email = includeForm.tietEmail.text.toString(),
                    birthdate = birthdateMillis,
                    salaryInEur = salaryInEur?.toLong(),
                    notes = includeForm.tietNotes.text.toString(),
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
            isValid = validateField(!candidate.firstname.isBlank(), tipFirstname) && isValid
            isValid = validateField(!candidate.lastname.isBlank(), tipLastname) && isValid
            isValid = validateField(!candidate.phone.isBlank(), tipPhone) && isValid
            isValid = validateField(!candidate.email.isBlank(), tipEmail) && isValid
            isValid = validateField(candidate.birthdate != 0L, tipBirthdate) && isValid
        }
        return isValid
    }

    private fun validateField(
        validCondition: Boolean,
        tip: TextInputLayout,
    ): Boolean {

        val errorMessage = getString(R.string.mandatory_field)
        if (validCondition) {
            tip.error = null
            return true
        } else {
            tip.error = errorMessage
            return false
        }
    }

    private fun setupEmailListener() {
        binding.includeForm.tietEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (Patterns.EMAIL_ADDRESS.matcher(binding.includeForm.tietEmail.text.toString())
                        .matches()
                ) {
                    binding.includeForm.tipEmail.error = null
                } else {
                    binding.includeForm.tipEmail.error = getString(R.string.invalid_format)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

        })
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
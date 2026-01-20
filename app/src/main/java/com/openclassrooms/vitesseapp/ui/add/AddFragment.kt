package com.openclassrooms.vitesseapp.ui.add

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
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentAddBinding
import com.openclassrooms.vitesseapp.ui.CandidateUI
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private val viewModel: AddViewModel by viewModel()

    private var birthdateMillis: Long = 0
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupDatePicker()
        setupPhotoPicker()
        setupSaveButton()
        setupEmailListener()
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

        binding.tietBirthdate.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            birthdateMillis = selection
            binding.tietBirthdate.setText(datePicker.headerText)
        }
    }

    private fun setupPhotoPicker() {

        binding.profilePhoto.apply {
            setImageResource(R.drawable.photo_library_72dp_on_surface_variant)

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

                val salaryString = tietSalary.text.toString()
                val salaryInEur =
                    if (salaryString.isNotEmpty()) Integer.parseInt(salaryString.trim()) else null

                val candidate = CandidateUI(
                    firstname = tietFirstname.text.toString(),
                    lastname = tietLastname.text.toString(),
                    photoUri = photoUri,
                    phone = tietPhone.text.toString(),
                    email = tietEmail.text.toString(),
                    birthdate = birthdateMillis,
                    salaryInEur = salaryInEur,
                    notes = tietNotes.text.toString(),
                )

                if (validateCandidate(candidate)) {
                    viewModel.saveCandidate(candidate)
                    // TODO : go back to home screen
                }
            }
        }
    }

    private fun validateCandidate(candidate: CandidateUI): Boolean {
        var isValid = true
        binding.apply {
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
    ) : Boolean {

        val errorMessage = getString(R.string.mandatory_field)
        if(validCondition) {
            tip.error = null
            return true
        } else {
            tip.error = errorMessage
            return false
        }
    }

    private fun setupEmailListener() {
        binding.tietEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (Patterns.EMAIL_ADDRESS.matcher(binding.tietEmail.text.toString())
                        .matches()
                ) {
                    binding.tipEmail.error = null
                } else {
                    binding.tipEmail.error = getString(R.string.invalid_format)
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
        fun newInstance() = AddFragment()
    }
}
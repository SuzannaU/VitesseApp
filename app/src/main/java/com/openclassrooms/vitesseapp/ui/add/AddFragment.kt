package com.openclassrooms.vitesseapp.ui.add

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentAddBinding
import com.openclassrooms.vitesseapp.ui.CandidateFromForm
import okio.IOException
import okio.use
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AddFragment : Fragment() {

    companion object {
        fun newInstance() = AddFragment()
    }

    private lateinit var binding: FragmentAddBinding
    private val viewModel: AddViewModel by viewModel()

    private lateinit var datePicker: MaterialDatePicker<Long>
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
        setupClickListeners()
    }

    private fun setupNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
    }

    private fun setupDatePicker() {
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.select_a_date)
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
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

    private fun setupClickListeners() {
        binding.apply {
            btnSave.setOnClickListener {

                val salaryString = tietSalary.text.toString()
                val salaryInEur =
                    if (salaryString.isNotEmpty()) Integer.parseInt(salaryString) else null

                val candidate = CandidateFromForm(
                    firstname = tietFirstname.text.toString(),
                    lastname = tietLastname.text.toString(),
                    photoUri = photoUri,
                    phone = tietPhone.text.toString(),
                    email = tietEmail.text.toString(),
                    birthdate = birthdateMillis,
                    salaryInEur = salaryInEur,
                    notes = tietNotes.text.toString(),
                )

                viewModel.saveCandidate(candidate)
            }
        }
    }
}
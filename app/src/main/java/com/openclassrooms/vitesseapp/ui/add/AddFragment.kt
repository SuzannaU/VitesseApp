package com.openclassrooms.vitesseapp.ui.add

import android.app.FragmentManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.data.entity.CandidateDto
import com.openclassrooms.vitesseapp.databinding.FragmentAddBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddFragment : Fragment() {

    companion object {
        fun newInstance() = AddFragment()
    }

    private lateinit var binding: FragmentAddBinding
    private val viewModel: AddViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = (requireActivity() as AppCompatActivity).supportActionBar
        toolbar?.title = "Ajouter un candidat"

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.select_a_date)
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()

        binding.tietBirthdate.setOnClickListener {
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        binding.btnSave.setOnClickListener {
            val candidate = CandidateDto(
                firstname = binding.tietFirstname.text.toString(),
                lastname = binding.tietLastname.text.toString(),
                photo = "",
                phone = "",
                email = "",
                birthdate = 1,
                salaryInEur = 1,
                notes = "",
            )

            viewModel.saveCandidate(candidate)
        }
    }
}
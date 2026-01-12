package com.openclassrooms.vitesseapp.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

        binding.buttonSave.setOnClickListener {
            val candidate = CandidateDto(
                firstname = binding.tietFirstname.text.toString(),
                lastname = binding.editTextId.text.toString(),
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
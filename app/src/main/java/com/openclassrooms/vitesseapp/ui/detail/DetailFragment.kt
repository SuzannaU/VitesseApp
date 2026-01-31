package com.openclassrooms.vitesseapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentDetailBinding
import com.openclassrooms.vitesseapp.ui.model.CandidateDisplay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.net.toUri
import androidx.core.view.get

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

        observeUiState()
        viewModel.loadCandidate(candidateId)
        setupNavigation()
        setupClickListeners()
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

                val starItem = toolbar.menu.findItem(R.id.star_icon)
                starItem.setIcon(
                    if(loadedCandidate?.isFavorite ?: false) R.drawable.baseline_star_24 else R.drawable.outline_star_24
                )

                birthdayField.text = getString(
                    R.string.birthdate_age,
                    it.birthdate,
                    it.age
                )

                if (it.salaryInEur != null) {
                    salaryField.text = getString(
                        R.string.salary_eur,
                        it.salaryInEur
                    )
                    convertedSalaryField.text = getString(
                        R.string.salary_gbp,
                        it.salaryInGbp
                    )
                } else {
                    salaryField.text = getString(R.string.not_available)
                }
                notesFields.text = it.notes
            }
        }
    }

    private fun setupNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            callButton.setOnClickListener { dialPhoneNumber() }
            smsButton.setOnClickListener { sendSms() }
            emailButton.setOnClickListener { sensEmail() }
            toolbar.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.star_icon -> {
                        onStarClicked(item)
                        true
                    }
                    R.id.edit_icon -> {
                        onEditClicked()
                        true
                    }
                    R.id.trash_icon -> {
                        onTrashClicked()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun dialPhoneNumber() {
        val phoneNumber = loadedCandidate?.phone ?: return
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent);
        } else {
            Log.w("DetailFragment", "dialing $phoneNumber is impossible");
            Toast.makeText(requireActivity(), R.string.dial_impossible, Toast.LENGTH_SHORT).show();
        }
    }

    private fun sendSms() {
        val phoneNumber = loadedCandidate?.phone ?: return
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$phoneNumber".toUri()
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent);
        } else {
            Log.w("DetailFragment", "sending SMS to $phoneNumber is impossible");
            Toast.makeText(requireActivity(), R.string.sms_impossible, Toast.LENGTH_SHORT).show();
        }
    }

    private fun sensEmail() {
        val email = loadedCandidate?.email ?: return
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent);
        } else {
            Log.w("DetailFragment", "sending email to $email is impossible");
            Toast.makeText(requireActivity(), R.string.email_impossible, Toast.LENGTH_SHORT).show();
        }
    }

    private fun onStarClicked(item: MenuItem) {
        viewModel.updateFavoriteStatus()
        val iconRes = if(loadedCandidate?.isFavorite ?: false) R.drawable.baseline_star_24 else R.drawable.outline_star_24
        item.setIcon(iconRes)
    }

    private fun onEditClicked() {
        Toast.makeText(requireActivity(), "edit clicked, not implemented yet", Toast.LENGTH_SHORT).show();
    }

    private fun onTrashClicked() {
        Toast.makeText(requireActivity(), "trash clicked, not implemented yet", Toast.LENGTH_SHORT).show();
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
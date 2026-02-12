package com.openclassrooms.vitesseapp.presentation.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil3.load
import coil3.size.Scale
import coil3.size.ViewSizeResolver
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentDetailBinding
import com.openclassrooms.vitesseapp.presentation.viewmodel.DetailViewModel
import com.openclassrooms.vitesseapp.presentation.ui.edit.EditFragment
import com.openclassrooms.vitesseapp.presentation.ui.home.HomeFragment
import com.openclassrooms.vitesseapp.presentation.model.CandidateDisplay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ARG_CANDIDATE_ID = "candidateId"

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
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
        setupDialogResultListener()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.detailUiState.collect { uiState ->
                binding.barLoading.isVisible = uiState is DetailViewModel.DetailUiState.LoadingState
                binding.tvNoCandidate.isVisible =
                    uiState is DetailViewModel.DetailUiState.NoCandidateFound
                binding.tvError.isVisible = uiState is DetailViewModel.DetailUiState.ErrorState
                binding.detailScrollview.isVisible =
                    uiState is DetailViewModel.DetailUiState.CandidateFound
                when (uiState) {
                    is DetailViewModel.DetailUiState.CandidateFound -> {
                        loadedCandidate = uiState.candidateDisplay
                        bindCandidate()
                    }

                    DetailViewModel.DetailUiState.DeleteSuccess -> {
                        navigateToHomeFragment()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun bindCandidate() {
        loadedCandidate?.let { candidateDisplay ->
            binding.apply {

                val fullname = buildString {
                    append(candidateDisplay.firstname)
                    append(" ")
                    append(candidateDisplay.lastname.uppercase())
                }
                toolbar.title = fullname

                val starItem = toolbar.menu.findItem(R.id.star_icon)
                starItem.setIcon(
                    if (candidateDisplay.isFavorite
                    ) R.drawable.baseline_star_24 else R.drawable.outline_star_24
                )

                if (candidateDisplay.photoBitmap == null) {
                    ivProfilePhoto.setImageResource(R.drawable.photo_library_72dp)
                } else {
                    ivProfilePhoto.load(candidateDisplay.photoBitmap) {
                        scale(Scale.FIT)
                        size(ViewSizeResolver(ivProfilePhoto))
                    }
                }

                birthdayField.text = getString(
                    R.string.birthdate_age,
                    candidateDisplay.birthdate,
                    candidateDisplay.age
                )

                if (candidateDisplay.salaryInEur != null) {
                    salaryField.text = getString(
                        R.string.salary_eur,
                        candidateDisplay.salaryInEur
                    )
                    convertedSalaryField.text = getString(
                        R.string.salary_gbp,
                        candidateDisplay.salaryInGbp
                    )
                } else {
                    salaryField.text = getString(R.string.not_available)
                }
                notesFields.text = candidateDisplay.notes
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
                when (item.itemId) {
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
            startActivity(intent)
        } else {
            Log.w("DetailFragment", "dialing $phoneNumber is impossible")
            Toast.makeText(requireActivity(), R.string.dial_impossible, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSms() {
        val phoneNumber = loadedCandidate?.phone ?: return
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$phoneNumber".toUri()
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Log.w("DetailFragment", "sending SMS to $phoneNumber is impossible")
            Toast.makeText(requireActivity(), R.string.sms_impossible, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sensEmail() {
        val email = loadedCandidate?.email ?: return
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Log.w("DetailFragment", "sending email to $email is impossible")
            Toast.makeText(requireActivity(), R.string.email_impossible, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onStarClicked(item: MenuItem) {
        viewModel.toggleFavoriteStatus()
        val iconRes = if (loadedCandidate?.isFavorite
                ?: false
        ) R.drawable.baseline_star_24 else R.drawable.outline_star_24
        item.setIcon(iconRes)
    }

    private fun onEditClicked() {
        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container,
                EditFragment.newInstance(loadedCandidate?.candidateId ?: 0)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun onTrashClicked() {
        DeleteDialogFragment().show(childFragmentManager, "DELETE_DIALOG")
    }

    private fun setupDialogResultListener() {
        childFragmentManager.setFragmentResultListener(
            DeleteDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            if (result.getBoolean("confirmed")) {
                viewModel.deleteCandidate(loadedCandidate?.candidateId ?: 0)
            }
        }
    }

    private fun navigateToHomeFragment() {
        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container,
                HomeFragment.newInstance()
            )
            .commit()
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
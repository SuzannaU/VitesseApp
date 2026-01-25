package com.openclassrooms.vitesseapp.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.vitesseapp.databinding.FragmentDetailBinding

private const val ARG_CANDIDATE_ID = "candidateId"

class DetailFragment : Fragment() {
    private var candidateId: Long = 0
    lateinit var binding : FragmentDetailBinding


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

        binding.detail.text = candidateId.toString()
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
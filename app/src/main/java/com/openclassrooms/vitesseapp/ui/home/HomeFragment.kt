package com.openclassrooms.vitesseapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.vitesseapp.databinding.FragmentHomeBinding
import com.openclassrooms.vitesseapp.domain.model.Candidate
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var adapter: CandidateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var candidates: List<Candidate> = emptyList()

        adapter = CandidateAdapter(candidates)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.candidateFlow.collect { candidatesValue ->
                candidates = candidatesValue
                adapter.updateCandidates(candidates)
            }
        }

        val tabLayout = binding.tabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> adapter.updateCandidates(candidates)
                    1 -> adapter.updateCandidates(candidates.filter { it.isFavorite })
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }
}
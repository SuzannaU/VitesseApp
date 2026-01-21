package com.openclassrooms.vitesseapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.openclassrooms.vitesseapp.R
import com.openclassrooms.vitesseapp.databinding.FragmentHomeBinding
import com.openclassrooms.vitesseapp.ui.CandidateUI

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
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

        val candidatesAll = listOf(
            CandidateUI(
                firstname = "firstname1",
                lastname = "lastname1",
                photoUri = null,
                phone = "123456",
                email = "email1",
                birthdate = 1L,
                salaryInEur = null,
                notes = null,
            ),
            CandidateUI(
                firstname = "firstname2",
                lastname = "lastname2",
                photoUri = null,
                phone = "123456",
                email = "email2",
                birthdate = 1L,
                salaryInEur = null,
                notes = null,
            ),
            CandidateUI(
                firstname = "firstname3",
                lastname = "lastname3",
                photoUri = null,
                phone = "123456",
                email = "email3",
                birthdate = 1L,
                salaryInEur = null,
                notes = null,
            ),
        )

        val candidatesFav = listOf(
            CandidateUI(
                firstname = "firstname4",
                lastname = "lastname4",
                photoUri = null,
                phone = "123456",
                email = "email1",
                birthdate = 1L,
                salaryInEur = null,
                notes = null,
            ),
            CandidateUI(
                firstname = "firstname5",
                lastname = "lastname5",
                photoUri = null,
                phone = "123456",
                email = "email2",
                birthdate = 1L,
                salaryInEur = null,
                notes = null,
            ),
            CandidateUI(
                firstname = "firstname6",
                lastname = "lastname6",
                photoUri = null,
                phone = "123456",
                email = "email3",
                birthdate = 1L,
                salaryInEur = null,
                notes = null,
            ),
        )

        adapter = CandidateAdapter(candidatesAll)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        val tabLayout = binding.tabLayout
//        val viewPager = binding.viewPager

//        val tab0 = TabLayout.Tab()
//        tab0.view = binding.tabAll.findViewById(R.id.tab_all)
//        tabLayout.addTab(tab0)

//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            when(position) {
//                0 -> tab.text = "position 0"
//                1 -> tab.text = "position 1"
//            }
//        }



        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> adapter.updateCandidates(candidatesAll)
                    1 -> adapter.updateCandidates(candidatesFav)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }
}
package com.openclassrooms.vitesseapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.vitesseapp.databinding.ItemCandidateBinding
import com.openclassrooms.vitesseapp.ui.CandidateUI

class CandidateAdapter(var candidates: List<CandidateUI>) : RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CandidateViewHolder {

        val binding = ItemCandidateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CandidateViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CandidateViewHolder,
        position: Int
    ) {
        holder.bind(candidates[position])
    }

    override fun getItemCount(): Int {
        return candidates.size
    }

    fun updateCandidates(candidates: List<CandidateUI>) {
        this.candidates = candidates
        notifyDataSetChanged()
    }

    inner class CandidateViewHolder(val binding: ItemCandidateBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(candidate: CandidateUI) {
            binding.apply {
                tvName.text = candidate.firstname
                tvNotes.text = candidate.notes
            }
        }
    }


}
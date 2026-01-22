package com.openclassrooms.vitesseapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.vitesseapp.databinding.ItemCandidateBinding
import com.openclassrooms.vitesseapp.domain.model.Candidate

class CandidateAdapter(var candidates: List<Candidate>) :
    RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CandidateViewHolder {

        val binding =
            ItemCandidateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun updateCandidates(candidates: List<Candidate>) {
        this.candidates = candidates
        notifyDataSetChanged()
    }

    class CandidateViewHolder(val binding: ItemCandidateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(candidate: Candidate) {
            binding.apply {
                ivThumbnail.setImageURI(candidate.photoPath?.toUri())
                tvName.text = buildString {
                    append(candidate.firstname)
                    append(" ")
                    append(candidate.lastname.uppercase())
                }
                tvNotes.text = candidate.notes
            }
        }
    }


}
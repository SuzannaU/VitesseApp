package com.openclassrooms.vitesseapp.presentation.ui.detail

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.openclassrooms.vitesseapp.R

class DeleteDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.deletion))
            .setMessage(getString(R.string.deletion_message))
            .setPositiveButton(getString(R.string.confirm)) { dialog, id ->
                setFragmentResult("delete_request", bundleOf("confirmed" to true))
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, id ->
                setFragmentResult("delete_request", bundleOf("confirmed" to false))
            }
            .create()
    }

    companion object {
        const val REQUEST_KEY = "delete_request"
    }
}
package com.openclassrooms.vitesseapp.presentation.ui.helpers

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.vitesseapp.R

fun Fragment.setupFormDatePicker(
    view: TextInputEditText,
    onDateSelected: (Long) -> Unit
) {

    val constraintsBuilder = CalendarConstraints.Builder().setValidator(
        DateValidatorPointBackward.now()
    )

    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText(R.string.select_a_date)
        .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
        .setCalendarConstraints(constraintsBuilder.build())
        .build()

    view.setOnClickListener {
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    datePicker.addOnPositiveButtonClickListener { selection ->
        onDateSelected(selection)
        view.setText(datePicker.headerText)
    }
}

fun Fragment.setupFormPhotoPicker(
    imageView: ImageView,
    onPhotoSelected: (Uri) -> Unit
) {

    imageView.apply {

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    onPhotoSelected(uri)
                    setImageURI(uri)
                }
            }

        setOnClickListener {
            pickPhotoLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }
}

fun Fragment.validateFormField(
    validCondition: Boolean,
    textInputLayout: TextInputLayout,
): Boolean {

    val errorMessage = getString(R.string.mandatory_field)
    if (validCondition) {
        textInputLayout.error = null
        return true
    } else {
        textInputLayout.error = errorMessage
        return false
    }
}

fun Fragment.setupFormEmailListener(
    textInputLayout: TextInputLayout,
    editText: TextInputEditText,
) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (Patterns.EMAIL_ADDRESS.matcher(editText.text.toString())
                    .matches()
            ) {
                textInputLayout.error = null
            } else {
                textInputLayout.error = getString(R.string.invalid_format)
            }
        }

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
        }
    })
}
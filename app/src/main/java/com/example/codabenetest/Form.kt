package com.example.codabenetest

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.codabenetest.databinding.FormBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


/**
 * Form : Fragment
 * Fragment of the form to put a new reference with it corresponding expiry date
 */
class Form : Fragment() {

    private var _binding: FormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FormBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cancel Button
        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        // Date Picker
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build()
        datePicker.addOnPositiveButtonClickListener { date ->
            val dateTime: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault())
            val dateAsFormattedText: String = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            binding.editTextDate.setText(dateAsFormattedText)
        }
        binding.textLayoutDate.setStartIconOnClickListener {
            datePicker.show(childFragmentManager, "tag");
        }

        // Add Button
        val editTextDate = binding.editTextDate
        val editTextGTIN = binding.editTextGTIN
        binding.addButton.setOnClickListener {
            val userDate = editTextDate.text
            val gtin = editTextGTIN.text
            if (gtin == null || gtin.isEmpty()) {
                editTextGTIN.error = "Please fill this field"
            } else {
                if (userDate == null || !isCorrect(userDate)) {
                    editTextDate.error = "Please provide a properly formatted date : dd/mm/yyyy"
                } else {
                    addNew(gtin, userDate)
                }

            }
        }

    }

    /**
     * isCorrect : function
     * Parameters : input : Editable
     * Return : Boolean
     * Check if the input of the date editText has the good format
     */
    private fun isCorrect(input : Editable): Boolean {
        if (input.length != 10) {
            return false
        }
        for (i in listOf(0, 1, 3, 4, 6, 7, 8, 9)) {
            if (!input[i].isDigit()) {
                return false
            }
        }
        for (i in listOf(2, 5)) {
            if (input[i].compareTo('/') != 0) {
                return false
            }
        }
        val month = input.subSequence(3, 5).toString().toInt()
        if (month < 1 || month > 12) {
            return false
        }
        val day = input.subSequence(0, 2).toString().toInt()
        for (i in 1..12) {
            if (month == i) {
                if (i % 2 == 0) {
                    if (day < 1 || day > 30) {
                        return false
                    }
                } else {
                    if (day < 1 || day > 31) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /**
     * addNew : function
     * Parameters : newGtin : Editable
     *              newDate : Editable
     * Add a new reference
     * If the reference already exists, it update the expiry date only if it's shortest
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun addNew (newGtin : Editable, newDate : Editable) {
        // Check if the date is shortest
        for (map in MainActivity.gtinArray) {
            if (newGtin.toString() == map["First Line"]) {
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                val newDateFormat: Date = sdf.parse(newDate.toString())
                val mapDateFormat: Date = sdf.parse(map["Second Line"])
                if (newDateFormat < mapDateFormat) {
                    // Update the expiry date
                    map["Second Line"] = newDate.toString()
                    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                } else {
                    view?.let {
                        Snackbar.make(it, "There is already a shortest expiry date", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                }
                return
            }
        }
        // Add to the list
        val new: MutableMap<String, String> = HashMap(2)
        new["First Line"] = newGtin.toString()
        new["Second Line"] = newDate.toString()
        MainActivity.gtinArray.add(new)
        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.codabenetest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.navigation.fragment.findNavController
import com.example.codabenetest.databinding.DataListBinding

/**
 * DataList : Fragment
 * Fragment of the inventory
 */
class DataList : Fragment() {

    private var _binding: DataListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataListBinding.inflate(inflater, container, false)

        // Adapter
        val adapter = SimpleAdapter(
            context,
            MainActivity.gtinArray,
            android.R.layout.simple_list_item_2,
            arrayOf("First Line", "Second Line"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        // ListView
        val listView: ListView = binding.inventory
        listView.adapter = adapter

        // Floating button
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
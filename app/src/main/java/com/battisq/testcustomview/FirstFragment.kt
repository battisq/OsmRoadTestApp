package com.battisq.testcustomview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.battisq.testcustomview.databinding.FragmentFirstBinding
import com.battisq.testcustomview.sudoku.utils.SudokuConstants.SUDOKU_CELL_COUNT
import com.battisq.testcustomview.sudoku.adapter.SudokuTableAdapter
import com.battisq.testcustomview.sudoku.utils.SudokuConstants.SUDOKU_COLUMN_COUNT

class FirstFragment : Fragment(R.layout.fragment_first) {

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!

    private val adapter = SudokuTableAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFirstBinding.bind(view)

        initSudokuRecycler()
    }

    private fun initSudokuRecycler() {
        binding.sudokuTable.apply {
            layoutManager = GridLayoutManager(requireContext(), SUDOKU_COLUMN_COUNT)
            adapter = this@FirstFragment.adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.battisq.testcustomview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.battisq.testcustomview.databinding.FragmentFirstBinding
import com.battisq.testcustomview.sudoku.view.SudokuCellContent
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {

            val isSingleDigits = Random.nextBoolean()

            binding.sudokuCellView.content = if (isSingleDigits) {
                SudokuCellContent.Solution(Random.nextInt(1, 10))
            } else {
                SudokuCellContent.Noted(
                    (1..9)
                        .filter { Random.nextBoolean() }
                        .toIntArray()
                        .also {
                            println()
                        }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
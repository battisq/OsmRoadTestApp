package com.battisq.testcustomview.sudoku.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.battisq.testcustomview.sudoku.utils.SudokuConstants.SUDOKU_CELL_COUNT
import com.battisq.testcustomview.sudoku.view.SudokuCellView

class SudokuTableAdapter : RecyclerView.Adapter<SudokuTableAdapter.ViewHolder>() {

    override fun getItemCount(): Int = SUDOKU_CELL_COUNT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SudokuCellView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    class ViewHolder(itemView: SudokuCellView) : RecyclerView.ViewHolder(itemView)
}


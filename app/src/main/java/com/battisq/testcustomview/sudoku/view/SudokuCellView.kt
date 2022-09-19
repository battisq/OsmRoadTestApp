package com.battisq.testcustomview.sudoku.view

import android.content.AttributionSource
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.battisq.testcustomview.R
import kotlin.math.max
import kotlin.math.min

class SudokuCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
) {

    init {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.SudokuCellView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                solutionNumberColor = getColor(
                    R.styleable.SudokuCellView_scvSolutionNumberColor,
                    Color.BLACK
                )

                notedNumberColor = getColor(
                    R.styleable.SudokuCellView_scvNotedNumberColor,
                    Color.GRAY
                )

                innerPadding = getDimensionPixelSize(
                    R.styleable.SudokuCellView_scvInternalPadding,
                    0,
                )

                solutionNumberSize = getDimensionPixelSize(
                    R.styleable.SudokuCellView_scvSolutionNumberSize,
                    60
                )

                notedNumberSize = getDimensionPixelSize(
                    R.styleable.SudokuCellView_scvNotedNumberSize,
                    38
                )
            } finally {
                recycle()
            }
        }
    }

    var content: SudokuCellContent? = null
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var solutionNumberColor: Int
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var notedNumberColor: Int
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private var innerPadding: Int
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    private var solutionNumberSize: Int
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    private var notedNumberSize: Int
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    private val solutionNumberPaint: Paint
        get() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = solutionNumberSize.toFloat()
            color = solutionNumberColor
        }

    private val notedNumbersPaint: Paint
        get() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = notedNumberSize.toFloat()
            color = solutionNumberColor
        }

    private var internalClipBounds = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val textMeasuredSize = evaluateMinimalContentDimensions()

        val measuredWidth = textMeasuredSize.width() + paddingLeft + paddingRight
        val measuredHeight = textMeasuredSize.height() + paddingTop + paddingBottom

        val resultWidth = when (widthMode) {
            // match_parent
            MeasureSpec.EXACTLY -> widthSize
            // current dp
            MeasureSpec.AT_MOST -> min(widthSize, measuredWidth)
            else -> measuredWidth
        }

        val resultHeight = when (heightMode) {
            // match_parent
            MeasureSpec.EXACTLY -> heightSize
            // current dp
            MeasureSpec.AT_MOST -> min(heightSize, measuredHeight)
            else -> measuredHeight
        }

        setMeasuredDimension(resultWidth, resultHeight)
    }

    private fun evaluateMinimalContentDimensions(): Rect {
        val digits = "0123456789"

        val singleDigitsSize = digits
            .toCharArray()
            .map { c ->
                val rect = Rect()
                solutionNumberPaint.getTextBounds(c.toString(), 0, 1, rect)
                rect
            }
            .fold(Rect()) { acc, rect ->
                Rect(
                    0,
                    0,
                    max(acc.width(), rect.width()),
                    max(acc.height(), rect.height())
                )
            }

        val notesSize = digits
            .chunked(3)
            .map { row ->
                val rect = Rect()
                notedNumbersPaint.getTextBounds(row, 0, row.length, rect)
                rect
            }
            .fold(Rect()) { acc, rect ->
                Rect(
                    0,
                    0,
                    max(rect.width(), acc.width()),
                    rect.height() + acc.height()
                )
            }
            .let { rect ->
                Rect(
                    0,
                    0,
                    rect.width() + 2 * innerPadding,
                    rect.height() + 2 * innerPadding
                )
            }

        return Rect(
            0,
            0,
            max(singleDigitsSize.width(), notesSize.width()),
            max(singleDigitsSize.height(), notesSize.height()),
        )
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let(::drawContent)
        super.onDraw(canvas)
    }

    private fun drawContent(canvas: Canvas) {
        when (val c = content) {
            is SudokuCellContent.Solution -> drawSolution(canvas, c)
            is SudokuCellContent.Noted -> drawNotes(canvas, c)
            else -> {}
        }
    }

    private fun drawSolution(
        canvas: Canvas,
        content: SudokuCellContent.Solution
    ) {
        val text = content.data.toString()

        canvas.getClipBounds(internalClipBounds)

        val availableWidth = internalClipBounds.width() - paddingLeft - paddingRight
        val availableHeight = internalClipBounds.height() - paddingTop - paddingBottom
        val x = (availableWidth / 2F) + paddingLeft
        val y = paddingTop + ((availableHeight / 2F) - ((solutionNumberPaint.descent() +
                solutionNumberPaint.ascent()) / 2F))

        canvas.drawText(text, x, y, solutionNumberPaint)
    }

    private fun drawNotes(
        canvas: Canvas,
        content: SudokuCellContent.Noted
    ) {
        canvas.getClipBounds(internalClipBounds)

        val availableWidth = internalClipBounds.width() - paddingLeft - paddingRight
        val availableHeight = internalClipBounds.height() - paddingTop - paddingBottom

        val horizontalSpacing = availableWidth / 3f
        val verticalSpacing = availableHeight / 3f

        (0 until 3)
            .map { step -> verticalSpacing / 2F + step * verticalSpacing }
            .map { it + paddingTop }
            .map { coordinate -> coordinate - ((notedNumbersPaint.descent() + notedNumbersPaint.ascent()) / 2F) }
            .flatMap { y ->
                (0 until 3)
                    .map { step -> horizontalSpacing / 2F + step * horizontalSpacing }
                    .map { it + paddingLeft }
                    .map { it to y }
            }
            .withIndex()
            .map { it.copy(index = it.index + 1) }
            .filter { content.data.contains(it.index) }
            .forEach {
                val text = it.index.toString()
                val x = it.value.first
                val y = it.value.second
                canvas.drawText(
                    text,
                    x,
                    y,
                    notedNumbersPaint
                )
            }
    }

    override fun onSaveInstanceState(): Parcelable =
        SavedState(super.onSaveInstanceState()).apply {
            content = this@SudokuCellView.content
        }

    override fun onRestoreInstanceState(state: Parcelable?) =
        when(state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                content = state.content
            }
            else -> super.onRestoreInstanceState(state)
        }

    fun setInnerPadding(unit: Int, size: Int) {
        innerPadding = TypedValue
            .applyDimension(unit, size.toFloat(), resources.displayMetrics)
            .toInt()
    }

    fun setSolutionNumberSize(unit: Int, size: Int) {
        solutionNumberSize = TypedValue
            .applyDimension(unit, size.toFloat(), resources.displayMetrics)
            .toInt()
    }

    fun setNotedNumberSize(unit: Int, size: Int) {
        notedNumberSize = TypedValue
            .applyDimension(unit, size.toFloat(), resources.displayMetrics)
            .toInt()
    }

    internal class SavedState : BaseSavedState {
        var content: SudokuCellContent? = null

        constructor(source: Parcel?) : super(source) {
            content = source?.readParcelable(SudokuCellContent::class.java.classLoader)
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeParcelable(content, flags)
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel?): SavedState =
                    SavedState(source)

                override fun newArray(size: Int) =
                    arrayOfNulls<SavedState>(size)

            }
        }
    }
}
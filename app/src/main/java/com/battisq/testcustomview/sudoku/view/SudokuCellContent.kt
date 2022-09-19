package com.battisq.testcustomview.sudoku.view

import android.os.Parcel
import android.os.Parcelable

sealed class SudokuCellContent : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        val type = when(this) {
            is Solution -> ParcelType.SOLUTION
            is Noted -> ParcelType.NOTE
        }
        parcel.writeType(type)
    }

    override fun describeContents() = 0

    data class Solution(val data: Int) : SudokuCellContent() {
        internal constructor(parcel: Parcel) : this(data = parcel.readInt())
    }

    data class Noted(val data: IntArray) : SudokuCellContent() {
        internal constructor(parcel: Parcel) : this(
            data = intArrayOf()
                .also(parcel::readIntArray)
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Noted

            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<SudokuCellContent> {
            override fun createFromParcel(parcel: Parcel): SudokuCellContent =
                when (parcel.readType()) {
                    ParcelType.SOLUTION -> Solution(parcel)
                    ParcelType.NOTE -> Noted(parcel)
                }

            override fun newArray(size: Int) =
                arrayOfNulls<SudokuCellContent>(size)
        }
    }
}

private enum class ParcelType {
    SOLUTION,
    NOTE,
}

private fun Parcel.readType() = readInt().let { ParcelType.values()[it] }
private fun Parcel.writeType(type: ParcelType) = writeInt(type.ordinal)
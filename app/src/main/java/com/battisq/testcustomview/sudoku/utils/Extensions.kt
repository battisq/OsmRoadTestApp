package com.battisq.testcustomview.sudoku.utils

internal fun Int.toCoordinate(): Coordinate {
    val x = this / 9
    val y = this % 9
    return Coordinate(x, y)
}
package com.example.tictactoe

import android.util.Log

class GameStatus {

    var isX: Boolean = false
    var win: Boolean = false
    var draw: Boolean = false
    var started: Boolean = false
    var lastTurn: String? = null
    var newRound: Boolean = false
    var latestWinner: String? = null
    var keepPlaying: Boolean = false

    var row1: Boolean = false
    var row2: Boolean = false
    var row3: Boolean = false

    var column1: Boolean = false
    var column2: Boolean = false
    var column3: Boolean = false

    var diagonal1: Boolean = false
    var diagonal2: Boolean = false

    var drawColumns = false
    var drawRows = false
    var drawDiagonals = false

    var columns: Array<Boolean> = arrayOf(row1, row2, row3)
    var rows: Array<Boolean> = arrayOf(column1, column2, column3)
    var diagonals: Array<Boolean> = arrayOf(diagonal1, diagonal2)
    var allDrawCombo: Array<Boolean> = arrayOf( drawRows, drawColumns, drawDiagonals)


    fun resetCombinations(){
        draw = false
        for (i in 0..2) {
            allDrawCombo[i] = false
            columns[i] = false
            rows[i] = false
            Log.d("RESETCOMBO", "I = $i ALL: ${allDrawCombo[i]} COLUMNS: ${columns[i]} ROWS: ${rows[i]}")
        }
        for (i in 0..1) diagonals[i] = false
            Log.d("RESETCOMBO", "DIAG 1: ${diagonals[0]} DIAG 2: ${diagonals[1]}")
    }
}

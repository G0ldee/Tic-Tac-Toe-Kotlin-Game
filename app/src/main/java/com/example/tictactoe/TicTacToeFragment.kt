package com.example.tictactoe

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.tictactoe.databinding.TictactoeBinding
private const val TAG = "TicTacToeFRAG"

class TicTacToeFragment : Fragment() {

    var gameState: GameStatus = GameStatus()
    private lateinit var binding: TictactoeBinding
    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var horizArray1: Array<ImageButton>
    private lateinit var horizArray2: Array<ImageButton>
    private lateinit var horizArray3: Array<ImageButton>
    private lateinit var tttGrid: Array<Array<ImageButton>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            val result = bundle.getString("bundleKey")
            Log.d(TAG, " FRAGMENT LISTENER RESULTS:${result}")
            if (result == "Yes") {
                gameState.newRound = true
                if (gameState.lastTurn == player1.name)
                    gameState.lastTurn = player2.name
                else gameState.lastTurn = player1.name

                Log.d(TAG, " GAMESTATE NEWROUND:${gameState.newRound}")
                gameState.resetCombinations()
                resetImageBoxes()
            } else if (result == "No") {
                gameState.resetCombinations()
                resetGame()
            }
            gameState.resetCombinations()
            if(gameState.draw)gameState.draw = false
        }
        binding = TictactoeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            horizArray1 = arrayOf(button1, button2, button3)
            horizArray2 = arrayOf(button4, button5, button6)
            horizArray3 = arrayOf(button7, button8, button9)
            tttGrid = arrayOf(horizArray1, horizArray2, horizArray3)

            actionButton.apply {
                setOnClickListener {
                    if (actionButton.text == context.getText(R.string.play)) {
                        if (textPlayer1.text.isEmpty() || textPlayer2.text.isEmpty()) {
                            Toast.makeText(
                                this.context,
                                "You must enter 2 player names",
                                Toast.LENGTH_SHORT
                            ).show()
                            setNames()
                        } else {
                            Toast.makeText(this.context, "Success", Toast.LENGTH_SHORT).show()
                            initPlayers(textPlayer1.text.toString(), textPlayer2.text.toString())
                            textPlayer1.isEnabled = false
                            textPlayer2.isEnabled = false
                            gameState.started = true
                            actionButton.text = context.getString(R.string.restart)
                            //enableImgBtns(gameState.started)
                            enableImgBtn()
                            setTestChoices()
                        }
                    } else if (actionButton.text == context.getString(R.string.nextgame) ||
                        actionButton.text == context.getString(R.string.restart)
                    ) {
                        if (gameState.latestWinner == null) gameState.lastTurn = player2.name
                        else {
                            if (gameState.latestWinner == player1.name.toString()) {
                                gameState.lastTurn = player2.name
                            }
                            if (gameState.latestWinner == player2.name.toString()) {
                                gameState.lastTurn = player1.name
                            }
                        }
                        gameState.newRound = true
                        gameState.win = false
                        resetImageBoxes()

                        if (gameState.newRound) {
                            actionButton.text = context?.getString(R.string.restart)

                        }
                    }
                }
            }

            //Set OnClickListeners for each Image box to add images.
            // Check for winner, disable option, and isX Flag to image button
            for (gridRow in tttGrid.indices) {
                //Log.d(TAG, "horizontal array: ${tttGrid[gridRow]}")
                for (gridBox in tttGrid[gridRow]) {
                    //Log.d(TAG, "Button: $gridBox")
                    gridBox.setOnClickListener {
                        //If first turn in new game then X starts

                        if (!gameState.keepPlaying && gameState.lastTurn == null) {
                            gridBox.setImageResource(R.drawable.cross)
                            gridBox.scaleType = ImageView.ScaleType.FIT_CENTER
                            gameState.isX = true
                            gridBox.tag = gameState.isX
                            gameState.lastTurn = player1.name
                        } else {
                            //If any turn other than first track/set last turn
                            if (gameState.lastTurn == player1.name) {
                                gridBox.setImageResource(R.drawable.circle)
                                gridBox.scaleType = ImageView.ScaleType.FIT_CENTER
                                gameState.isX = false
                                gridBox.tag = gameState.isX
                                gameState.lastTurn = player2.name
                            } else {
                                gridBox.setImageResource(R.drawable.cross)
                                gridBox.scaleType = ImageView.ScaleType.FIT_CENTER
                                gameState.isX = true
                                gridBox.tag = gameState.isX
                                gameState.lastTurn = player1.name
                            }
                        }
                        gridBox.isEnabled = false
                        checkForWinner()
                    }
                }
            }
        }
        disableImgBtns()
        //displayInfo(gameState.started)
    }

    private fun setTestChoices() {
        binding.button1.setImageResource(R.drawable.cross)
        binding.button1.tag = true
        binding.button2.setImageResource(R.drawable.cross)
        binding.button2.tag = true
        binding.button3.setImageResource(R.drawable.circle)
        binding.button3.tag = false
        binding.button4.setImageResource(R.drawable.circle)
        binding.button4.tag = false
        binding.button5.setImageResource(R.drawable.circle)
        binding.button5.tag = false
        binding.button6.setImageResource(R.drawable.cross)
        binding.button6.tag = true
        //binding.button7.setImageResource(R.drawable.cross)
        //binding.button7.tag = true
        binding.button8.setImageResource(R.drawable.circle)
        binding.button8.tag = false
        //binding.button9.setImageResource(R.drawable.cross)
        //binding.button9.tag = true
    }

    private fun displayInfo(started: Boolean, draw: Boolean) {
        //Depending on GameState choose which DialogFragment to open
        if (!started) {
            val showInfo = InfoFragment()
            showInfo.show((activity as AppCompatActivity).supportFragmentManager, "Info")
        } else {
            if(!draw) {
                val intent = Intent(context, GameEndFragment::class.java)
                intent.putExtra("winner", gameState.latestWinner)
                val showInfo = GameEndFragment(intent)
                showInfo.show((activity as AppCompatActivity).supportFragmentManager, "Info")
            } else {
                Toast.makeText(this.context, "Draw!", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, GameEndFragment::class.java)
                val drawText = "No winner for this match, we have a draw."
                intent.putExtra("drawText", drawText)
                intent.putExtra("draw", gameState.draw)
                val showInfo = GameEndFragment(intent)
                showInfo.show((activity as AppCompatActivity).supportFragmentManager, "Info")
            }
        }
    }

    private fun checkForDraw() {

        gameState.draw = false

        //Check ROWS
        for (gridColumn in 0..2) {
        var isRowsCross = false
        var isRowsCircle = false
        tttGrid[gridColumn].forEach {
            if (it.tag != null) {
                if (it.tag == true) {
                    isRowsCross = true
                }
                if (it.tag == false) {
                    isRowsCircle = true
                }
            }
        }
        if (isRowsCircle && isRowsCross) {
            gameState.rows[gridColumn] = true
            Log.d(TAG, "DRAW ON  ROWS $gridColumn: ${gameState.rows[gridColumn]} + ROW1 ${gameState.row1}")
            }
        }

        //check Columns
        for (gridColumn in 0..2) {
        var isColumnsCross = false
        var isColumnsCircle = false
        tttGrid.forEach {
            if (it[gridColumn].tag != null) {
                if (it[gridColumn].tag == true) {
                    isColumnsCross = true
                }
                if (it[gridColumn].tag == false) {
                    isColumnsCircle = true
                }
            }
        }
        if (isColumnsCircle && isColumnsCross) {
            gameState.columns[gridColumn] = true
            Log.d(TAG, "DRAW ON COLUMNS $gridColumn: ${gameState.columns[gridColumn]}")
        }
        }

        //Check Diagonals
        if (tttGrid[0][0].tag != null && tttGrid[1][1].tag != null ||
            tttGrid[0][0].tag != null && tttGrid[2][2].tag != null ||
            tttGrid[1][1].tag != null && tttGrid[2][2].tag != null) {
            try {
                if (checkNotNull(tttGrid[0][0].tag) != checkNotNull(tttGrid[1][1].tag) ||
                    checkNotNull(tttGrid[0][0].tag) != checkNotNull(tttGrid[2][2].tag) ||
                    checkNotNull(tttGrid[1][1].tag) != checkNotNull(tttGrid[2][2].tag)
                )
                    gameState.diagonals[0] = true
                    Log.d(TAG, "DRAW DIAGONAL 1: ${gameState.diagonals[0]}")

            } catch (_: IllegalStateException) {}
        }
        if(tttGrid[0][2].tag != null && tttGrid[1][1].tag != null ||
           tttGrid[0][2].tag != null && tttGrid[2][0].tag != null ||
           tttGrid[1][1].tag != null && tttGrid[2][0].tag != null) {
            try {
                if (checkNotNull(tttGrid[0][2].tag) != checkNotNull(tttGrid[1][1].tag) ||
                    checkNotNull(tttGrid[0][2].tag) != checkNotNull(tttGrid[2][0].tag) ||
                    checkNotNull(tttGrid[1][1].tag) != checkNotNull(tttGrid[2][0].tag)
                )
                    gameState.diagonals[1] = true
                    Log.d(TAG, "DRAW DIAGONAL 2: ${gameState.diagonals[1]}")
            } catch (_: IllegalStateException) {}
        }

        if (gameState.rows[0] && gameState.rows[1] && gameState.rows[2]) {
            gameState.allDrawCombo[0] = true
            Log.d(TAG, "DRAW ALL ROWS ${gameState.rows}")
        }

        if (gameState.columns[0] && gameState.columns[1] && gameState.columns[2]) {
            gameState.allDrawCombo[1] = true
            Log.d(TAG, "DRAW ALL COLUMNS ${gameState.columns}")
        }

        if (gameState.diagonals[0] && gameState.diagonals[1]) {
            gameState.allDrawCombo[2] = true
            Log.d(TAG, "DRAW ALL DIAGONALS ${gameState.diagonals}")
        }

        if(gameState.allDrawCombo[0] && gameState.allDrawCombo[1] && gameState.allDrawCombo[2]) {
            gameState.draw = true
            Log.d(TAG, "DRAW IN ALL ${gameState.rows}")
        }
        //if (it[gridColumn].tag == true) isCross = true else isCircle = true
    }


    private fun checkForWinner() {
        //Check rows for winner
        checkForDraw()

        gameState.win = false

        for (gridRow in 0..2){     //tttGrid.indices) {
            tttGrid[gridRow].forEach {
                //if(it.drawable.current)
                if (it.tag != null) {
                    if (tttGrid[gridRow][0].tag == tttGrid[gridRow][1].tag &&
                        tttGrid[gridRow][0].tag == tttGrid[gridRow][2].tag) gameState.win = true
                }
            }
        }
        //Check columns for winner
        for (gridColumn in 0..2){
            tttGrid.forEach {
                    if (it[gridColumn].tag != null) {
                        if (tttGrid[0][gridColumn].tag == tttGrid[1][gridColumn].tag &&
                            tttGrid[0][gridColumn].tag == tttGrid[2][gridColumn].tag
                        ) gameState.win = true
                        //for (gridBox in 0..2) {
                        //Log.d(TAG, "I:$gridColumn Y:$it ${it[gridColumn].tag} COLUMN: $gridColumn ${gameState.win}")
                    }
                }
        }
        //Check diagonals for winner
        if(tttGrid[0][0].tag != null &&
            tttGrid[1][1].tag != null &&
            tttGrid[2][2].tag != null) {
            if (tttGrid[0][0].tag == tttGrid[1][1].tag &&
                tttGrid[0][0].tag == tttGrid[2][2].tag
            ) {
                gameState.win = true
                //Log.d(TAG, "Left Diagonal $win")
            }
        } else if(tttGrid[0][2].tag != null &&
                    tttGrid[1][1].tag != null &&
                        tttGrid[2][0].tag != null) {

                if (tttGrid[0][2].tag == tttGrid[1][1].tag &&
                    tttGrid[0][2].tag == tttGrid[2][0].tag
                ) {
                    gameState.win = true
                    //Log.d(TAG, "Right Diagonal $win")*/
                }
            }

        if (gameState.draw) {
            disableImgBtns()
            displayInfo(gameState.started, gameState.draw)
            gameState.draw = false
        }

        //If winner is found stop game and display dialog and scores
        if(gameState.win){
            setScore(gameState.isX)
            disableImgBtns()
            displayInfo(gameState.started, gameState.draw)
            //gameState.started = false
            //enableImgBtns(gameState.started)
        }
    }
/*
    private fun enableImgBtns(started: Boolean) {
        for (gridColumn in 0..2) {
            tttGrid.forEach {
                if (started) {
                    it[gridColumn].isEnabled = true
                } else {
                    it[gridColumn].isEnabled = false
                }
            }
        }
    }
*/
    private fun enableImgBtn() {
        for (gridColumn in 0..2) {
            tttGrid.forEach {
                it[gridColumn].isEnabled = true
            }
        }
    }

    private fun disableImgBtns() {
        for (gridColumn in 0..2){
            tttGrid.forEach {
                it[gridColumn].isEnabled = false
            }
        }
    }

    private fun updateScore() {
        /*
        Update score Textview's text, and color
        Log.d(TAG, "PLAYER 1 SCORE:${player1.score}  PLAYER 2 SCORE: ${player2.score}")
         */
        binding.player1Score.text = player1.score.toString()
        binding.player2Score.text = player2.score.toString()
        if (player1.score == player2.score ){
            binding.player1Score.setTextColor(Color.BLACK)
            binding.player2Score.setTextColor(Color.BLACK)
        }else {
            if (player1.score < player2.score) {
                binding.player1Score.setTextColor(Color.RED)
                binding.player2Score.setTextColor(Color.GREEN)
            } else {
                binding.player1Score.setTextColor(Color.GREEN)
                binding.player2Score.setTextColor(Color.RED)
            }
        }
    }

    //Update Player instance score and gamestate lastWinner
    // call updateScore to display results
    private fun setScore(isX: Boolean) {
        if(isX){
            player1.score++
            gameState.latestWinner = player1.name.toString()
        } else {
            player2.score++
            gameState.latestWinner = player2.name.toString()
        }
        gameState.newRound = false
        updateScore()
    }

    private fun setNames() {
        binding.textPlayer1.setText(getString(R.string.p1name))
        binding.textPlayer2.setText(getString(R.string.p2name))
    }


    private fun resetImageBoxes() {
        for (gridRow in tttGrid.indices) {
            for (gridBox in tttGrid[gridRow]) {

                gridBox.tag = null
                gridBox.setImageResource(0)
                gridBox.isEnabled = true
            }
        }
        //gameState.lastTurn = null
    }

    private fun resetGame() {
        resetImageBoxes()
        disableImgBtns()
        binding.actionButton.text = context?.getString(R.string.play)
        gameState = GameStatus()
        Log.d(TAG,"${gameState.started} + ${gameState.keepPlaying}")
        resetPlayers()
    }
    private fun resetPlayers() {
        binding.textPlayer1.hint = getString(R.string.player1_name)
        binding.textPlayer2.hint = getString(R.string.player2_name)
        binding.textPlayer1.isEnabled = true
        binding.textPlayer2.isEnabled = true
        player1.name = null
        player1.score = 0
        player2.name = null
        player2.score = 0
        updateScore()
    }

    private fun initPlayers(name1: String, name2: String) {
        player1 = Player(name1)
        player2 = Player(name2)
        //Log.d(TAG,"PLAYER NAMES ${player1.name} + ${player2.name}")
    }
}

package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.tictactoe.R.string.congrats
import com.example.tictactoe.databinding.TictactoeBinding

class GameEndFragment(intent: Intent) : DialogFragment() {

    private lateinit var binding: TictactoeBinding
    private var winner: String = intent.getStringExtra("winner").toString()
    private var drawText: String = intent.getStringExtra("drawText").toString()
    private var draw: Boolean = intent.getBooleanExtra("draw", false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TictactoeBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_end, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gameInfo: TextView = view.findViewById(R.id.info)
        val btnYes: Button = view.findViewById(R.id.btnyes)
        val btnNo: Button = view.findViewById(R.id.btnno)
        Log.d("MYTAG", winner)

        if (!draw) {
            val text =
                getString(congrats) + " " + winner + ", you won!" + "\n Would you like to play another round ?"
            gameInfo.text = text
        } else {
            val text =
                "$drawText\n Would you like to play another round ?"
            gameInfo.text = text
        }
        btnYes.setOnClickListener {
            val result = "Yes"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
            dismiss()
        }
        btnNo.setOnClickListener {
            val result = "No"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
            binding.actionButton.text = R.string.play.toString()
            dismiss()
        }
    }
}


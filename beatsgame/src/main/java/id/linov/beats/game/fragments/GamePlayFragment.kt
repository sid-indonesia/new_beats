package id.linov.beats.game.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.linov.beats.game.Game
import id.linov.beats.game.GameUI
import id.linov.beats.game.R
import id.linov.beatslib.BeatsTask
import kotlinx.android.synthetic.main.game_layout.*
import id.linov.beats.game.contactor.ServerContactor

/**
 * Created by Hayi Nukman at 2019-10-20
 * https://github.com/ha-yi
 */

class GamePlayFragment: Fragment() {
    companion object {
        fun create(x: BeatsTask): GamePlayFragment = GamePlayFragment().apply {
            task = x
        }
    }

    lateinit var task: BeatsTask

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameUI.bindState(GameUI.State().apply {
            tileY = task.tileNumber
            tileX = task.tileNumber
        })
        updateSelection()

        vR.setOnClickListener {
            Game.selectedOpt = 'R'
            updateSelection()
        }

        vB.setOnClickListener {
            Game.selectedOpt = 'B'
            updateSelection()
        }

        vY.setOnClickListener {
            Game.selectedOpt = 'Y'
            updateSelection()
        }

        vW.setOnClickListener {
            Game.selectedOpt = 'W'
            updateSelection()
        }

        btnReset.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("RESET")
                .setMessage("Reset Board?")
                .setPositiveButton("OK") { di, _ ->
                    Game.taskActions = mutableMapOf()
                    Game.actions = mutableListOf()
                    gameUI.invalidate()
                    ServerContactor.resetPlay()
                    di.dismiss()
                }
                .setNegativeButton("Cancel") { di, _ ->
                    di.dismiss()
                }.show()
        }

        btnSave.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("FINISH")
                .setMessage("Finish board and send all data to server?")
                .setPositiveButton("OK") { di, _ ->
                    ServerContactor.sendAfinal()
                    activity?.finish()
                    di.dismiss()
                }
                .setNegativeButton("Cancel") { di, _ ->
                    di.dismiss()
                }.show()
        }


    }

    private fun updateSelection() {
        selected.setBackgroundResource(Game.getSelectedOptColor())
    }
}
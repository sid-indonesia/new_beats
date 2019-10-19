package id.linov.beats.game.fragments

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
    }

    private fun updateSelection() {
        selected.setBackgroundResource(Game.getSelectedOptColor())
    }
}
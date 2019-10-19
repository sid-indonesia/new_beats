package id.linov.beats.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.linov.beats.game.R
import kotlinx.android.synthetic.main.fragment_game.*

/**
 * Created by Hayi Nukman at 2019-10-20
 * https://github.com/ha-yi
 */

class HomeGameFrag: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        llGroup.setOnClickListener {
            startGroupTest()
        }

        llPersonal.setOnClickListener {
            startPersonalTest()
        }
    }

    private fun startPersonalTest() {

    }

    private fun startGroupTest() {

    }

}
package id.linov.beats.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.linov.beats.game.R
import kotlinx.android.synthetic.main.waiting_layout.*

/**
 * Created by Hayi Nukman at 2019-10-19
 * https://github.com/ha-yi
 */

class WaitingServerFrags: Fragment() {
    var text: String = "Waiting for BEATS Local Server"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.waiting_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        msg.text = text
    }

}
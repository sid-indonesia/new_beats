package id.linov.beats.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.linov.beats.game.R
import id.linov.beatslib.User
import kotlinx.android.synthetic.main.fragment_user_info.*

/**
 * Created by Hayi Nukman at 2019-10-19
 * https://github.com/ha-yi
 */

class UserInfoFrags: Fragment() {
    var callback: ((User) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSave.setOnClickListener {
            saveUserInfo()
        }
    }

    private fun saveUserInfo() {
        if (inputNama.text.toString().isBlank()) {
            inputNama.error = "nama tidak boleh kosong"
            return
        }

        if (inputGender.text.toString().isBlank()) {
            inputNama.error = "Gender tidak boleh kosong"
            return
        }

        if (inputUsia.text.toString().isBlank()) {
            inputNama.error = "Usia tidak boleh kosong"
            return
        }

        val us = User(
            inputNama.text.toString(),
            inputEmail.text.toString(),
            inputGender.text.toString(),
            inputUsia.text.toString().toIntOrNull(),
            null
        )
        callback?.invoke(us)
    }
}
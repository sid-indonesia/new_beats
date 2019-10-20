package id.linov.beats.game

import com.google.android.gms.nearby.connection.Payload
import com.google.gson.Gson
import id.linov.beatslib.Action
import id.linov.beatslib.GameData
import id.linov.beatslib.GameType
import id.linov.beatslib.User

/**
 * Created by Hayi Nukman at 2019-10-19
 * https://github.com/ha-yi
 */

object Game {
    var serverID: String? = null
    var userInformation: User? = null
    var gameType: GameType = GameType.PERSONAL
    var taskActions: MutableMap<Int, MutableList<Action>> = mutableMapOf()
    var actions: MutableList<Action> = mutableListOf()
    var taskID: Int = -1

    var selectedOpt: Char = 'W'

    fun getColor(color: Char) : Int {
        return when (color) {
            'R' -> R.color.col_r
            'B' -> R.color.col_b
            'Y' -> R.color.col_y
            'W' -> R.color.col_grey_1000w
            else -> R.color.col_grey_1000w
        }
    }

    fun getSelectedOptColor(): Int {
        return getColor(selectedOpt)
    }

    fun currentActionPayload(): Payload {
        return Payload.fromBytes(
            Gson().toJson(GameData(userInformation, gameType, taskID, actions)).toByteArray()
        )
    }
}


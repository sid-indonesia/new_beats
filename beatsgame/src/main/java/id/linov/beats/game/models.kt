package id.linov.beats.game

import android.graphics.Color


/**
 * Created by Hayi Nukman at 2019-10-19
 * https://github.com/ha-yi
 */

object Game {
    var serverID: String? = null
    var userInformation: User? = null
    var gameType: GameType = GameType.PERSONAL
    var actions: MutableList<Action> = mutableListOf()

    var selectedOpt: Char = 'W'

    fun getSelectedOptColor(): Int {
        return when (selectedOpt) {
            'R' -> Color.RED
            'G' -> Color.GREEN
            'B' -> Color.BLUE
            'Y' -> Color.YELLOW
            'W' -> Color.WHITE
            else -> Color.WHITE
        }
    }
}

data class User(
    var name: String,
    var email: String?,
    var gender: String?,
    var usia: Int?,
    var location: Pair<Double, Double>?
)

data class Action(val timestamp: Long, val x: Int, val y: Int, val color: ColorE)

enum class ColorE {
    RED, YELLOW, BLUE, GREEN
}
enum class GameType {
    PERSONAL, GROUP
}
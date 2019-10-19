package id.linov.beats.game


/**
 * Created by Hayi Nukman at 2019-10-19
 * https://github.com/ha-yi
 */

object Game {
    var serverID: String? = null
    var userInformation: User? = null
    var gameType: GameType = GameType.PERSONAL
    var actions: MutableList<Action> = mutableListOf()
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
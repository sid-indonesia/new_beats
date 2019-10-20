package id.linov.beatslib


/**
 * Created by Hayi Nukman at 2019-10-20
 * https://github.com/ha-yi
 */
 
data class BeatsTask(
    val taskID: Int,
    val tileNumber: Int,
    var colors: List<String> = listOf("R", "B", "Y"),
    val duration: Long
)

data class GameData(
    val user: User?,
    val type: GameType,
    val taskID: Int,
    val actions: List<Action>
)

data class User(
    var name: String,
    var email: String?,
    var gender: String?,
    var usia: Int?,
    var location: Pair<Double, Double>?
)

data class Action(val timestamp: Long, val x: Int, val y: Int, val color: Char)

enum class GameType {
    PERSONAL, GROUP
}
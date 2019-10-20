package id.linov.beatslib

import com.google.android.gms.nearby.connection.Payload
import com.google.gson.Gson


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
    var location: Pair<Double, Double>?,
    var userID: String? = null
)

data class Action(val timestamp: Long, val x: Int, val y: Int, val color: Char)

enum class GameType {
    PERSONAL, GROUP
}

data class GroupData(
    var name: String,
    var leadID: String,
    var members: MutableList<Pair<String,String>?>?,
    var leadUserEndpointName: String? = null
)

data class DataShare<T>(
    val command: Int,
    val data: T
) {
    fun toPayload(): Payload {
        return Payload.fromBytes(Gson().toJson(this).toByteArray())
    }
}

const val CMD_NEW_GAME = 0
const val CMD_GAME_DATA = 1
const val CMD_ADD_USER = 2
const val CMD_CREATE_GROUP = 10
const val CMD_JOIN_GROUP = 11
const val CMD_GET_GROUPS = 12
const val CMD_GET_CONFIG = 20
const val CMD_GET_MYUID = 99


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

data class Action(
    val x: Int,
    val y: Int,
    val tile: TileInfo
)

data class ActionLog(
    val userID: String?,
    val groupName: String?,
    val taskID: Int,
    val type: GameType,
    val action: Action
)

enum class GameType {
    PERSONAL, GROUP
}

data class GroupData(
    var name: String,
    var leadID: String,
    var members: MutableSet<String>?,
    var leadUserEndpointName: String? = null,
    var isGameRunning: Boolean = false,
    var currentTask: Int = 0
)

data class DataShare<T>(
    val command: Int,
    val data: T
) {
    fun toPayload(): Payload {
        return Payload.fromBytes(Gson().toJson(this).toByteArray())
    }
}

data class TileInfo(
    var color: Colors,
    var timestamp: Long,
    var userID: String? = null
)

data class Board(
    var taskID: Int,
    var length: Int = 20,
    var tiles: Array<Array<TileInfo>> = arrayOf()
) {
    init {
        initializeTiles()
    }

    private fun initializeTiles() {
        for (y in 0 until length) {
            var arr = arrayOf<TileInfo>()
            for (x in 0 until length) {
                arr += TileInfo(Colors.W, System.currentTimeMillis())
            }
            tiles += arr
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (taskID != other.taskID) return false
        if (length != other.length) return false
        if (!tiles.contentDeepEquals(other.tiles)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = taskID
        result = 31 * result + length
        result = 31 * result + tiles.contentDeepHashCode()
        return result
    }

    fun saveAction(act: Action) {
        tiles[act.y][act.x] = act.tile
    }
}

data class GameSession(
    var userID: String,
    var gameType: GameType,
    var boards: MutableMap<Int, Board> = mutableMapOf(),
    var groupName: String? = null,
    var startTime: Long,
    var endTime: Long? = null,
    var actionLogs: MutableList<Action> = mutableListOf()
) {
    fun saveActionLog(data: ActionLog) {
        actionLogs.add(data.action)
        val task = data.taskID
        if (boards[task] == null) {
            boards[task] = Board(task)
        }
        boards[task]?.saveAction(data.action)
    }
}

enum class Colors {
    R, G, B, Y, W
}

const val CMD_NEW_GAME = 0
const val CMD_GAME_DATA = 1
const val CMD_ADD_USER = 2
const val CMD_CREATE_GROUP = 10
const val CMD_JOIN_GROUP = 11
const val CMD_GET_GROUPS = 12
const val CMD_GET_CONFIG = 20
const val CMD_GET_MYUID = 99
const val CMD_GROUP_GAME_NEW =  70
const val CMD_GROUP_GAME =  71

const val CMD_GAME_SESSION_DATA = 100


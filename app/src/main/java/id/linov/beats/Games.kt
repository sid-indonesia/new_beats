package id.linov.beats

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log.e
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.Payload
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.linov.beatslib.*

@SuppressLint("StaticFieldLeak")
object Games {
    var ctx: Context? = null
    var con: ConnectionInfo? = null

    val paired: MutableList<Pair<String, String>> = mutableListOf()

    // group name to list of members
    val groups: MutableMap<String, GroupData> = mutableMapOf()
    val personalData: MutableMap<String, MutableMap<Int, List<Action>>> = mutableMapOf()
    val users: MutableList<User> = mutableListOf()

    val gameSessions: MutableMap<String, GameSession> = mutableMapOf()
    val groupSessions: MutableMap<String, GameSession> = mutableMapOf()

    fun init(context: Context) {
        ctx = context
    }

    fun save(user: String, p: Payload) {
        if (p.type == Payload.Type.BYTES) {
            p.asBytes()?.let {
                val str = String(it)
                val dt = Gson().fromJson<DataShare<Any>>(str, DataShare::class.java)
                when (dt?.command) {
                    CMD_GAME_DATA -> saveGameData(user, str)
                    CMD_CREATE_GROUP -> createGroup(user, str)
                    CMD_GET_GROUPS -> getGroups(user)
                    CMD_JOIN_GROUP -> joinGroup(user, str)
                    CMD_GET_MYUID -> handleGetUID(user)
                    CMD_ADD_USER -> addUser(user, str)
                    CMD_NEW_GAME -> handleNewGame(user)
                    CMD_GROUP_GAME_NEW -> handleCreateGroupGame(user, str)
                    CMD_START_TASK -> broadcastTaskID(user, str)
                }
            }
        }
    }

    private fun broadcastTaskID(user: String, str: String) {
        val tp = object : TypeToken<DataShare<GroupTask>>() {}.type
        val grpTask = Gson().fromJson<DataShare<GroupTask>>(str, tp)?.data
        grpTask?.let {
            val dts = groups[it.groupID]?.members?.toList() ?: listOf()
            ctx?.let {ctx ->
                Nearby.getConnectionsClient(ctx).sendPayload(dts, DataShare(CMD_START_TASK, it.taskID).toPayload())
            }
        }
    }

    private fun handleCreateGroupGame(user: String, str: String) {
        e("CMD_GROUP_GAME_NEW", "Start game $user")
        val tp = object : TypeToken<DataShare<String>>() {}.type
        val grpID = Gson().fromJson<DataShare<String>>(str, tp).data
        ctx?.let {
            val dts = groups[grpID]?.members?.toList() ?: listOf()
            e("CMD_GROUP_GAME_NEW", "start game on all:  ${dts.joinToString()}")
            Nearby.getConnectionsClient(it).sendPayload(dts, DataShare(CMD_GROUP_GAME_NEW, dts).toPayload())
        }
    }

    private fun handleNewGame(user: String) {
        // force replace game session.
        gameSessions[user] = GameSession(user, GameType.PERSONAL, startTime = System.currentTimeMillis())
        ctx?.let {
            Nearby.getConnectionsClient(it).sendPayload(user, DataShare(CMD_NEW_GAME, user).toPayload())
        }
    }

    private fun addUser(user: String, str: String) {
        val tp = object : TypeToken<DataShare<User>>() {}.type
        val data = Gson().fromJson<DataShare<User>>(str, tp)
        if (data?.data != null) {
            users.add(data.data.apply {
                userID = user
            })
        }
    }

    private fun handleGetUID(user: String) {
        ctx?.let {
            Nearby.getConnectionsClient(it)
                .sendPayload(user, DataShare(CMD_GET_MYUID, user).toPayload())
        }
    }

    private fun <T>send(user: String, data: T, cmd: Int) {
        ctx?.let {
            Nearby.getConnectionsClient(it)
                .sendPayload(user, DataShare(cmd, data).toPayload())
        }
    }

    private fun joinGroup(user: String, str: String) {
        val tp = object : TypeToken<DataShare<String>>() {}.type
        val data = Gson().fromJson<DataShare<String>>(str, tp)?.data
        data?.let {
            groups[it]?.members?.add(user)
            // send response groups.
            getGroups(user)
        }
        send(user, data, CMD_JOIN_GROUP)
    }

    private fun getGroups(user: String) {
        send(user, groups.values.toList(), CMD_GET_GROUPS)
    }

    private fun createGroup(user: String, str: String) {
        e("SERVER", "create group: $str owner $user")
        val tp = object : TypeToken<DataShare<String>>() {}.type
        val data = Gson().fromJson<DataShare<String>>(str, tp)?.data
        data?.let {
            groups.put(
                it, GroupData(
                    it,
                    user,
                    mutableSetOf(user),
                    paired.find { it.first == user }?.second
                )
            )
            e("SERVER", "new group created ")
            getGroups(user)
            send(user, data, CMD_JOIN_GROUP)
        }
    }
/*
    private fun saveGameData(user: String, str: String) {
        val tp = object : TypeToken<DataShare<GameData>>() {}.type
        val data = Gson().fromJson<DataShare<GameData>>(str, tp)?.data
        if (data != null) {
            if (data.actions.isNullOrEmpty()) {
                // reset data.
                personalData[user] = mutableMapOf()
            } else {
                if (personalData[user] == null) {
                    personalData[user] = mutableMapOf()
                }
                personalData[user]?.put(data.taskID, data.actions)
            }
        }
    }

 */

    private fun saveGameData(user: String, str: String) {
        val tp = object : TypeToken<DataShare<ActionLog>>() {}.type
        val data = Gson().fromJson<DataShare<ActionLog>>(str, tp)?.data
        if (data != null) {
            when (data.type) {
                GameType.PERSONAL -> savePersonalGameData(user, data)
                GameType.GROUP -> saveGroupGameData(user, data)
            }
        }
    }

    private fun saveGroupGameData(user: String, data: ActionLog) {
        data.groupName?.let { groupID ->
            if (groupSessions[groupID] == null) {
                groupSessions[groupID] = GameSession(
                    user,
                    GameType.GROUP,
                    startTime = System.currentTimeMillis(),
                    groupName = groupID
                )
            }
            groupSessions[groupID]?.saveActionLog(data)
            ctx?.let {
                val dts = groups[groupID]?.members?.toList() ?: listOf()
                // broadcast update to all group members
                Nearby.getConnectionsClient(it).sendPayload(dts, DataShare(CMD_GROUP_GAME, data).toPayload())
            }
        }
    }

    private fun savePersonalGameData(user: String, data: ActionLog) {
        e("PERSONAL GAME DATA", "$user (${data.action.x},${data.action.x})  ${data.action.tile.color}")
        if (gameSessions[user] == null) {
            handleNewGame(user)
        }
        gameSessions[user]?.saveActionLog(data)
    }
}
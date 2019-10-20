package id.linov.beats

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log.e
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.linov.beatslib.*

@SuppressLint("StaticFieldLeak")
object Games {
    var ctx: Context? = null
    var con: ConnectionInfo? = null

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
                }
            }
        }
    }

    private fun handleGetUID(user: String) {
        ctx?.let {
            Nearby.getConnectionsClient(it)
                .sendPayload(user, DataShare(CMD_GET_MYUID, user).toPayload())
        }
    }

    private fun joinGroup(user: String, str: String) {
        val tp = object : TypeToken<DataShare<String>>() {}.type
        val data = Gson().fromJson<DataShare<String>>(str, tp)?.data
        data?.let {
            groups[it]?.members?.add(paired.find { it.first == user })
            // send response groups.
            getGroups(user)
        }
    }

    private fun getGroups(user: String) {
        ctx?.let {
            Nearby.getConnectionsClient(it)
                .sendPayload(user, DataShare(CMD_GET_GROUPS, groups.values.toList()).toPayload())
        }
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
                    mutableListOf(
                        paired.find { it.first == user }
                    ),
                    paired.find { it.first == user }?.second
                )
            )
            e("SERVER", "new group created ")
            getGroups(user)
        }
    }

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

    val paired: MutableList<Pair<String, String>> = mutableListOf()

    // group name to list of members
    val groups: MutableMap<String, GroupData> = mutableMapOf()

    val personalData: MutableMap<String, MutableMap<Int, List<Action>>> = mutableMapOf()
}
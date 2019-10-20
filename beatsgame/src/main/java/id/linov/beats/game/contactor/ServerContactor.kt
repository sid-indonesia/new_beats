package id.linov.beats.game.contactor

import android.content.Context
import android.util.Log.e
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import id.linov.beats.game.Game
import id.linov.beats.game.GroupListener
import id.linov.beatslib.Action
import id.linov.beatslib.BeatsTask
import id.linov.beatslib.GameData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import id.linov.beatslib.*
import com.google.gson.reflect.TypeToken


object ServerContactor {
    var connection: ConnectionsClient? = null
    var con: ConnectionInfo? = null
    var groupListener: GroupListener? = null

    fun init(context: Context) {
        connection = Nearby.getConnectionsClient(context)
    }

    fun getMyUID() {
        connection?.sendPayload(Game.serverID ?: "", DataShare(CMD_GET_MYUID, "").toPayload())
    }

    val payloadServerCallback = object : PayloadCallback() {
        override fun onPayloadReceived(p0: String, p1: Payload) {
            e("PAYLOAD", "from $p0 : data --> ${p1}")
            hanldePayload(p0, p1)
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            e("onPayloadTransferUpdate", "from $p0 : data --> ${p1}")
        }
    }

    private fun hanldePayload(user: String, data: Payload) {
        when (data.type) {
            Payload.Type.BYTES -> {
                data.asBytes()?.let {
                    val str = String(it)
                    val dt = Gson().fromJson(str, DataShare::class.java)
                    when (dt?.command) {
                        CMD_GET_GROUPS -> handleGroups(user, str)
                        CMD_GET_CONFIG -> getConfig(user, str)
                        CMD_GET_MYUID -> hanldeUser(user, str)
                    }
                }
            }
        }
    }

    private fun hanldeUser(user: String, str: String) {
        val userID = Gson().fromJson<DataShare<String>>(str, DataShare::class.java).data
        Game.userInformation?.userID = userID
    }

    private fun getConfig(user: String, str: String) {
        // todo get configs
    }

    private fun handleGroups(user: String, str: String) {
        val dttp = object : TypeToken<DataShare<List<GroupData>>>() {}.type
        val dt = Gson().fromJson<DataShare<List<GroupData>>>(str, dttp)
        dt?.data?.forEach {
            e("RECEIVED FROM SERVER", "name: ${it.name} # members: ${it.members?.joinToString()}")
        }
        groupListener?.onData(dt)
    }

    fun connectToServer(onConnect: () -> Unit) {
        val connCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
                e("SUCCESS", "onConnectionResult $p0")
            }

            override fun onDisconnected(p0: String) {
                e("SUCCESS", "onDisconnected $p0")
            }

            override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
                e(
                    "INITIALIZED",
                    "connection initilized, $p0 ${p1.endpointName} ${p1.authenticationToken}  ${p1.isIncomingConnection} "
                )
                connection?.acceptConnection(p0, payloadServerCallback)
                onConnect.invoke()
            }
        }
        connection?.requestConnection(
            Game.userInformation?.name ?: "",
            Game.serverID ?: "",
            connCallback
        )?.addOnSuccessListener {
            e("SUCCESS", "Connected to server.....")
        }?.addOnFailureListener {
            e("FAILED", "$it")
            if (it.message?.contains("STATUS_ALREADY_CONNECTED_TO_ENDPOINT") == true) {
                onConnect.invoke()
            } else {
                GlobalScope.async {
                    Thread.sleep(2000)
                    connectToServer(onConnect)
                }
            }
        }
    }

    fun send(onFail: (String) -> Unit) {
        e("SENDING", "sending payload.... to ${Game.serverID}")
        connection?.sendPayload(Game.serverID ?: "", Game.currentActionPayload())
            ?.addOnSuccessListener {
                e("SUCCESS", "Sending payload to ${Game.serverID} ${con?.endpointName} success...")
            }
            ?.addOnFailureListener {
                e("FAILED", "failed to send data (${Game.serverID}:${con?.endpointName}): ${it}")
                onFail.invoke("failed")
            }
    }

    fun resetPlay() {
        send { }
    }

    fun createPayload(id: Int, act: List<Action>) = DataShare(
        CMD_GAME_DATA,
        GameData(
            Game.userInformation,
            Game.gameType,
            id,
            act
        )
    ).toPayload()

    fun sendAfinal() {
        Game.taskActions.forEach {
            connection?.sendPayload(Game.serverID ?: "", createPayload(it.key, it.value))
                ?.addOnSuccessListener {
                    e(
                        "SUCCESS",
                        "Sending payload to ${Game.serverID} ${con?.endpointName} success..."
                    )
                }
                ?.addOnFailureListener {
                    e(
                        "FAILED",
                        "failed to send data (${Game.serverID}:${con?.endpointName}): ${it}"
                    )
                }
        }
    }

    var tasks: List<BeatsTask> = listOf(
        BeatsTask(0, 10, duration = 120000),
        BeatsTask(1, 10, duration = 120000),
        BeatsTask(2, 15, duration = 120000),
        BeatsTask(3, 15, duration = 120000),
        BeatsTask(4, 20, duration = 120000),
        BeatsTask(5, 20, duration = 120000)
    )

    fun createGroup(name: String) {
        connection?.sendPayload(Game.serverID ?: "", DataShare(CMD_CREATE_GROUP, name).toPayload())
    }

    fun groupListener(listener: GroupListener) {
        groupListener = listener
    }

    fun removeGroupListenet() {
        groupListener = null
    }

    fun joinGroup(selectedGroup: GroupData) {
        connection?.sendPayload(
            Game.serverID ?: "",
            DataShare(CMD_JOIN_GROUP, selectedGroup.name).toPayload()
        )
    }

    fun getGroups() {
        connection?.sendPayload(Game.serverID ?: "", DataShare(CMD_GET_GROUPS, "").toPayload())
    }

    fun addUser() {
        connection?.sendPayload(
            Game.serverID ?: "",
            DataShare(CMD_ADD_USER, Game.userInformation).toPayload()
        )
    }
}
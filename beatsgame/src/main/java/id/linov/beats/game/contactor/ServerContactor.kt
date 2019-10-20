package id.linov.beats.game.contactor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log.e
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import id.linov.beats.game.Game
import id.linov.beatslib.BeatsTask
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

object ServerContactor {
    var connection: ConnectionsClient? = null
    var con: ConnectionInfo? = null

    fun init(context: Context) {
        connection = Nearby.getConnectionsClient(context)
    }

    val payloadServerCallback=  object: PayloadCallback() {
        override fun onPayloadReceived(p0: String, p1: Payload) {
            e("PAYLOAD", "from $p0 : data --> ${p1}")
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            e("onPayloadTransferUpdate", "from $p0 : data --> ${p1}")
        }
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
                e("INITIALIZED", "connection initilized, $p0 ${p1.endpointName} ${p1.authenticationToken}  ${p1.isIncomingConnection} ")
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
            }
            else {
                GlobalScope.async {
                    Thread.sleep(2000)
                    connectToServer(onConnect)
                }
            }
        }
    }

    fun send() {
        e("SENDING", "sending payload.... to ${Game.serverID}")
        connection?.sendPayload(Game.serverID ?: "", Game.currentActionPayload())
            ?.addOnSuccessListener {
                e("SUCCESS", "Sending payload to ${Game.serverID} ${con?.endpointName } success...")
            }
            ?.addOnFailureListener {
                e("FAILED", "failed to send data (${Game.serverID}:${con?.endpointName}): ${it}")
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
}
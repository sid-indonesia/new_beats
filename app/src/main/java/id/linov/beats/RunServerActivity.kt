package id.linov.beats

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.*
import android.util.Log.ERROR
import android.util.Log.e
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import id.linov.beats.adapters.ClientsAdapter
import id.linov.beatslib.BEATS_STRATEGY
import id.linov.beatslib.SERVICE_ID
import id.linov.beatslib.SERVICE_NAME
import id.linov.beatslib.User
import kotlinx.android.synthetic.main.activity_run_server.*

class RunServerActivity : AppCompatActivity() {
    /**
     * TODO
     * 1. Create Group Mechanism
     * 1.1. Automatic Group Mechanism (random pairing)
     * 1.2. Manual Paired group (PRIORITY)
     * 2. Start Personal Test Session
     * 3. Start Group Test Session
     * 4. Sync to Server (create new server pls)
     * 5. Group Test config (max player in a group)
     */

    var started = false
    var progressing = false
    val callback = object : ConnectionLifecycleCallback() {
        override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
            e("CLC", "onConnectionResult $p0 - ${p1.status}")
            e("CLC", "onConnectionResult $p0 - ${p1.status.statusCode}")
            if (p1.status.statusCode == 13) {
                val user = Games.users[p0]
                if (user != null) {
                    val g = user.groupID
                    g?.let {
                        Games.groups[g]?.members?.remove(p0)
                    }
                    Games.users.remove(p0)
                }
                updateList()
            }
        }

        override fun onDisconnected(p0: String) {
            e("CLC", "onDisconnected $p0")
            val user = Games.users[p0]
            if (user != null) {
                val g = user.groupID
                g?.let {
                    Games.groups[g]?.members?.remove(p0)
                }
                Games.users.remove(p0)

            }
            updateList()
        }

        override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
            e("CLC", "onConnectionInitiated $p0")
            Nearby.getConnectionsClient(this@RunServerActivity).acceptConnection(p0, payloadCallback)
                .addOnSuccessListener {
                    e("SUCCESS", "PAIRED WITH ${p1.endpointName} $p0")
                    Games.users[p0] = User(p1.endpointName)
                    updateList()
                }
                .addOnFailureListener {
                    e("FAILED", "$it")
                }
        }
    }

    private fun updateList() {
        adapter.notifyDataSetChanged()
        txtParticipanNumber.text = "${Games.users.size} Participant"
    }

    val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(p0: String, p1: Payload) {
            e("PAYLOAD", "from $p0 : data --> ${p1}")
            Games.save(p0, p1)
            updateList()
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            e("onPayloadTransferUpdate", "from $p0 : data --> ${p1}")
        }

    }
    val adapter: ClientsAdapter by lazy { ClientsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_server)

        btToggleService.setOnClickListener {
            toggle()
        }
        animateButton()
        Games.init(applicationContext)

        rvContent.adapter = adapter
        rvContent.layoutManager = LinearLayoutManager(this)
    }

    private fun toggle() {
        if (inputServerName.text.toString().isBlank()) {
            inputServerName.error = "Server name must not be empty"
        }
        if (!started) {
            startAdvertising()
        } else {
            stopAdvertise()
        }
    }

    private fun stopAdvertise() {
        Nearby.getConnectionsClient(this).stopAdvertising()
        started = false
        animateButton()
    }

    private fun startAdvertising() {
        val ao = AdvertisingOptions.Builder().setStrategy(BEATS_STRATEGY).build()
        progressing = true
        animateButton()
        Nearby.getConnectionsClient(this)
            .startAdvertising(SERVICE_NAME, SERVICE_ID, callback, ao)
            .addOnSuccessListener {
                e("SUCCESS", "addOnSuccessListener")
                started = true
                progressing = false
                animateButton()
            }
            .addOnCanceledListener {
                e("ERROR", "addOnCanceledListener")
                started = false
                progressing = false
                animateButton()
            }
            .addOnFailureListener {
                e("ERROR", "addOnFailureListener ${it.toString()}")
                started = false
                progressing = false
                if (it.message?.contains("STATUS_ALREADY_ADVERTISING") == true) {
                    started = true
                }
                animateButton()
            }
    }

    private fun animateButton() {
        val bg = if (started) R.color.colorAccent else R.color.colorPrimary
        val text = if (started) "STOP" else "START"
        progress.visibility = if (progressing) View.VISIBLE else View.GONE

        btToggleService.setBackgroundColor(ContextCompat.getColor(this, bg))
        btToggleService.text = text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(root)
            val c1 = ConstraintSet()
            c1.clone(this, R.layout.activity_run_server)
            val c2 = ConstraintSet()
            c2.clone(this, R.layout.activity_run_server_alt)
            val constraint = if (progressing) {
                ConstraintSet().apply {
                    clone(
                        this@RunServerActivity,
                        R.layout.activity_run_server_progress
                    )
                }
            } else if (started) c2 else c1

            constraint.applyTo(root)
        }
    }
}

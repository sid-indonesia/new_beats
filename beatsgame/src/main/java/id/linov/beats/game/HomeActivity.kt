package id.linov.beats.game

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.e
import androidx.core.content.PermissionChecker
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.material.snackbar.Snackbar
import id.linov.beats.game.fragments.UserInfoFrags
import id.linov.beats.game.fragments.WaitingServerFrags
import id.linov.beatslib.BEATS_STRATEGY
import id.linov.beatslib.SERVICE_ID
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class HomeActivity : AppCompatActivity() {
    val connCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
            e("SUCCESS", "onConnectionResult $p0")
        }

        override fun onDisconnected(p0: String) {
            e("SUCCESS", "onDisconnected $p0")
        }

        override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
            e("SUCCESS", "onConnectionInitiated $p0")
        }
    }

    val callback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(p0: String, p1: DiscoveredEndpointInfo) {
            Snackbar.make(container, "Found BEATS Server, please fill informations", Snackbar.LENGTH_LONG).show()
            if (p0 == Game.serverID && Game.userInformation != null) {
                tryConnect()
            } else {
                Game.serverID = p0
                if (Game.userInformation == null) {
                    requestUserInfo()
                } else {
                    tryConnect()
                }
            }
        }

        override fun onEndpointLost(p0: String) {
            e("FOUND", "endpoint lost: $p0")
        }

    }

    private fun requestUserInfo() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, UserInfoFrags().apply {
                callback = {
                    Game.userInformation = it
                    tryConnect()
                }
            })
            commit()
        }
    }

    private fun tryConnect() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, WaitingServerFrags().apply {
                text = "Connecting to BEATS server"
            })
            commit()
        }
        awaitConnectToServer()
    }

    private fun awaitConnectToServer() {
        Nearby.getConnectionsClient(this).requestConnection(
            Game.userInformation?.name ?: "",
            Game.serverID ?: "",
            connCallback
        ).addOnSuccessListener {
            e("SUCCESS", "Connected to server.....")
        }.addOnFailureListener {
            e("FAILED", "$it")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, WaitingServerFrags())
            commit()
        }

        requestPermission()
        awaitServer()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.ACCESS_WIFI_STATE",
                "android.permission.CHANGE_WIFI_STATE",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION"
            ), 99)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            99 -> {
                if (grantResults.filter { it == PermissionChecker.PERMISSION_DENIED }.isNotEmpty()) {
                    requestPermission()
                }
            }
        }
    }

    private fun awaitServer() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(BEATS_STRATEGY).build()
        Nearby.getConnectionsClient(this).startDiscovery(SERVICE_ID, callback, discoveryOptions)
            .addOnSuccessListener {
                e("SUCCESS", "Success founding server...")
            }
            .addOnFailureListener {
                e("FAILED", "${it}")
                e("FAILED", "failed to find server retrying in 5 secs...")
                GlobalScope.async {
                    Thread.sleep(5000)
                    awaitServer()
                }
            }
    }
}

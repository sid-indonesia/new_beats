package id.linov.beatslib

import android.app.Activity
import android.content.Intent
import com.google.android.gms.nearby.connection.Strategy
import android.R
import android.content.res.Resources
import android.content.res.TypedArray




/**
 * Created by Hayi Nukman at 2019-10-18
 * https://github.com/ha-yi
 */
 
fun Activity.startAct(klass: Class<*>) {
    startActivity(Intent(this, klass))
}


val BEATS_STRATEGY = Strategy.P2P_CLUSTER
val SERVICE_ID = "id.linov.beats"
val SERVICE_NAME = "BEATS Server"

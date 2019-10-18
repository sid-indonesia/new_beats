package id.linov.beats

import android.app.Activity
import android.content.Intent


/**
 * Created by Hayi Nukman at 2019-10-18
 * https://github.com/ha-yi
 */
 
fun Activity.startAct(klass: Class<*>) {
    startActivity(Intent(this, klass))
}
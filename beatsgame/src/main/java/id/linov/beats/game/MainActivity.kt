package id.linov.beats.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.os.HandlerCompat
import com.google.android.gms.nearby.Nearby
import id.linov.beats.game.contactor.ServerContactor
import id.linov.beatslib.startAct

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ServerContactor.init(applicationContext)
        HandlerCompat.postDelayed(Handler(), {
            startAct(HomeActivity::class.java)
            finish()
        }, null, 2000)
    }
}

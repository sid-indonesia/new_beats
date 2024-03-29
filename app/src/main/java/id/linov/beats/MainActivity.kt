package id.linov.beats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.os.HandlerCompat
import id.linov.beatslib.startAct

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HandlerCompat.postDelayed(Handler(), {
            startAct(RunServerActivity::class.java)
            finish()
        }, null, 5000)
    }
}

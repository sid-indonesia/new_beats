package id.linov.beats

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.*
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_run_server.*

class RunServerActivity : AppCompatActivity() {
    var started = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_server)

        btToggleService.setOnClickListener {
            toggle()
        }
        animateButton()
    }

    private fun toggle() {
        started = !started
        animateButton()
    }

    private fun animateButton() {
        val bg = if (!started) {
            R.color.colorPrimary
        } else R.color.colorAccent

        val text = if (started) {
            "STOP"
        } else "START"

        btToggleService.setBackgroundColor(ContextCompat.getColor(this, bg))
        btToggleService.text = text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(root)
            val c1 = ConstraintSet()
            c1.clone(this, R.layout.activity_run_server)
            val c2 = ConstraintSet()
            c2.clone(this, R.layout.activity_run_server_alt)
            val constraint = if (started) c2 else c1
            constraint.applyTo(root)
        }
    }
}

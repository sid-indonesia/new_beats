package id.linov.beats

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.*
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.activity_run_server.*

class RunServerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_server)

        swToggleService.setOnClickListener {
            toggle(it)
        }
    }

    private fun toggle(it: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(root)
            val c1 = ConstraintSet()
            c1.clone(this, R.layout.activity_run_server)
            val c2 = ConstraintSet()
            c2.clone(this, R.layout.activity_run_server_alt)
            val constraint = if (swToggleService.isChecked) c2 else c1
            constraint.applyTo(root)
        }
    }
}

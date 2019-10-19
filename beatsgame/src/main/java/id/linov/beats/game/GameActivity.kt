package id.linov.beats.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    val ui: GameUI by lazy { GameUI(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameContainer.addView(ui)
        ui.bindState(GameUI.State())
    }
}

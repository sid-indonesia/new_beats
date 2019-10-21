package id.linov.beats.game

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log.e
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import com.google.android.material.snackbar.Snackbar
import kotlin.math.min
import id.linov.beats.game.contactor.ServerContactor
import id.linov.beatslib.*
import id.linov.beatslib.GameType.PERSONAL
import id.linov.beatslib.GameType.GROUP

/**
 * Created by Hayi Nukman at 2019-10-20
 * https://github.com/ha-yi
 */

class GameUI @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mainLayout = LinearLayout(context)
    private val tileLayout = GridLayout(context)
    private val optionsLayout = LinearLayout(context)

    private var state: State = State()
    private var needRedraw = true

    init {
        setBackgroundResource(R.color.col_grey_1000b)
        addView(mainLayout, MATCH_PARENT, MATCH_PARENT)
        renderTiles()
    }

    private fun renderTiles() {
        mainLayout.apply {
            orientation = LinearLayout.HORIZONTAL
            addView(tileLayout, LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                weight = 1f
                gravity = Gravity.CENTER
            })
            tileLayout.apply {
                gravity = Gravity.CENTER
            }

            addView(optionsLayout, LinearLayout.LayoutParams(90, MATCH_PARENT))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        e("onMeasure", "W = $widthMeasureSpec  h= $heightMeasureSpec")

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (needRedraw) drawTiles()
    }

    private fun drawTiles() {
        tileLayout.columnCount = state.tileX

        val w = (min(tileLayout.measuredWidth, tileLayout.measuredHeight) / state.tileX) - 2
        e("Drawing tile", "W = $w")
        for (y in 0 until state.tileY) {
            for (x in 0 until state.tileX) {
                tileLayout.addView(Tile(context).apply {
                    bind(w, x, y) { a, b ->
                        e("CLICKED", "Tile number: $a $b")
                        recordTask(a, b)
                    }
                }, LayoutParams(w, w).apply {
                    setMargins(1, 1, 1, 1)
                })
            }
        }
        needRedraw = false
    }

    private fun recordTask(a: Int, b: Int) {
        val act = Action(
            a, b, TileInfo(
                Game.selectedOpt,
                System.currentTimeMillis(),
                Game.userInformation?.userID
            )
        )
        Game.actions.add(act)

        val group = if (Game.gameType == PERSONAL) null else Game.groupID
        ServerContactor.sendAction(ActionLog(Game.userInformation?.userID, group, Game.taskID, Game.gameType, act))
    }

    fun bindState(state: State) {
        this.state = state
    }


    class State {
        var tileX: Int = 10
        var tileY: Int = 10
    }
}
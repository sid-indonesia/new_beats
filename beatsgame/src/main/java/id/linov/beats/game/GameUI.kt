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
import androidx.core.view.setPadding
import kotlin.math.min
import id.linov.beats.game.contactor.ServerContactor
import id.linov.beatslib.*
import id.linov.beatslib.GameType.PERSONAL

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

    private var tiles: Array<Array<Tile>> = arrayOf()

    init {
        setBackgroundResource(R.color.col_grey_1000b)
        addView(mainLayout, MATCH_PARENT, MATCH_PARENT)
//        tileLayout.useDefaultMargins = true
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

    fun updateTiles(data: ActionLog) {
        data.action.let {
            tiles.getOrNull(it.y)?.getOrNull(it.x)?.updateTile(it.tile)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (needRedraw) drawTiles()
    }

    private fun drawTiles() {
        tiles = arrayOf()
        tileLayout.columnCount = state.tileX

        val w = (min(tileLayout.measuredWidth, tileLayout.measuredHeight) / state.tileX) - 2
//        e("Drawing tile", "W = $w")
        for (y in 0 until state.tileY) {
            var tRow: Array<Tile> = arrayOf()
            for (x in 0 until state.tileX) {
                val ll = FrameLayout(context)
                ll.addView(Tile(context).apply {
                    bind(w, x, y) { a, b ->
                        e("CLICKED", "Tile number: $a $b")
                        recordTask(a, b)
                    }
                    tRow += this
                }, w, w)
                ll.setPadding(1)

                tileLayout.addView(ll)
            }
            tiles += tRow
        }
        needRedraw = false

        initSelectionfromBoard()
    }

    private fun initSelectionfromBoard() {
        state.initBoard?.let {
            it.tiles.forEachIndexed { y, row ->
                row.forEachIndexed { x, tile ->
                    tiles.getOrNull(y)?.getOrNull(x)?.updateTile(tile)
                }
            }
        }
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
        ServerContactor.sendAction(
            ActionLog(
                Game.userInformation?.userID,
                group,
                Game.taskID,
                Game.gameType,
                act
            )
        )
    }

    fun bindState(state: State) {
        this.state = state
    }


    class State {
        var tileX: Int = 10
        var tileY: Int = 10
        var initBoard: Board? = null
    }
}
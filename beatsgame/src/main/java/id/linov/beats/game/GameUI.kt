package id.linov.beats.game

import android.content.Context
import android.util.AttributeSet
import android.util.Log.e
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout

/**
 * Created by Hayi Nukman at 2019-10-20
 * https://github.com/ha-yi
 */

class GameUI @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val mainLayout = LinearLayout(context)
    val tileLayout = GridLayout(context)
    val optionsLayout = LinearLayout(context)

    var state: State = State()

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
            })
            addView(optionsLayout, LinearLayout.LayoutParams(90, MATCH_PARENT))
        }
    }

    fun bindState(state: State) {
        tileLayout.columnCount = state.tileX
        val w = (tileLayout.measuredWidth / state.tileX) - state.tileX
        for (y in 0..state.tileY) {
            for (x in 0..state.tileX) {
                tileLayout.addView(Tile(context).apply {
                    bind(w, x, y) { a, b ->
                        e("CLICKED", "Tile number: $a $b")
                    }
                }, LayoutParams(w, w).apply {
                    setMargins(1,1,1,1)
                })
            }
        }
    }


    class State {
        var tileX: Int = 10
        var tileY: Int = 10

        var options: List<Char> = listOf('R', 'B', 'Y')
        var tileListener: ((x: Int, y: Int, col: Char) -> Unit)? = null
    }
}
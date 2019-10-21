package id.linov.beats.game

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridLayout

/**
 * Created by Hayi Nukman at 2019-10-20
 * https://github.com/ha-yi
 */

class Tile @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var size: Int = 10
    var x: Int = -1
    var y: Int = -1

    lateinit var listener: (x: Int, y: Int) -> Unit

    fun bind(size: Int, x: Int, y: Int, clickListener: (x: Int, y: Int) -> Unit) {
        this.size = size
        this.x = x
        this.y = y
        listener = clickListener
        applicate()
    }

    private fun applicate() {
        setOnClickListener {
            listener.invoke(x, y)
            setBackgroundResource(Game.getSelectedOptColor())
        }

        Game.actions.filter { it.x == x && it.y == y }.lastOrNull()?.color?.let {
            setBackgroundResource(Game.getColor(it))
        } ?: kotlin.run {
            setBackgroundResource(Game.getColor('W'))
        }
    }
}
package id.linov.beats

import android.content.Context
import android.graphics.Color

/**
 * Created by Hayi Nukman at 2019-10-19
 * https://github.com/ha-yi
 */

fun Context.getRandomColor(typeColor: String): Int {
    var returnColor = Color.BLACK
    val arrayId = resources.getIdentifier(
        "mdcolor_$typeColor",
        "array",
        applicationContext.packageName
    )

    if (arrayId != 0) {
        val colors = resources.obtainTypedArray(arrayId)
        val index = (Math.random() * colors.length()).toInt()
        returnColor = colors.getColor(index, Color.BLACK)
        colors.recycle()
    }
    return returnColor
}
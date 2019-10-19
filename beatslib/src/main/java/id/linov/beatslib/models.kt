package id.linov.beatslib


/**
 * Created by Hayi Nukman at 2019-10-20
 * https://github.com/ha-yi
 */
 
data class BeatsTask(
    val taskID: Int,
    val tileNumber: Int,
    var colors: List<String> = listOf("R", "B", "Y"),
    val duration: Long
)
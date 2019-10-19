package id.linov.beats.game.contactor

import id.linov.beatslib.BeatsTask

object ServerContactor {
    var tasks: List<BeatsTask> = listOf(
        BeatsTask(0, 10, duration = 120000),
        BeatsTask(1, 10, duration = 120000),
        BeatsTask(2, 15, duration = 120000),
        BeatsTask(3, 15, duration = 120000),
        BeatsTask(4, 20, duration = 120000),
        BeatsTask(5, 20, duration = 120000)
    )
}
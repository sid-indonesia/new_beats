package id.linov.beatslib.interfaces

import id.linov.beatslib.ActionLog
import id.linov.beatslib.GameData
import id.linov.beatslib.GameSession


/**
 * Created by Hayi Nukman at 2019-10-21
 * https://github.com/ha-yi
 */
 
interface GameListener {
    fun onGameData(data: ActionLog)
    fun onOpenTask(taskID: Int?)
}
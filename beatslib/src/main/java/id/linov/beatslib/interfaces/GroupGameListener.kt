package id.linov.beatslib.interfaces

import id.linov.beatslib.GameData


/**
 * Created by Hayi Nukman at 2019-10-21
 * https://github.com/ha-yi
 */
 
interface GroupGameListener {
    fun onGameData(int: Int, data: GameData)
}
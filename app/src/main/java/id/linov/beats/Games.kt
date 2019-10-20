package id.linov.beats

import com.google.android.gms.nearby.connection.Payload
import com.google.gson.Gson
import id.linov.beatslib.Action
import id.linov.beatslib.GameData

object Games {
    fun save(user: String, p: Payload) {
        if (p.type == Payload.Type.BYTES) {
            val str = String(p.asBytes()!!)
            val data = Gson().fromJson<GameData>(str, GameData::class.java)
            if (data != null) {
                if(personalData[user] == null) {
                    personalData[user] = mutableMapOf()
                }
                personalData[user]?.put(data.taskID, data.actions)
            }
        }
    }

    val paired: MutableList<Pair<String, String>> = mutableListOf()
    val groups: MutableMap<String, MutableList<Pair<String, String>>> = mutableMapOf()

    val personalData: MutableMap<String, MutableMap<Int, List<Action>>> = mutableMapOf()
}
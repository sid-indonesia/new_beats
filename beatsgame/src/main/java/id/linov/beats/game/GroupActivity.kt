package id.linov.beats.game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import id.linov.beats.game.contactor.ServerContactor
import id.linov.beatslib.DataShare
import id.linov.beatslib.GameData
import id.linov.beatslib.GroupData
import id.linov.beatslib.interfaces.GroupGameListener
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.group_item.view.*

class GroupActivity : AppCompatActivity(), GroupListener, GroupGameListener {
    var groupData: List<GroupData>? = null
    var selectedGroup: GroupData? = null

    override fun onData(data: DataShare<List<GroupData>>) {
        groupData = data.data
        findMyGroup()
        updateLayout()
        updateListener()
        adapter.notifyDataSetChanged()
    }

    override fun onGameData(int: Int, data: GameData) {
        // always start activity here
        startActivity(Intent(this, GameActivity::class.java))
    }

    private fun updateListener() {
        if(isJoined()) {
            ServerContactor.groupData = selectedGroup
        } else {
            ServerContactor.groupData = null
        }
    }

    private fun findMyGroup() {
        selectedGroup = null
        groupData?.forEach { g ->
            g.members?.forEach {
                e("members", "${it} == ${Game.userInformation?.userID}")

                if (it == Game.userInformation?.userID && Game.userInformation?.userID != null) {
                    selectedGroup = g
                    return
                }
            }
        }
    }

    val adapter: GroupAdapter by lazy { GroupAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        if (Game.userInformation?.userID == null) {
            ServerContactor.getMyUID()
        }

        btnNewGroup.setOnClickListener {
            if (btnNewGroup.text.toString().toLowerCase() == "NEW Group".toLowerCase()) {
                inputGN.visibility = View.VISIBLE
                btnNewGroup.text = "Create Group"
            } else if (!inputGN.text.isNullOrBlank()) {
                createNewGroup()
            } else {
                Snackbar.make(it, "Nama Group tidak boleh kosong", Snackbar.LENGTH_LONG).show()
            }
        }
        rvGroups.adapter = adapter
        rvGroups.layoutManager = LinearLayoutManager(this)
    }

    private fun createNewGroup() {
        ServerContactor.createGroup(inputGN.text.toString())
        inputGN.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        ServerContactor.removeGroupListenet()
    }

    override fun onResume() {
        super.onResume()
        ServerContactor.groupListener(this)
        ServerContactor.getGroups()
    }

    override fun onDestroy() {
        super.onDestroy()
        ServerContactor.groupData = null
    }

    inner class GroupAdapter : RecyclerView.Adapter<GroupHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
            return GroupHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.group_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int = groupData?.size ?: 0

        override fun onBindViewHolder(holder: GroupHolder, position: Int) {
            holder.bind(position)
        }
    }

    inner class GroupHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(position: Int) {
            itemView.apply {
                val item = groupData?.get(position)
                txtGroupName.text = "${item?.name} (${item?.members?.size ?: 0})"
                if (item?.members?.find { it == Game.userInformation?.userID } != null) {
                    btnPlay.visibility = View.GONE
                    btnPlay.setOnClickListener {}
                } else {
                    btnPlay.visibility = View.VISIBLE
                    btnPlay.setOnClickListener {
                        if (item != null) {
                            joinGroup(item)
                        }
                    }
                }
            }
        }
    }

    fun isLead(): Boolean {
        return (selectedGroup?.leadID != null && selectedGroup?.leadID == Game.userInformation?.userID)
    }

    fun isJoined(): Boolean {
        return selectedGroup?.members?.find { it == Game.userInformation?.userID } != null || isLead()
    }

    private fun updateLayout() {
        txtGroupNum.text = "${groupData?.size ?: 0} Groups"
        txtSelectedGroup.text = selectedGroup?.name ?: "No Group"
        btnPlay.setOnClickListener {
            if (isLead() || isJoined()) {
                // todo start game
            } else {
                selectedGroup?.let {
                    joinGroup(it)
                }
            }
        }

        if (isLead()) {
            btnNewGroup.visibility = View.GONE
            btnPlay.text = "Start Game"
            btnPlay.visibility = View.VISIBLE
            txtStatus.text = "You are the lead in this group"
        } else if (isJoined()) {
            btnNewGroup.visibility = View.GONE
            btnPlay.text = "Start Game"
            btnPlay.visibility = View.VISIBLE
            txtStatus.text = "You are have join this group"
        } else {
            btnPlay.visibility = View.GONE
            txtStatus.text = "You have't join any group yet. Select Group on left side"
        }
    }

    private fun joinGroup(selectedGroup: GroupData) {
        ServerContactor.joinGroup(selectedGroup)
    }
}


interface GroupListener {
    fun onData(data: DataShare<List<GroupData>>)
}

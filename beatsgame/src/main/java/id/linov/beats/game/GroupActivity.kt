package id.linov.beats.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import id.linov.beats.game.contactor.ServerContactor
import id.linov.beatslib.DataShare
import id.linov.beatslib.GroupData
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.group_item.*

class GroupActivity : AppCompatActivity(), GroupListener {
    var groupData: List<GroupData>? = null
    var selectedGroup: GroupData? = null

    override fun onData(data: DataShare<List<GroupData>>) {
        groupData = data.data
        adapter.notifyDataSetChanged()
    }

    val adapter: GroupAdapter by lazy { GroupAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

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

        ServerContactor.groupListener(this)
        ServerContactor.getGroups()
        rvGroups.adapter = adapter
        rvGroups.layoutManager = LinearLayoutManager(this)
    }

    private fun createNewGroup() {
        ServerContactor.createGroup(inputGN.text.toString())
        inputGN.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        ServerContactor.removeGroupListenet()
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
                groupData?.get(position)?.let { key ->
                    txtGroupName.text = key.name
                    setOnClickListener {
                        selectedGroup = key
                        updateLayout()
                    }
                }
            }
        }
    }

    fun isLead(): Boolean {
        return (selectedGroup?.leadID != null && selectedGroup?.leadID == Game.userInformation?.userID)
    }

    fun isJoined(): Boolean {
        return selectedGroup?.members?.find { it?.first == Game.userInformation?.userID }?.first != null || isLead()
    }

    private fun updateLayout() {
        txtSelectedGroup.text = selectedGroup?.name
        btnJoin.setOnClickListener {
            if (isLead() || isJoined()) {
                // todo start game
            } else {
                selectedGroup?.let {
                    joinGroup(it)
                }
            }
        }

        if (isLead()) {
            btnJoin.text = "Start Game"
            btnJoin.visibility = View.VISIBLE
            txtStatus.text = "You are the lead in this group"
        } else if (isJoined()) {
            btnJoin.text = "Start Game"
            btnJoin.visibility = View.GONE
            txtStatus.text = "You are have join this group"
        } else {
            btnJoin.text = "Join Group"
            btnJoin.visibility = View.VISIBLE
            txtStatus.text = "Click 'Join Group' to join this group"
        }
    }

    private fun joinGroup(selectedGroup: GroupData) {
        ServerContactor.joinGroup(selectedGroup)
    }
}


interface GroupListener {
    fun onData(data: DataShare<List<GroupData>>)
}

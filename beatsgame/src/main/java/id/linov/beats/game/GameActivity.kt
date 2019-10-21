package id.linov.beats.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.linov.beats.game.contactor.ServerContactor
import id.linov.beats.game.fragments.GamePlayFragment
import id.linov.beatslib.ActionLog
import id.linov.beatslib.BeatsTask
import id.linov.beatslib.interfaces.GameListener
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.task_item.view.*

class GameActivity : AppCompatActivity(), GameListener {
    var activeFrament: GamePlayFragment? = null

    override fun onGameData(data: ActionLog) {
        if (activeFrament == null || Game.taskID != data.taskID) {
            openTask(data.taskID)
            Thread.sleep(500)
        }
        updateFragment(data)
    }

    fun updateFragment(data: ActionLog) {
        activeFrament?.onGameData(data)
    }

    var selectedTask: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        ServerContactor.gameDataListener = this
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TaskAdapter()
    }

    inner class TaskAdapter: RecyclerView.Adapter<TaskAdapter.TaskHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
            return TaskHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false))
        }

        override fun getItemCount(): Int = ServerContactor.tasks.size

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            holder.bind(ServerContactor.tasks[position])
        }

        inner class TaskHolder(v: View): RecyclerView.ViewHolder(v) {
            fun bind(task: BeatsTask) {
                itemView.apply {
                    taskID.text = "${task.taskID}".padStart(2, '0')
                    setOnClickListener {
                        saveLast(selectedTask)
                        selectedTask = task.taskID
                        loadTask(selectedTask)
                        notifyDataSetChanged()
                        notifyItemChanged(position)
                        openTask(task.taskID)
                    }
                    if (selectedTask == task.taskID) {
                        itemContainer.setBackgroundResource(R.color.col_sel_t)
                    } else {
                        itemContainer.setBackgroundResource(R.color.col_sel_tx)
                    }
                    icStat.visibility = if (Game.taskActions[task.taskID].isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                }
            }
        }
    }

    fun openTask(taskID: Int) {
        val task = ServerContactor.tasks.find { it.taskID == taskID } ?: ServerContactor.tasks.first()
        supportFragmentManager.beginTransaction()
            .replace(R.id.gameContainer, GamePlayFragment.create(task).also {
                activeFrament = it
            })
            .commit()
        Game.taskID = taskID
    }

    private fun loadTask(selectedTask: Int?) {
        if (selectedTask != null && Game.taskActions[selectedTask] != null) {
            Game.actions = Game.taskActions[selectedTask] ?: mutableListOf()
        }
    }

    private fun saveLast(selectedTask: Int?) {
        if(selectedTask != null) {
            Game.taskActions.put(selectedTask, Game.actions)
            Game.actions = mutableListOf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // destroy listener
        ServerContactor.gameDataListener = null
    }
}


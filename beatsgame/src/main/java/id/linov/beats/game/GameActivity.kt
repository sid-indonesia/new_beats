package id.linov.beats.game

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.linov.beats.game.contactor.ServerContactor
import id.linov.beats.game.fragments.GamePlaceholder
import id.linov.beats.game.fragments.GamePlayFragment
import id.linov.beatslib.ActionLog
import id.linov.beatslib.BeatsTask
import id.linov.beatslib.GameType
import id.linov.beatslib.interfaces.GameListener
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.task_item.view.*


class GameActivity : AppCompatActivity(), GameListener {
    private var selectedTask: Int? = null
    private val adapter = TaskAdapter()
    var activeFrament: GamePlayFragment? = null

    override fun onGameData(data: ActionLog) {
        if (activeFrament == null || Game.taskID != data.taskID) {
            onOpenTask(data.taskID)
            Thread.sleep(500)
        }
        updateFragment(data)
    }

    fun updateFragment(data: ActionLog) {
        activeFrament?.onGameData(data)
    }

    private var timer = object: CountDownTimer(120000, 1000) {
        override fun onFinish() {
            if (Game.isGroupLead()) {
                AlertDialog.Builder(this@GameActivity)
                    .setTitle("Task ${selectedTask?.toString()?.padStart(2, '0')}")
                    .setMessage("Waktu pengerjaan sudah habis.")
                    .setCancelable(false)
                    .setPositiveButton("Lanjut Ke Task berikutnya") { di, _ ->
                        ServerContactor.startNewTask((selectedTask ?: 0) + 1)
                        di.dismiss()
                        onOpenTask((selectedTask ?: 0) + 1)
                        updateButton()
                    }.show()
            } else {
                activeFrament?.onGroupGameFinished()
            }
        }

        override fun onTick(millisUntilFinished: Long) {
            val minutes = millisUntilFinished / 1000 / 60
            val seconds = millisUntilFinished / 1000 % 60
            when {
                millisUntilFinished > 30000 -> txtTimer.setTextColor(Color.WHITE)
                millisUntilFinished > 10000 -> txtTimer.setTextColor(Color.parseColor("#ff6d00"))
                else -> txtTimer.setTextColor(Color.RED)
            }
            txtTimer.text = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
    }

    override fun onOpenTask(taskID: Int?) {
        taskID?.let {
            if (taskID > ServerContactor.tasks.last().taskID) {
                timer.cancel()
                alltaskFinished()
            } else {
                selectedTask = taskID
                openTask(taskID)
            }
        }
    }

    private fun alltaskFinished() {
        btnStartGame.visibility = View.GONE
        ServerContactor.finished()
        AlertDialog.Builder(this)
            .setTitle("All Task Cleared")
            .setCancelable(false)
            .setMessage("Great, Semua task sudah berhasil kamu selesaikan.")
            .setPositiveButton("Tutup") { di, _ ->
                di.dismiss()
                finish()
            }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        ServerContactor.gameDataListener = this
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        supportFragmentManager.beginTransaction()
            .replace(R.id.gameContainer, GamePlaceholder())
            .commit()
        btnStartGame.text = "START"
        initView()
    }

    private fun updateButton() {
        btnStartGame.text = if (selectedTask == ServerContactor.tasks.last().taskID) "FINISH" else "NEXT"
    }

    private fun initView() {
        btnStartGame.setOnClickListener {
            timer.cancel()
            if (Game.groupID != null && Game.gameType == GameType.GROUP && Game.isGroupLead()) {
                // only update when it on group
                ServerContactor.startNewTask((selectedTask ?: -1) + 1)
            } else {
                onOpenTask((selectedTask ?: -1) + 1)
            }
            updateButton()
        }
        val vis = if (Game.isGroupLead()) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        btnStartGame.visibility = vis
        btnEndGame.visibility = vis
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle("Close current game?")
            .setMessage("Are you sure want to close?")
            .setPositiveButton("Ok, Close and Finish") { _, _ ->
                timer.cancel()
                super.onBackPressed()
            }
            .setNegativeButton("No, Continue Playing") { di, _ ->
                di.dismiss()
            }.show()
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
        e("PLAY", "Opening task $taskID")
        selectedTask = taskID
        loadTask(taskID)
        val task = ServerContactor.tasks.find { it.taskID == taskID } ?: ServerContactor.tasks.first()
        supportFragmentManager.beginTransaction()
            .replace(R.id.gameContainer, GamePlayFragment.create(task).also {
                activeFrament = it
            })
            .commit()
        Game.taskID = taskID
        adapter.notifyDataSetChanged()
        adapter.notifyItemChanged(taskID)
        startTimer()
    }

    private fun startTimer() {
        timer.cancel()
        timer.start()
    }

    private fun loadTask(selectedTask: Int) {
        if (Game.taskActions[selectedTask] != null) {
            Game.actions = Game.taskActions[selectedTask] ?: mutableListOf()
        }
    }

    private fun saveLast(selectedTask: Int) {
        if(selectedTask != null) {
            Game.taskActions.put(selectedTask, Game.actions)
            Game.actions = mutableListOf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // destroy listener
        ServerContactor.gameDataListener = null
        timer.cancel()
    }
}


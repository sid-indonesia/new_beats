package id.linov.beats.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.linov.beats.Games
import id.linov.beats.R
import id.linov.beats.getRandomColor
import kotlinx.android.synthetic.main.client_item.view.*


/**
 * Created by Hayi Nukman at 2019-10-19
 * https://github.com/ha-yi
 */

class ClientsAdapter : RecyclerView.Adapter<ClientsAdapter.ClientHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientHolder = ClientHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.client_item, parent, false)
    )

    override fun getItemCount(): Int = Games.paired.size

    override fun onBindViewHolder(holder: ClientHolder, position: Int) {
        holder.bind(position)
    }

    class ClientHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(pos: Int) {
            itemView.apply {
                Games.paired[pos].let {
                    txtID.text = it.first
                    txtID.setBackgroundColor(context.getRandomColor("300"))
                    txtName.text = it.second
                    llProgress.removeAllViews()
                    Games.personalData[it.first]?.forEach {
                        llProgress.addView(TextView(context).apply {
                            text = "${it.key}".padStart(2, '0')
                            gravity = Gravity.CENTER
                            setBackgroundResource(R.color.col_b)
                            setPadding(10, 0, 10, 0)
                        }, LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
                            gravity = Gravity.CENTER_VERTICAL
                            setMargins(0, 0, 5, 0)
                        })

                    }
                }
            }

        }
    }

}
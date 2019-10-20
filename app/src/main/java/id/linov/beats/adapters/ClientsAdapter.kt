package id.linov.beats.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    class ClientHolder(v: View): RecyclerView.ViewHolder(v) {
        fun bind(pos: Int) {
            itemView.apply {
                Games.paired[pos].let {
                    txtID.text = it.first
                    txtID.setBackgroundColor(context.getRandomColor("300"))
                    txtName.text = it.second

                    txtTasksDone.text = Games.personalData[it.first]?.map {
                        it.key
                    }?.joinToString() ?: "N/A"
                }
            }

        }
    }

}
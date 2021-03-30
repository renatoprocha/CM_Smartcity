package ipvc.estg.cm_smartcity.Application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.cm_smartcity.R
import ipvc.estg.cm_smartcity.entities.Nota

class NotaListAdapter : ListAdapter<Nota, NotaListAdapter.NotaViewHolder>(NotasComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        return NotaViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val current = getItem(position)
        holder.titulo(current.titulo)
        holder.descricao(current.descricao)

    }

    class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val notaItemView: TextView = itemView.findViewById(R.id.titulo)
        private val descricaoItemView: TextView = itemView.findViewById(R.id.descricao)

        fun titulo(text: String?) {
            notaItemView.text = text

        }

        fun descricao(text: String?) {
            descricaoItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): NotaViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerline, parent, false)
                return NotaViewHolder(view)
            }
        }
    }

    class NotasComparator : DiffUtil.ItemCallback<Nota>() {
        override fun areItemsTheSame(oldItem: Nota, newItem: Nota): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Nota, newItem: Nota): Boolean {
            return oldItem.titulo == newItem.titulo
        }
    }
}


package ipvc.estg.cm_smartcity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.cm_smartcity.R
import ipvc.estg.cm_smartcity.notasDB.NotasDB
import kotlinx.android.synthetic.main.recyclerline.view.*

class LineAdapter(val list: ArrayList<NotasDB>):RecyclerView.Adapter<LineViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerline, parent, false);
        return LineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val currentPlace = list[position]

        holder.titulo.text = currentPlace.titulo
        holder.descricao.text = currentPlace.descricao
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val titulo = itemView.titulo
    val descricao = itemView.descricao
}
package ipvc.estg.cm_smartcity.Application

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView

import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.view.menu.MenuView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.cm_smartcity.*
import ipvc.estg.cm_smartcity.entities.Nota
import ipvc.estg.cm_smartcity.viewModel.NotaViewModel
import ipvc.estg.cm_smartcity.viewModel.NotaViewModelFactory
import kotlinx.android.synthetic.main.recyclerline.view.*


import org.w3c.dom.Text

class NotaListAdapter : ListAdapter<Nota, NotaListAdapter.NotaViewHolder>(NotasComparator()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        return NotaViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int){
        val current = getItem(position)
        holder.titulo(current.titulo)
        holder.descricao(current.descricao)
        holder.id(current.id.toString())



    }

    class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val notaItemView: TextView = itemView.findViewById(R.id.titulo)
        private val descricaoItemView: TextView = itemView.findViewById(R.id.descricao)
        private val idItemView: TextView = itemView.findViewById(R.id.idNota)
        private val deletImgBtn: ImageView = itemView.findViewById(R.id.deletImgBtn)


        init{
            itemView.setOnClickListener{v: View ->
                val position: Int = adapterPosition
                val id=itemView.idNota.text
                val titulo=itemView.titulo.text
                val desc=itemView.descricao.text



                val intent = Intent(itemView.context, NotasEditor::class.java)
                intent.putExtra("ID", id)
                intent.putExtra("TITULO", titulo)
                intent.putExtra("DESC", desc)

                itemView.context.startActivity(intent)
            }

            deletImgBtn.setOnClickListener{


                val idD=itemView.idNota.text.toString()
                val titulo=itemView.titulo.text.toString()
                val desc=itemView.descricao.text.toString()



                (itemView.context as Notas).deleteNota(idD, titulo, desc)
            }
        }

        fun titulo(text: String?) {
            notaItemView.text = text

        }

        fun descricao(text: String?) {
            descricaoItemView.text = text
        }

        fun id(text: String?){
            idItemView.text = text
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


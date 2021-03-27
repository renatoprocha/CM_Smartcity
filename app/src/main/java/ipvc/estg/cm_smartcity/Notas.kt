package ipvc.estg.cm_smartcity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.cm_smartcity.adapter.LineAdapter
import ipvc.estg.cm_smartcity.adapter.OnNoteItenClickListener
import ipvc.estg.cm_smartcity.notasDB.NotasDB
import kotlinx.android.synthetic.main.activity_notas.*

class Notas : AppCompatActivity(), OnNoteItenClickListener{

    private lateinit var myList: ArrayList<NotasDB>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        myList = ArrayList<NotasDB>()


        myList.add(NotasDB("Exemplo", "exemplo desc"))
        myList.add(NotasDB("Exemplo3", "exemplo desc2"))

        recycler_view.adapter = LineAdapter(myList, this)
        recycler_view.layoutManager = LinearLayoutManager(this)


    }

    override fun onItemClick(list: NotasDB, position: Int) {
        Toast.makeText(this, list.descricao, Toast.LENGTH_SHORT).show()

        val intent = Intent(this, NotasEditor::class.java)
        intent.putExtra("TITULO", list.titulo)
        intent.putExtra("DESCRICAO", list.descricao)

        startActivity(intent)
    }

}

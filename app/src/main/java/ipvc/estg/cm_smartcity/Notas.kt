package ipvc.estg.cm_smartcity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ipvc.estg.cm_smartcity.adapter.LineAdapter
import ipvc.estg.cm_smartcity.notasDB.NotasDB
import kotlinx.android.synthetic.main.activity_notas.*

class Notas : AppCompatActivity() {

    private lateinit var myList: ArrayList<NotasDB>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        myList = ArrayList<NotasDB>()

        for (i in 0 until 200) {
            myList.add(NotasDB("titulo $i", "$i"))
        }
        recycler_view.adapter = LineAdapter(myList)
        recycler_view.layoutManager = LinearLayoutManager(this)
    }
}

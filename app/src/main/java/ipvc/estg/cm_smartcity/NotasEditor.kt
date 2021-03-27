package ipvc.estg.cm_smartcity

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.recyclerline.*

class NotasEditor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas_editor)


        titulo.text = getIntent().getStringExtra("TITULO")
        descricao.text = getIntent().getStringExtra("DESCRICAO")


    }
}

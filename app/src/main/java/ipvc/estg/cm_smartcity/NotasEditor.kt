package ipvc.estg.cm_smartcity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels

import ipvc.estg.cm_smartcity.entities.Nota
import ipvc.estg.cm_smartcity.viewModel.NotaViewModel
import ipvc.estg.cm_smartcity.viewModel.NotaViewModelFactory

import kotlinx.android.synthetic.main.activity_notas.*
import kotlinx.android.synthetic.main.recyclerline.*
import org.w3c.dom.Text

class NotasEditor : AppCompatActivity() {
    private val wordViewModel: NotaViewModel by viewModels {
        NotaViewModelFactory((application as NotasApplication).repository)
    }

    private lateinit var editWordView: EditText
    private lateinit var editDescView: EditText
    private lateinit var tituloTextView: TextView
    private lateinit var descTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas_editor)
        editWordView = findViewById(R.id.titulo)
        editDescView = findViewById(R.id.descricao)

        var idNota = -1

        if(getIntent().getStringExtra("ID")!=null){
            idNota = getIntent().getStringExtra("ID").toInt()
            descTextView = findViewById(R.id.descricaoTextView)
            tituloTextView = findViewById(R.id.tituloTextView)
            tituloTextView.setVisibility(View.VISIBLE)
            descTextView.setVisibility(View.VISIBLE)

        }


        titulo.text = getIntent().getStringExtra("TITULO")
        descricao.text = getIntent().getStringExtra("DESC")

        val button = findViewById<Button>(R.id.button3)
        button.setOnClickListener{
            if(idNota != -1){

                if(TextUtils.isEmpty(editWordView.text) or (TextUtils.isEmpty(editDescView.text))) {
                    Toast.makeText(this, "Campo(s) em branco!", Toast.LENGTH_SHORT).show()
                } else {

                    val uTitulo = editWordView.text.toString()
                    val uDesc = editDescView.text.toString()
                    val nota = Nota(id = idNota, titulo = uTitulo, descricao = uDesc)
                    wordViewModel.update(nota)
                    Toast.makeText(this, "Nota atualizada com sucesso", Toast.LENGTH_SHORT).show()
                }


            }
            val replyIntent = Intent()
            if(TextUtils.isEmpty(editWordView.text) or (TextUtils.isEmpty(editDescView.text))) {
                replyIntent.putExtra(EXTRA_REPLY, "1")
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = editWordView.text.toString()
                val desc = editDescView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                replyIntent.putExtra(EXTRA_DESC, desc)

                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }



    }

    companion object{
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
        const val EXTRA_DESC = "com.example.android.wordlistsql.REPLY1"
    }

}

package ipvc.estg.cm_smartcity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipvc.estg.cm_smartcity.Application.NotaListAdapter
import ipvc.estg.cm_smartcity.entities.Nota
import ipvc.estg.cm_smartcity.viewModel.NotaViewModel
import ipvc.estg.cm_smartcity.viewModel.NotaViewModelFactory
import kotlinx.android.synthetic.main.recyclerline.*


class Notas : AppCompatActivity() {

    private val newWordActivityRequestCode = 1
    private val wordViewModel: NotaViewModel by viewModels {
        NotaViewModelFactory((application as NotasApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = NotaListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        wordViewModel.allNotas.observe(owner = this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton3)
        fab.setOnClickListener {
            val intent = Intent(this@Notas, NotasEditor::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        var eTitulo ="a"
        var eDesc ="a"

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {


            intentData?.getStringExtra(NotasEditor.EXTRA_REPLY)?.let { reply ->
                eTitulo = reply

            }



        }
        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {


            intentData?.getStringExtra(NotasEditor.EXTRA_DESC)?.let { reply ->
                eDesc = reply
                val nota = Nota(titulo = eTitulo, descricao = eDesc)
                wordViewModel.insert(nota)
            }



        }
        else {
            Toast.makeText(
                applicationContext,
                "Campos em branco!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}


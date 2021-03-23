package ipvc.estg.cm_smartcity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun abreNotas(view: View) {
        val intent = Intent(this, Notas::class.java)
        startActivity(intent)
    }
}

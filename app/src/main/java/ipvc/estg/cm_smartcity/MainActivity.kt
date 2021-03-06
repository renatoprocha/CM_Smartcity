package ipvc.estg.cm_smartcity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import ipvc.estg.cm_smartcity.api.EndPoints
import ipvc.estg.cm_smartcity.api.loginInfo
import ipvc.estg.cm_smartcity.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.perference_file_key), Context.MODE_PRIVATE)

        val idValue = sharedPref.getInt(getString(R.string.id_user), -1)
        Log.d("id_user", "$idValue")

        if(idValue != -1){
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
    fun login(view: View){

        lateinit var nome: EditText
        lateinit var password: EditText

        nome = findViewById(R.id.nome)
        password = findViewById(R.id.password)

        var nomeL = nome.text.toString()
        var passL = password.text.toString()
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.login(nomeL, passL)


        call.enqueue(object : Callback<loginInfo> {
            override fun onResponse(
                call: Call<loginInfo>,
                response: Response<loginInfo>
            ) {
                if(response.isSuccessful){
                    val c: loginInfo = response.body()!!
                    if(c.status == "true"){
                        val sharedPref: SharedPreferences = getSharedPreferences(
                            getString(R.string.perference_file_key), Context.MODE_PRIVATE)
                        with (sharedPref.edit()){
                            putInt(getString(R.string.id_user), c.data.id)
                            commit()
                        }

                        val intent = Intent(this@MainActivity, MapsActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    if(c.status == "false"){
                        Toast.makeText(this@MainActivity, "Cardenciais erradas", Toast.LENGTH_LONG).show()
                    }
                }
            }


            override fun onFailure(call: Call<loginInfo>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro", Toast.LENGTH_LONG).show()
            }
        })

    }

    fun abreNotas(view: View) {
        val intent = Intent(this, Notas::class.java)
        startActivity(intent)
    }


}



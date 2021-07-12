package ipvc.estg.cm_smartcity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import ipvc.estg.cm_smartcity.api.*
import kotlinx.android.synthetic.main.activity_report_new.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*

class ReportEdit : AppCompatActivity(), PermissionListener {




    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton
    private lateinit var editText: EditText
    private lateinit var imgButton: ImageButton
    private lateinit var delButton: Button
    private lateinit var imageView: ImageView
    private lateinit var report: Report
    open var image64 = "null"

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        var idMarker = intent.getIntExtra("EXTRA_ID", -1)
        Log.d("IDMARK", idMarker.toString())



        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReport(idMarker)

        call.enqueue(object : Callback<Report> {
            override fun onFailure(call: Call<Report>, t: Throwable) {
                Toast.makeText(this@ReportEdit, "${t.message}  erro", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Report>, response: Response<Report>) {
                if(response.isSuccessful){
                    report = response.body()!!
                    atualizarDados()

                }
            }
        })



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_new)
        editText = findViewById(R.id.descricaoReport)
        delButton = findViewById(R.id.button4)
        delButton.setVisibility(View.VISIBLE)



        imgButton = findViewById(R.id.imageButton)
        imageView = findViewById(R.id.imageView)

        radioGroup = findViewById(R.id.radioGroup)

        Log.d("LOC", idMarker.toString())

        imgButton.setOnClickListener{
            Dexter.withContext(this)
                .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(this)
                .check()
        }


        val buttonDel = findViewById<Button>(R.id.button4)
        buttonDel.setOnClickListener{
            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.reportDelete(idMarker)


            call.enqueue(object : Callback<loginInfo> {
                override fun onResponse(
                    call: Call<loginInfo>,
                    response: Response<loginInfo>
                ) {
                    if(response.isSuccessful){

                            val intent = Intent(this@ReportEdit, MapsActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                }


                override fun onFailure(call: Call<loginInfo>, t: Throwable) {
                    Toast.makeText(this@ReportEdit, "Erro", Toast.LENGTH_LONG).show()
                }
            })
        }



        val button = findViewById<Button>(R.id.submeter)

        button.setOnClickListener{
            if(radioGroup.checkedRadioButtonId == -1){
                Toast.makeText(this, "Selecione um tipo de ocorrência.", Toast.LENGTH_SHORT).show()
            } else {


                val radioId = radioGroup.checkedRadioButtonId

                Toast.makeText(this@ReportEdit, "$radioId", Toast.LENGTH_LONG).show()
                Log.d("IDMARK", radioId.toString())

                radioButton = findViewById(radioId)
                val selected = radioButton.text
                val descricao = descricaoReport.text.toString()







                /*Chamada de api*/
                val sharedPref: SharedPreferences = getSharedPreferences(
                    getString(R.string.perference_file_key), Context.MODE_PRIVATE)

                val idUser = sharedPref.getInt(getString(R.string.id_user), -1)
                val lat = report.lat
                val longi = report.longi


                Log.d("IDMARK", idMarker.toString())
                Log.d("IDMARK", idUser.toString())
                Log.d("IDMARK", selected.toString())
                Log.d("IDMARK", descricao)
                Log.d("IDMARK", lat.toString())
                Log.d("IDMARK", longi.toString())
                Log.d("IDMARK", image64)

                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.reportUpdate(idMarker,idUser, selected.toString(), descricao, lat, longi, image64)

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

                                val intent = Intent(this@ReportEdit, MapsActivity::class.java)
                                startActivity(intent)
                                finish()

                            }
                            if(c.status == "false"){
                                Toast.makeText(this@ReportEdit, "Cardenciais erradas", Toast.LENGTH_LONG).show()
                            }
                        }
                    }


                    override fun onFailure(call: Call<loginInfo>, t: Throwable) {
                        Toast.makeText(this@ReportEdit, "Erro", Toast.LENGTH_LONG).show()
                    }
                })

                val intente = Intent(this@ReportEdit, MapsActivity::class.java)
                startActivity(intente)

            }




        }


    }

    private fun atualizarDados() {
        Log.d("IDMARK", report.tipo)
        if(report.tipo == "Saneamento") {

            radioGroup.check(R.id.radioSaneamento)
        }
        if(report.tipo == "Obra") {

            radioGroup.check(R.id.radioObras)
        }
        if(report.tipo == "Acidente") {

            radioGroup.check(R.id.radioAcidente)
        }
        editText.setText(report.descricao)
    }



    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {

    }

    override fun onPermissionRationaleShouldBeShown(response: PermissionRequest?, token: PermissionToken?) {
        if (token != null) {
            token.continuePermissionRequest()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                imageView.setImageURI(resultUri)


                /////////////
                val image = findViewById<ImageView>(R.id.imageView)
                val bitmap = image.drawable.toBitmap()
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                image64 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())


                /////////////



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

}

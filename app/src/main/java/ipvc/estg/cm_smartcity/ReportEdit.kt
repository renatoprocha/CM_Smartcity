package ipvc.estg.cm_smartcity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import ipvc.estg.cm_smartcity.api.EndPoints
import ipvc.estg.cm_smartcity.api.Report
import ipvc.estg.cm_smartcity.api.ServiceBuilder
import ipvc.estg.cm_smartcity.api.reportInsertInfo
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
    private lateinit var imageView: ImageView
    private lateinit var report: Report
    open var image64 = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        var idMarker = intent.getStringExtra("EXTRA_ID")



        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReport(idMarker.toInt())

        call.enqueue(object : Callback<Report> {
            override fun onFailure(call: Call<Report>, t: Throwable) {
                Toast.makeText(this@ReportEdit, "${t.message}  erro", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Report>, response: Response<Report>) {
                if(response.isSuccessful){
                    report = response.body()!!

                }
            }
        })


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_new)
        editText = findViewById(R.id.descricaoReport)

        imgButton = findViewById(R.id.imageButton)
        imageView = findViewById(R.id.imageView)

        radioGroup = findViewById(R.id.radioGroup)

        Log.d("LOC", idMarker)

        imgButton.setOnClickListener{
            Dexter.withContext(this)
                .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(this)
                .check()
        }


        val button = findViewById<Button>(R.id.submeter)
        button.setOnClickListener{
            if(radioGroup.checkedRadioButtonId == -1){
                Toast.makeText(this, "Selecione um tipo de ocorrência.", Toast.LENGTH_SHORT).show()
            } else {
                val radioId = radioGroup.checkedRadioButtonId

                radioButton = findViewById(radioId)
                val selected = radioButton.text
                val descricao = editText.text.toString()




                /*Chamada de api*/
                val sharedPref: SharedPreferences = getSharedPreferences(
                    getString(R.string.perference_file_key), Context.MODE_PRIVATE)

                val idUser = sharedPref.getInt(getString(R.string.id_user), -1)
                val lat = report.lat
                val longi = report.longi


                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.insertReport(idUser, selected.toString(), descricao, lat, longi, image64)

                call.enqueue(object : Callback<reportInsertInfo> {
                    override fun onResponse(
                        call: Call<reportInsertInfo>,
                        response: Response<reportInsertInfo>
                    ) {
                        if(response.isSuccessful){
                            val c: reportInsertInfo = response.body()!!
                            if(c.status == "true"){

                            }
                            if(c.status == "false"){
                                Toast.makeText(this@ReportEdit, "Algo errado", Toast.LENGTH_LONG).show()
                            }
                        }

                    }

                    override fun onFailure(call: Call<reportInsertInfo>, t: Throwable) {
                        Toast.makeText(this@ReportEdit, "ERRO API", Toast.LENGTH_SHORT).show()
                    }
                })

                val intente = Intent(this@ReportEdit, MapsActivity::class.java)
                startActivity(intente)

            }




        }


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

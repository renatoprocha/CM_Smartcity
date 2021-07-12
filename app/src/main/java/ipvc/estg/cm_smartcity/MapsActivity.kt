package ipvc.estg.cm_smartcity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import ipvc.estg.cm_smartcity.Application.CustomInfoWindowAdapter
import ipvc.estg.cm_smartcity.api.EndPoints
import ipvc.estg.cm_smartcity.api.Report
import ipvc.estg.cm_smartcity.api.ServiceBuilder
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_report_new.*
import kotlinx.android.synthetic.main.activity_report_new.radioGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


//branch mapa
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener{
    private lateinit var mMap: GoogleMap
    private lateinit var sensorManageer: SensorManager
    private var brightness: Sensor? = null

    open var luminosidade = "normal"
    open var localizacao = "Desconhecido"
    open var lat = -1.32
    open var longi = -1.32

    companion object{
        open var mHashMap=HashMap<Marker, MarkerInfo>()
    }




    private lateinit var report: Report
    private lateinit var reports: List<Report>
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        //inicialização fusedLocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                var loc = LatLng(lastLocation.latitude, lastLocation.longitude)

                LocationChanged(lastLocation)

                lat = loc.latitude
                longi = loc.longitude


                Log.d("cord", "Coord:" + loc.latitude + "-" + loc.longitude + "Localização:" + localizacao )

            }
        }

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.perference_file_key), Context.MODE_PRIVATE)

        val idUser = sharedPref.getInt(getString(R.string.id_user), -1)
        Log.d("id_user2", "$idUser")

        //Request de todos os reports
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReports()
        var position: LatLng

        call.enqueue(object : Callback<List<Report>> {
            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                Toast.makeText(this@MapsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {


                if(response.isSuccessful){
                    reports = response.body()!!
                    for(report in reports) {
                        position = LatLng(report.lat.toString().toDouble(),
                            report.longi.toString().toDouble())

                        if(report.id_user == idUser) {
                            var markerInfo = MarkerInfo(report.id, report.image)
                            var marker = mMap.addMarker(MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(report.tipo).snippet(report.descricao + "\n"))
                            mHashMap.put(marker, markerInfo)

                        } else {
                            var markerInfo = MarkerInfo(report.id, report.image)
                            var marker = mMap.addMarker(MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(report.tipo).snippet(report.descricao + "\n"))
                            mHashMap.put(marker, markerInfo)
                        }
                    }
                }
            }

        })
        createLocationRequest()

        radioGroup.setOnCheckedChangeListener{ group, checkedID ->
            if(checkedID == R.id.radioObras1) {

                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.getReportTipo("Obra")
                var position: LatLng

                call.enqueue(object : Callback<List<Report>> {
                    override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                        Toast.makeText(this@MapsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<List<Report>>,
                        response: Response<List<Report>>
                    ) {


                        if (response.isSuccessful) {
                            reports = response.body()!!
                            for (report in reports) {
                                position = LatLng(
                                    report.lat.toString().toDouble(),
                                    report.longi.toString().toDouble()
                                )

                                if (report.id_user == idUser) {
                                    var markerInfo = MarkerInfo(report.id, report.image)
                                    var marker = mMap.addMarker(
                                        MarkerOptions().position(position).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_GREEN
                                            )
                                        ).title(report.tipo).snippet(report.descricao + "\n")
                                    )
                                    mHashMap.put(marker, markerInfo)

                                } else {
                                    var markerInfo = MarkerInfo(report.id, report.image)
                                    var marker = mMap.addMarker(
                                        MarkerOptions().position(position).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        ).title(report.tipo).snippet(report.descricao + "\n")
                                    )
                                    mHashMap.put(marker, markerInfo)
                                }
                            }
                        }
                    }

                })
                mMap.clear()
            }
            if(checkedID == R.id.radioSaneamento1){

                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.getReportTipo("Saneamento")
                var position: LatLng

                call.enqueue(object : Callback<List<Report>> {
                    override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                        Toast.makeText(this@MapsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<List<Report>>,
                        response: Response<List<Report>>
                    ) {


                        if (response.isSuccessful) {
                            reports = response.body()!!
                            for (report in reports) {
                                position = LatLng(
                                    report.lat.toString().toDouble(),
                                    report.longi.toString().toDouble()
                                )

                                if (report.id_user == idUser) {
                                    var markerInfo = MarkerInfo(report.id, report.image)
                                    var marker = mMap.addMarker(
                                        MarkerOptions().position(position).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_GREEN
                                            )
                                        ).title(report.tipo).snippet(report.descricao + "\n")
                                    )
                                    mHashMap.put(marker, markerInfo)

                                } else {
                                    var markerInfo = MarkerInfo(report.id, report.image)
                                    var marker = mMap.addMarker(
                                        MarkerOptions().position(position).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        ).title(report.tipo).snippet(report.descricao + "\n")
                                    )
                                    mHashMap.put(marker, markerInfo)
                                }
                            }
                        }
                    }

                })
                mMap.clear()
            }
            if(checkedID == R.id.radioAcidente1){

                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.getReportTipo("Acidente")
                var position: LatLng

                call.enqueue(object : Callback<List<Report>> {
                    override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                        Toast.makeText(this@MapsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<List<Report>>,
                        response: Response<List<Report>>
                    ) {


                        if (response.isSuccessful) {
                            reports = response.body()!!
                            for (report in reports) {
                                position = LatLng(
                                    report.lat.toString().toDouble(),
                                    report.longi.toString().toDouble()
                                )

                                if (report.id_user == idUser) {
                                    var markerInfo = MarkerInfo(report.id, report.image)
                                    var marker = mMap.addMarker(
                                        MarkerOptions().position(position).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_GREEN
                                            )
                                        ).title(report.tipo).snippet(report.descricao + "\n")
                                    )
                                    mHashMap.put(marker, markerInfo)

                                } else {
                                    var markerInfo = MarkerInfo(report.id, report.image)
                                    var marker = mMap.addMarker(
                                        MarkerOptions().position(position).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        ).title(report.tipo).snippet(report.descricao + "\n")
                                    )
                                    mHashMap.put(marker, markerInfo)
                                }
                            }
                        }
                    }

                })
                mMap.clear()
            }
            if(checkedID == R.id.Semfiltro){

                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.getReports()
                var position: LatLng

                call.enqueue(object : Callback<List<Report>> {
                    override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                        Toast.makeText(this@MapsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<List<Report>>,
                        response: Response<List<Report>>
                    ) {


                        if (response.isSuccessful) {
                            reports = response.body()!!
                            for (report in reports) {
                                position = LatLng(
                                    report.lat.toString().toDouble(),
                                    report.longi.toString().toDouble()
                                )

                                if (report.id_user == idUser) {
                                    var markerInfo = MarkerInfo(report.id, report.image)
                                    var marker = mMap.addMarker(
                                        MarkerOptions().position(position).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_GREEN
                                            )
                                        ).title(report.tipo).snippet(report.descricao + "\n")
                                    )
                                    mHashMap.put(marker, markerInfo)

                                } else {
                                    var markerInfo = MarkerInfo(report.id, report.image)
                                    var marker = mMap.addMarker(
                                        MarkerOptions().position(position).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        ).title(report.tipo).snippet(report.descricao + "\n")
                                    )
                                    mHashMap.put(marker, markerInfo)
                                }
                            }
                        }
                    }

                })
                mMap.clear()
            }
        }
        setUpSensor()

    }

    private fun setUpSensor(){
        sensorManageer = getSystemService(SENSOR_SERVICE) as SensorManager
        brightness = sensorManageer.getDefaultSensor(Sensor.TYPE_LIGHT)

    }

    private fun brightness(brightness: Float): String{
        return when (brightness.toInt()){
            in 0 .. 11 -> "dark"
            in 12 .. 25000 -> "normal"
            else -> "normal"
        }
    }


    override fun onPause(){
        sensorManageer.unregisterListener(this)
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume(){
        super.onResume()
        sensorManageer.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
        startLocationUpdates()

    }

    private fun startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener {
                marker -> // on marker click we are getting the title of our marker
            // which is clicked and displaying it in a toast message.
            var markerId = mHashMap.get(marker)


            val sharedPref: SharedPreferences = getSharedPreferences(
                getString(R.string.perference_file_key), Context.MODE_PRIVATE)

            val idUser = sharedPref.getInt(getString(R.string.id_user), -1)

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.getReport(markerId!!.id)

            call.enqueue(object : Callback<Report> {
                override fun onFailure(call: Call<Report>, t: Throwable) {
                    Toast.makeText(this@MapsActivity, "${t.message}  erro", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Report>, response: Response<Report>) {
                    if(response.isSuccessful){
                        report = response.body()!!
                        val reportUser = report.id_user
                        val idDoMarker = report.id
                        Log.d("IDMARK", idDoMarker.toString())
                        if(reportUser == idUser){
                           val intent = Intent(this@MapsActivity, ReportEdit::class.java)
                            intent.putExtra("EXTRA_ID", idDoMarker)
                            startActivity(intent)
                            Toast.makeText(this@MapsActivity, "Ponto meu", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@MapsActivity, "Ação não permitida", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })

            false
        }


        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this@MapsActivity))

        setUpMap()
    }

    fun abreNota(view: View) {
        val intent = Intent(this@MapsActivity, Notas::class.java)
        startActivity(intent)
    }
    fun Report(view: View) {
        val intent = Intent(this@MapsActivity, ReportNew::class.java)
        intent.putExtra("EXTRA_LOC", localizacao)
        intent.putExtra("EXTRA_LAT", lat)
        intent.putExtra("EXTRA_LONGI", longi)
        startActivity(intent)

    }

    fun Logout(view: View){
        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.perference_file_key), Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putInt(getString(R.string.id_user), -1)
            commit()
        }




        val intent = Intent(this@MapsActivity, MainActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun setUpMap(){
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

            return
        } else {
            mMap.isMyLocationEnabled = true

            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location->
                if(location != null) {
                    lastLocation = location

                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                }
            }
        }
    }

    private fun createLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }




    public fun setAddress(adress: Address) {
        if(adress != null) {
            if(adress.getAddressLine(0) != null) {
                Log.d("LLL", ""+ adress.getAddressLine(0) +"")
                localizacao = adress.getAddressLine(0)
            }
            if(adress.getAddressLine(1) != null) {
                Log.d("LLL", ""+ adress.getAddressLine(1) +"")
                localizacao = adress.getAddressLine(1)
            }
        }
    }

    private fun LocationChanged(p0: Location) {
        Log.d("LLL", "changed!!")

        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress: List<Address>? = null

        try{
            Adress = geoCoder.getFromLocation(p0.latitude, p0.longitude,1)
            mudarTema(luminosidade)
        }catch (e: IOException) {
        e.printStackTrace()}

    }
    private fun mudarTema(luminosidade: String){
        if(luminosidade == "dark"){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_night))
        }else{
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_day))
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT){
            var light = event.values[0]

            if (brightness(light) == "dark"){
               luminosidade = "dark"

            }else{
                luminosidade = "normal"
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }


}

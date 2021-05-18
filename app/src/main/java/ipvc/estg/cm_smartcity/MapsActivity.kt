package ipvc.estg.cm_smartcity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.estg.cm_smartcity.api.EndPoints
import ipvc.estg.cm_smartcity.api.Report
import ipvc.estg.cm_smartcity.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

//branch mapa
class MapsActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
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


                Log.d("cord", "Coord:" + loc.latitude + "-" + loc.longitude + "Localização:" )
                mMap.isMyLocationEnabled = true
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
                            mMap.addMarker(MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(report.descricao))
                        } else {
                            mMap.addMarker(MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(report.descricao))
                        }
                    }
                }
            }

        })
        createLocationRequest()
    }

    override fun onPause(){
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume(){
        super.onResume()
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



        setUpMap()
    }

    fun abreNota(view: View) {
        val intent = Intent(this@MapsActivity, Notas::class.java)
        startActivity(intent)
    }
    fun Report(view: View) {
        val intent = Intent(this@MapsActivity, ReportNew::class.java)
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
                    Toast.makeText(this@MapsActivity, lastLocation.toString(), Toast.LENGTH_SHORT).show()
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




    private fun setAddress(adress: Address) {
        if(adress != null) {
            if(adress.getAddressLine(0) != null) {
                Log.d("LLL", ""+ adress.getAddressLine(0) +"")
            }
            if(adress.getAddressLine(1) != null) {
                Log.d("LLL", ""+ adress.getAddressLine(1) +"")
            }

        }
    }

    private fun LocationChanged(p0: Location) {
        Log.d("LLL", "changed!!")

        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress: List<Address>? = null

        try{
            Adress = geoCoder.getFromLocation(p0.latitude, p0.longitude,1)
        }catch (e: IOException) {
        e.printStackTrace()}
        setAddress(Adress!![0])
    }

}

package ipvc.estg.cm_smartcity.Application

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker
import ipvc.estg.cm_smartcity.MarkerInfo
import ipvc.estg.cm_smartcity.R
import ipvc.estg.cm_smartcity.MapsActivity.Companion.mHashMap


/**
 * Created by User on 10/2/2017.
 */
class CustomInfoWindowAdapter(private val mContext: Context) : InfoWindowAdapter {
    private val mWindow: View
    private fun rendowWindowText(marker: Marker, view: View) {
        val title = marker.title
        val tvTitle = view.findViewById<View>(R.id.titulo) as TextView
        if (title != "") {
            tvTitle.text = title
        }
        val snippet = marker.snippet
        val tvSnippet = view.findViewById<View>(R.id.snippet) as TextView
        if (snippet != "") {
            tvSnippet.text = snippet
        }

        var infoMarker = mHashMap.get(marker)

        var imageBase64 = infoMarker!!.image

        val decodedString: ByteArray = Base64.decode(imageBase64, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        val imageV = view.findViewById<View>(R.id.image) as ImageView
        imageV.setImageBitmap(decodedByte)

        if (imageBase64 == null) {
           
        }

    }



    override fun getInfoWindow(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    init {
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null)
    }
}
package com.wischnewsky.finalproject

import android.content.Context
import android.location.Address
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.wischnewsky.finalproject.data.Expense
import android.location.Geocoder
import android.util.Log
import java.io.IOException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var expenseList: MutableList<Expense>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        expenseList = intent.extras!!["DATA_SET"] as MutableList<Expense>

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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        val resultTotal = getLocationTotals()
        resultTotal.forEach {
            val coordinates = getLocationFromAddress(this, it.key)
            Log.d("Found coordinate", coordinates.toString())
            if(coordinates != null) {
                mMap.addMarker(MarkerOptions().position(coordinates!!).title("You spent a total of " + it.value.toString() + " in " + it.key.toString()))
            }
        }

        val mapSettings = mMap?.uiSettings
        mapSettings?.isZoomControlsEnabled = true

    }

    fun getLocationTotals(): MutableMap<String, Long>{
        val locationBreakdown: MutableMap<String, Long> = mutableMapOf()

        expenseList.forEach {
            if (locationBreakdown.containsKey(it.locationName)) {
                val previous = locationBreakdown.get(it.locationName)
                locationBreakdown.replace(it.locationName, previous!! + it.cost)
            } else {
                locationBreakdown.put(it.locationName, it.cost)
            }
        }

        return locationBreakdown
    }

    fun getLocationFromAddress(context: Context, strAddress: String): LatLng? {

        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address.isEmpty()) {
                return null
            }

            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)

        } catch (ex: IOException) {

            ex.printStackTrace()
        }

        return p1
    }
}

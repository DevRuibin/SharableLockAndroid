package com.example.shareablelock

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "MapsActivity"
    private lateinit var mMap: GoogleMap
    private var latitude = 0.0f
    private var longitude = 0.0f
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_maps)

        // Initialize apiService after setContentView to ensure context is available
        apiService = RetrofitHelper.getApiService(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val lockId = intent.getLongExtra("lockId", -1)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Fetch lock details from API
        fetchLockDetails(lockId)
    }

    private fun fetchLockDetails(lockId: Long) {
        apiService.getLockById(lockId).enqueue(object : retrofit2.Callback<LockModel> {
            override fun onResponse(call: retrofit2.Call<LockModel>, response: retrofit2.Response<LockModel>) {
                if (response.isSuccessful) {
                    val lock = response.body()
                    if (lock != null) {
                        latitude = lock.latitude
                        longitude = lock.longitude
                        Log.d(TAG, "Lock location: $latitude, $longitude")
                        updateMapLocation()
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<LockModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        updateMapLocation()
    }

    private fun updateMapLocation() {
        val lat = latitude
        val lon = longitude
        if (::mMap.isInitialized) {
            val location = LatLng(lat.toDouble(), lon.toDouble())
            mMap.addMarker(MarkerOptions().position(location).title("Lock Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }
}

package com.teamrocket.driverapp

import com.teamrocket.driverapp.R
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback

    val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    // val BUS_ID = "BUS12" // Removed hardcoded ID
    var currentBusId: String = ""
    val SERVER_URL = "https://go-saathi.vercel.app/api/bus/location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            val etBusNumber = findViewById<android.widget.EditText>(R.id.etBusNumber)
            val etPassword = findViewById<android.widget.EditText>(R.id.etPassword)

            val inputBusId = etBusNumber.text.toString().trim()
            val inputPassword = etPassword.text.toString().trim()

            if (inputBusId.isEmpty()) {
                etBusNumber.error = "Bus ID is required"
                return@setOnClickListener
            }
            if (inputPassword.isEmpty()) {
                etPassword.error = "Password is required"
                return@setOnClickListener
            }

            // Disable UI while verifying
            etBusNumber.isEnabled = false
            etPassword.isEnabled = false
            findViewById<Button>(R.id.btnStart).isEnabled = false
            
            loginAndStart(inputBusId, inputPassword, etBusNumber, etPassword)
        }
    }

    private fun loginAndStart(busId: String, pass: String, etIds: android.widget.EditText, etPass: android.widget.EditText) {
        val statusTxt = findViewById<TextView>(R.id.txtStatus)
        statusTxt.text = "Verifying Credentials..."

        val json = JSONObject()
        json.put("busId", busId)
        json.put("password", pass)

        val body = RequestBody.create(
            "application/json".toMediaType(),
            json.toString()
        )

        val request = Request.Builder()
            .url("https://go-saathi.vercel.app/api/bus/login")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    statusTxt.text = "Login Failed: ${e.message}"
                    // Re-enable inputs
                    etIds.isEnabled = true
                    etPass.isEnabled = true
                    findViewById<Button>(R.id.btnStart).isEnabled = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful) {
                        currentBusId = busId
                        statusTxt.text = "Login Success! Starting Tracker..."
                        startLocationUpdates()
                    } else {
                        statusTxt.text = "Login Failed: Invalid Credentials"
                        android.widget.Toast.makeText(this@MainActivity, "Invalid Credentials", android.widget.Toast.LENGTH_SHORT).show()
                        // Re-enable inputs
                        etIds.isEnabled = true
                        etPass.isEnabled = true
                        findViewById<Button>(R.id.btnStart).isEnabled = true
                    }
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                sendLocation(location.latitude, location.longitude)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        findViewById<TextView>(R.id.txtStatus).text = "Status: Tracking GPS \nTarget: $SERVER_URL"
        android.widget.Toast.makeText(this, "Started Tracking!", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun sendLocation(lat: Double, lng: Double) {

        val json = JSONObject()
        json.put("bus_id", currentBusId)
        json.put("lat", lat)
        json.put("lng", lng)
        json.put("speed", 30)

        val body = RequestBody.create(
            "application/json".toMediaType(),
            json.toString()
        )

        val request = Request.Builder()
            .url(SERVER_URL)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    findViewById<TextView>(R.id.txtStatus).text = "Error: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                 runOnUiThread {
                     if (response.isSuccessful) {
                         findViewById<TextView>(R.id.txtStatus).text = "Sent: $lat, $lng"
                     } else {
                         findViewById<TextView>(R.id.txtStatus).text = "Server Error: ${response.code}"
                     }
                 }
            }
        })
    }
}

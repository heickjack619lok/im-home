package com.android.imhome.ui.myhome

import android.Manifest
import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.android.imhome.util.BaseActivity
import com.android.imhome.R
import com.android.imhome.databinding.ActivityMyHomeSettingBinding
import com.android.imhome.model.GeoFenceLiveData
import com.android.imhome.geofence.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.NEVER_EXPIRE
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_my_home_setting.*
import java.util.ArrayList
import com.android.imhome.util.PreferencesUtil
import com.android.imhome.util.Util


class MyHomeSettingActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMyHomeSettingBinding
    private var mGeofencePendingIntent: PendingIntent? = null

    companion object {
        const val PERMISSIONS_REQUEST_LOCATION = 99
        const val GEOFENCE_ID: String = "1"

        fun start(context: Context) {
            val intent = Intent(context, MyHomeSettingActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        checkLocationPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_home_setting)
        mBinding.iconIsHomeStatus.isEnabled = Util.isHomed(this)
        toolbar.setNavigationOnClickListener { finish() }

        //show stored information
        mBinding.inputLatitude.setText(PreferencesUtil.getLatitude(this))
        mBinding.inputLongitude.setText(PreferencesUtil.getLongitude(this))
        mBinding.inputEntryRadius.setText(PreferencesUtil.getRadius(this))
        mBinding.inputWifiSsid.setText(PreferencesUtil.getSSID(this))

        mBinding.buttonSave.setOnClickListener {
            val latitude = mBinding.inputLatitude.text.toString().toDoubleOrNull()
            val longitude = mBinding.inputLongitude.text.toString().toDoubleOrNull()
            val radius = mBinding.inputEntryRadius.text.toString().toFloatOrNull()

            //save the Home WiFi SSID
            PreferencesUtil.putSSID(this, mBinding.inputWifiSsid.text.toString())
            mBinding.iconIsHomeStatus.isEnabled = Util.isHomed(this)

            //save the Home Location
            if (latitude != null && longitude != null && radius != null) {
                //only check WiFi connected because location information will be reset
                //if WiFi connected, it is still inHome, else wait for geofence ping
                mBinding.iconIsHomeStatus.isEnabled = Util.isConnectedHomeWiFi(this)
                addGeoFencing(latitude, longitude, radius)
            }
        }

        //listener to get isHome status and display it
        GeoFenceLiveData.getIsHome().observe(this, Observer { mBinding.iconIsHomeStatus.isEnabled = it ?: false })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.location_permission_not_granted), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle(R.string.location_needed_title)
                    .setMessage(R.string.location_needed_desc)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@MyHomeSettingActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    private fun addGeoFencing(latitude: Double, longitude: Double, radius: Float) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //store the latest information
            PreferencesUtil.putLatitude(this, latitude)
            PreferencesUtil.putLongitude(this, longitude)
            PreferencesUtil.putRadius(this, radius)
            PreferencesUtil.putIsHomed(this, false)

            val geofence = Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setExpirationDuration(NEVER_EXPIRE)
                .setCircularRegion(latitude, longitude, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setNotificationResponsiveness(5000)
                .build()

            val geofencingClient = LocationServices.getGeofencingClient(this)
            geofencingClient.addGeofences(getGeofencingRequest(geofence), getGeofencePendingIntent(0))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //geofence added successfully.
                        Toast.makeText(this, getString(R.string.success_added_home), Toast.LENGTH_SHORT).show()
                    } else {
                        //geofence failed to add.
                        Toast.makeText(this, getString(R.string.failed_added_home), Toast.LENGTH_SHORT).show()
                        task.exception!!.printStackTrace()
                    }
                }
        }
    }

    private fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        val geofenceArrayList = ArrayList<Geofence>()
        geofenceArrayList.add(geofence)

        val builder = GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        builder.addGeofences(geofenceArrayList)

        // Return a GeofencingRequest.
        return builder.build()
    }

    private fun getGeofencePendingIntent(id: Long): PendingIntent {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent!!
        }
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        mGeofencePendingIntent = PendingIntent.getBroadcast(
            this,
            java.lang.Long.valueOf(id).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return mGeofencePendingIntent!!
    }
}
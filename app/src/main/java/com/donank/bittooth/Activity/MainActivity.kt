package com.donank.bittooth.Activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.donank.bittooth.BTHelper.BTService
import com.donank.bittooth.Fragments.Splash
import com.donank.bittooth.R
import com.donank.bittooth.Utility.showFragment

class MainActivity : AppCompatActivity() {

    private val REQUEST_ENABLE_BT = 3
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var btService = BTService(this,Handler())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)

        showFragment(
                Fragment.instantiate(
                        this,
                        Splash::class.java.name
                ),
                addToBackStack = false
        )
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = supportFragmentManager,
                addToBackStack = addToBackStack)
    }

    override fun onStart() {
        super.onStart()
        if (!bluetoothAdapter.isEnabled) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)
        }else if(btService == null){
            btService = BTService(MainActivity(), Handler())
        }
    }
}
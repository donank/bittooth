package com.donank.bittooth.Fragments

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.donank.bittooth.BTHelper.BTService
import com.donank.bittooth.R
import com.donank.bittooth.Utility.showFragment
import com.donank.bittooth.Utility.showInSnack
import kotlinx.android.synthetic.main.fragment_dashboard.*




class Dashboard : Fragment() {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var btService: BTService
    private val REQUEST_ENABLE_BT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bluetoothAdapter == null) {
            showInSnack(this.view!!, "Bluetooth support not available! Exiting!!")
            activity!!.finish()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_dashboard,container,false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_config.setOnClickListener {
            showFragment(
                    Fragment.instantiate(
                            this.context,
                         BluetoothDashboard::class.java.name
                    ),
                    addToBackStack = true
            )
        }
    }

    private fun connectDevice(macAddress: String, secure: Boolean) {
        val device = bluetoothAdapter.getRemoteDevice(macAddress)
        btService.connect(device, secure)
    }


    private fun send(data: ByteArray) {
        if (btService.getState() != btService.STATE_CONNECTED) {
            showInSnack(this.view!!,"Device Not Connected!")
            return
        }
        if (data.isNotEmpty()) {
            btService.write(data)
        }
    }


    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }

    override fun onStart() {
        super.onStart()
        if (!bluetoothAdapter.isEnabled) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)
        }else if(btService == null){
            btService = BTService(activity!!, Handler())
        }
    }

    override fun onResume() {
        super.onResume()

        if (btService != null) {
            if (btService.getState() == btService.STATE_NONE) {
                btService.start()
            }
        }
    }

}
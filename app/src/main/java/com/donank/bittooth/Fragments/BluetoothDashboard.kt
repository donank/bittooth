package com.donank.bittooth.Fragments

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.donank.bittooth.BR
import com.donank.bittooth.BTHelper.BTService
import com.donank.bittooth.Data.Device
import com.donank.bittooth.R
import com.donank.bittooth.Utility.showFragment
import com.donank.bittooth.Utility.showInSnack
import com.donank.bittooth.databinding.ItemDeviceBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.fragment_bluetooth_dashboard.*
import kotlinx.android.synthetic.main.item_device.view.*



class BluetoothDashboard : Fragment() {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_CONNECT_DEVICE_INSECURE = 2
    private val REQUEST_ENABLE_BT = 3
    private lateinit var btService: BTService

    private val pairedDevices = ObservableArrayList<Device>()

    private val availableDevices = ObservableArrayList<Device>()

    private val pairedLastAdapter: LastAdapter by lazy {
        initPairedLastAdapter()
    }

    private fun initPairedLastAdapter(): LastAdapter {
        return LastAdapter(pairedDevices, BR.item)
                .map<Device, ItemDeviceBinding>(R.layout.item_device){
                    onBind {
                        it.itemView.btn_pair.visibility = View.GONE
                    }
                }
                .into(recycler_paired_devices)
    }

    private val availableDevicesLastAdapter: LastAdapter by lazy {
        initAvailableDevicesLastAdapter()
    }

    private fun initAvailableDevicesLastAdapter(): LastAdapter {
        return LastAdapter(availableDevices, BR.item)
                .map<Device, ItemDeviceBinding>(R.layout.item_device){
                    onBind {
                        it.itemView.btn_pair.setOnClickListener {v->
                            connectDevice(it.binding.item!!.mac,false)
                            refreshPairedDevices()
                        }
                    }
                }
                .into(recycler_available_devices)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bluetoothAdapter == null) {
            showInSnack(this.view!!, "Bluetooth support not available! Exiting!!")
            activity!!.finish()
        }
        btService = BTService(activity!!, Handler())
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        activity!!.registerReceiver(mReceiver, filter)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_bluetooth_dashboard, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycler_paired_devices.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler_paired_devices.adapter = pairedLastAdapter
        recycler_available_devices.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler_available_devices.adapter = availableDevicesLastAdapter

        switch_toggleBT.isChecked = bluetoothAdapter.isEnabled
        switch_toggleDiscoverable.isEnabled = bluetoothAdapter.isEnabled
        btn_refresh.isEnabled = bluetoothAdapter.isEnabled

        if(bluetoothAdapter.isEnabled){
            refreshPairedDevices()
        }

        if (bluetoothAdapter.scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            switch_toggleDiscoverable.isChecked = true
        }

        switch_toggleBT.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableBt()
            } else {
                disableBt()
            }
        }

        switch_toggleDiscoverable.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                Log.d("ToggleDiscovery - ", "isCheked")
                enableBtDiscoverable()
            }
        }

        btn_refresh.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                if(bluetoothAdapter.isDiscovering){
                    bluetoothAdapter.cancelDiscovery()
                }
                bluetoothAdapter.startDiscovery()
            } else {
                Log.d("bluetoothAdapter - ", "Disabled")
            }
        }

    }

    private fun enableBt() {
        Log.d("ToggleBT - ", "isCheked")
        if (!bluetoothAdapter.isEnabled) {
            Log.d("bluetoothAdapter - ", "turning on")
            startActivityForResult(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    REQUEST_ENABLE_BT
            )
        }
    }

    private fun disableBt() {
        if (bluetoothAdapter.isEnabled) {
            bluetoothAdapter.disable()
            switch_toggleDiscoverable.isChecked = false
            switch_toggleDiscoverable.isEnabled = false
            showInSnack(this.view!!, "Bluetooth Disabled!")
        }
    }

    private fun enableBtDiscoverable(){
        if (bluetoothAdapter.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Handler().postDelayed({
                switch_toggleDiscoverable.isEnabled = false
            }, 3000)
            showInSnack(this.view!!,"Device Discoverable for 300s")
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            startActivity(discoverableIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> if (resultCode == Activity.RESULT_OK) {
                showInSnack(this.view!!, "Bluetooth Enabled!")
                bluetoothAdapter.startDiscovery()
                switch_toggleDiscoverable.isEnabled = true
                if (bluetoothAdapter.scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    switch_toggleDiscoverable.isChecked = true
                }
            } else {
                showInSnack(this.view!!, "Bluetooth Not Enabled!")
                switch_toggleBT.isChecked = false
            }
        }
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    Log.d("Device", "Found")
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if(!device.name.isNullOrEmpty()){
                        availableDevices.clear()
                        val availableDevice = Device(
                                name = device.name,
                                type = device.type,
                                mac = device.address
                        )
                        if(!availableDevices.contains(availableDevice)){
                            availableDevices.add(availableDevice)
                            availableDevicesLastAdapter.notifyDataSetChanged()
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d("Receiver:",action)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("Receiver:",action)
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    Log.d("Receiver:", action)
                }
            }
        }
    }

    private fun refreshPairedDevices(){
        val storedPairedDevices = bluetoothAdapter.bondedDevices
        if (storedPairedDevices.size > 0) {
            storedPairedDevices.forEach {
                pairedDevices.add(Device(
                        name = it.name,
                        type = it.type,
                        mac = it.address
                ))
                pairedLastAdapter.notifyDataSetChanged()
            }
        } else {
            pairedDevices.add(Device(
                    name = "No Paired Devices",
                    type = 0,
                    mac = ""
            ))
            pairedLastAdapter.notifyDataSetChanged()
        }
    }



    private fun connectDevice(macAddress: String, secure: Boolean) {
        val device = bluetoothAdapter.getRemoteDevice(macAddress)
        btService.connect(device, secure)
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }

    override fun onResume() {
        super.onResume()

        if (btService != null) {
            if (btService.getState() == btService.STATE_NONE) {
                btService.start()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (btService != null) {
            btService.stop()
        }
        activity!!.unregisterReceiver(mReceiver)
    }
}


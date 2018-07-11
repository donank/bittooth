package com.donank.bittooth.Fragments

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.donank.bittooth.BR
import com.donank.bittooth.Data.Device
import com.donank.bittooth.R
import com.donank.bittooth.Utility.showFragment
import com.donank.bittooth.Utility.showInSnack
import com.donank.bittooth.databinding.ItemDeviceBinding
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.fragment_bluetooth_dashboard.*

class BluetoothDashboard : Fragment() {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val REQUEST_ENABLE_BT = 1

    private val pairedDevices = ObservableArrayList<Device>()

    private val availableDevices = ObservableArrayList<Device>()

    private val pairedLastAdapter : LastAdapter by lazy {
        initPairedLastAdapter()
    }

    private fun initPairedLastAdapter(): LastAdapter{
        return LastAdapter(pairedDevices, BR.item)
                .map<Device, ItemDeviceBinding>(R.layout.item_device)
                .into(recycler_paired_devices)
    }
    private val availableDevicesLastAdapter : LastAdapter by lazy {
        initAvailableDevicesLastAdapter()
    }

    private fun initAvailableDevicesLastAdapter(): LastAdapter{
        return LastAdapter(availableDevices, BR.item)
                .map<Device, ItemDeviceBinding>(R.layout.item_device)
                .into(recycler_available_devices)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bluetoothAdapter == null) {
            showInSnack(this.view!!,"Bluetooth support not available! Exiting!!")
            activity!!.finish()
        }

        activity!!.registerReceiver(mReceiver,IntentFilter(BluetoothDevice.ACTION_FOUND))
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                availableDevices.add(Device(
                        name = device.name,
                        type = device.type,
                        mac = device.address
                ))
                availableDevicesLastAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_bluetooth_dashboard,container,false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycler_paired_devices.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        recycler_paired_devices.adapter = pairedLastAdapter
        recycler_available_devices.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        recycler_available_devices.adapter = availableDevicesLastAdapter

        if(!bluetoothAdapter.isEnabled){
            switch_toggleBT.isChecked = false
        }

        val storedPairedDevices = bluetoothAdapter.bondedDevices

        if(storedPairedDevices.size > 0){
            storedPairedDevices.forEach {
                pairedDevices.add(Device(
                        name = it.name,
                        type = it.type,
                        mac = it.address
                ))
                pairedLastAdapter.notifyDataSetChanged()
            }
        }else {
            pairedDevices.add(Device(
                    name = "No Paired Devices",
                    type = 0,
                    mac = ""
            ))
            pairedLastAdapter.notifyDataSetChanged()
        }

        switch_toggleBT.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                if(bluetoothAdapter.isEnabled){
                    startActivityForResult(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            REQUEST_ENABLE_BT
                    )
                }
            }else{
                if(!bluetoothAdapter.isEnabled){
                    bluetoothAdapter.disable()
                    showInSnack(this.view!!, "Bluetooth Disabled!")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> if(resultCode == -1){
                showInSnack(this.view!!, "Bluetooth Enabled!")
            }else {
                showInSnack(this.view!!, "Bluetooth Not Enabled!")
                switch_toggleBT.isChecked = false
            }
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(mReceiver)
    }
}


package com.donank.bittooth.Fragments

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.donank.bittooth.Activity.MainActivity
import com.donank.bittooth.R
import com.donank.bittooth.Utility.showFragment
import com.donank.bittooth.Utility.showInSnack
import kotlinx.android.synthetic.main.fragment_bluetooth_dashboard.*

class BluetoothDashboard : Fragment() {

    val bluetoohAdapter = BluetoothAdapter.getDefaultAdapter()

    val REQUEST_ENABLE_BT = 1
    val REQUEST_DISABLE_BT = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bluetoohAdapter == null) {
            showInSnack(this.view!!,"Bluetooth support not available! Exiting!!")
            activity!!.finish()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_bluetooth_dashboard,container,false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(!bluetoohAdapter.isEnabled){
            switch_togleBT.isChecked = false
        }
        switch_togleBT.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                if(!bluetoohAdapter.isEnabled){
                    startActivityForResult(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            REQUEST_ENABLE_BT
                    )
                }
            }else{
                if(bluetoohAdapter.isEnabled){
                    bluetoohAdapter.disable()
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
                switch_togleBT.isChecked = false
            }
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        fragment.showFragment(container = R.id.fragment_container,
                fragmentManager = activity!!.supportFragmentManager,
                addToBackStack = addToBackStack)
    }
}
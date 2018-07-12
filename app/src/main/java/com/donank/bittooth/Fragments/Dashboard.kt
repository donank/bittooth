package com.donank.bittooth.Fragments

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.donank.bittooth.BTHelper.BTService
import com.donank.bittooth.R
import com.donank.bittooth.Utility.showFragment
import com.donank.bittooth.Utility.showInSnack
import kotlinx.android.synthetic.main.fragment_dashboard.*
import android.widget.Toast
import com.donank.bittooth.BTHelper.MessageConstants


class Dashboard : Fragment() {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var btService: BTService
    private val REQUEST_ENABLE_BT = 3
    private val messageConstants = MessageConstants()
    private var mConnectedDeviceName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bluetoothAdapter == null) {
            showInSnack(this.view!!, "Bluetooth support not available! Exiting!!")
            activity!!.finish()
        }
        btService = BTService(activity!!, Handler())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_dashboard, container, false)

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

        btn_send.setOnClickListener {
            val ba = ByteArray(1)
            send(ba.plus(1))
            Log.d("Transfer","1 byte")
        }
    }

    private val mHandler = Handler.Callback {
                val activity = activity
                when (it.what) {
                    messageConstants!!.MESSAGE_STATE_CHANGE -> when (it.arg1) {
                        btService.STATE_CONNECTED -> {
                            Log.d("btService","STATE_CONNECTED")
                            true
                        }
                        btService.STATE_CONNECTING -> {
                            Log.d("btService","STATE_CONNECTING")
                            true
                        }
                        btService.STATE_LISTEN, btService.STATE_NONE -> {
                            Log.d("btService","STATE_LISTEN/NONE")
                            true
                        }
                        else -> {
                            true
                        }
                    }
                    messageConstants.MESSAGE_WRITE -> {
                        val writeData = it.obj as ByteArray
                        Log.d("Sent","$writeData")
                        true
                    }
                    messageConstants.MESSAGE_READ -> {
                        val readData = it.obj as ByteArray
                        Log.d("Recieved","$readData")
                        true
                    }
                    messageConstants.MESSAGE_DEVICE_NAME -> {
                        mConnectedDeviceName = it.data.getString(messageConstants.DEVICE_NAME)
                        if (null != activity) {
                            Toast.makeText(activity, "Connected to $mConnectedDeviceName", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    messageConstants.MESSAGE_TOAST -> {
                        if (null != activity) {
                            Toast.makeText(activity, it.data.getString(messageConstants.TOAST),
                                    Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    else -> {
                        true
                    }
                }
            }


    private fun send(data: ByteArray) {
        if (btService.getState() != btService.STATE_CONNECTED) {
            showInSnack(this.view!!, "Device Not Connected!")
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
        } else if (btService == null) {
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
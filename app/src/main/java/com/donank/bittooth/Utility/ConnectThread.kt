package com.donank.bittooth.Utility

import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothDevice
import android.util.Log
import java.io.IOException
import java.util.*


class ConnectThread:Thread() {
    private val mmBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var mmSocket: BluetoothSocket
    private lateinit var mmDevice: BluetoothDevice

    fun ConnectThread(device: BluetoothDevice) {
        var tmp: BluetoothSocket? = null
        mmDevice = device

        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.randomUUID())
        } catch (e: IOException) {
            Log.e(TAG, "Socket's create() method failed", e)
        }

        mmSocket = tmp!!
    }

    override fun run() {
        mmBluetoothAdapter.cancelDiscovery()

        try {
            mmSocket.connect()
        } catch (connectException: IOException) {
            try {
                mmSocket.close()
            } catch (closeException: IOException) {
                Log.e(TAG, "Could not close the client socket", closeException)
            }

            return
        }
       //manageMyConnectedSocket(mmSocket)
    }
    fun cancel() {
        try {
            mmSocket!!.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }

    }

}
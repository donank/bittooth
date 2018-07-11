package com.donank.bittooth.BTHelper

import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothDevice
import android.util.Log
import java.io.IOException
import java.util.*


class ConnectThread(device: BluetoothDevice, secure: Boolean) :Thread() {
    private val mmBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val mmSocket: BluetoothSocket
    private val mmDevice: BluetoothDevice

    init {
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
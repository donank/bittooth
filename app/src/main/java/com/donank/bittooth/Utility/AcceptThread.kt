package com.donank.bittooth.Utility

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.util.Log
import java.io.IOException
import java.util.*
import android.content.ContentValues.TAG
import android.bluetooth.BluetoothSocket



class AcceptThread : Thread() {

    private val mbluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var serverSocket: BluetoothServerSocket
    init {
        var tmp : BluetoothServerSocket? = null
        try{
            tmp = mbluetoothAdapter.listenUsingRfcommWithServiceRecord("Server", UUID.randomUUID())
        }catch (e: IOException){
            Log.e("AcceptThread:", "Socket's listen() method failed", e);
        }
        serverSocket = tmp!!
    }

    override fun run() {
        var socket: BluetoothSocket
        while (true) {
            try {
                socket = serverSocket.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket's accept() method failed", e)
                break
            }

            if (socket != null) {
                //manageMyConnectedSocket(socket)
                serverSocket.close()
                break
            }
        }
    }

    fun cancel() {
        try {
            serverSocket.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }

    }
}
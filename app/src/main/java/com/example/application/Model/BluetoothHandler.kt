package com.example.application.Model

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import java.lang.Exception
import java.util.*


/*
val UUID: UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
const val MAC_ADDRESS_SAFETY_CONSOLE: String = "98:D3:81:FD:4A:C0"


class BluetoothHandler(context: Context) {

    // CLASS MEMBERS
    var mBluetooth: BluetoothSPP = BluetoothSPP(context)


    // PRIVATE MEMBER FUNCTIONS
    private fun createConnectIntent() : Intent {

        val intent = Intent()
        intent.putExtra(BluetoothState.EXTRA_DEVICE_ADDRESS, MAC_ADDRESS_SAFETY_CONSOLE)

        return intent
    }

    // PUBLIC MEMBER FUNCTIONS
    fun init() {

        try {
            if (!mBluetooth.isServiceAvailable) {
                mBluetooth.setupService()
                mBluetooth.startService(BluetoothState.DEVICE_OTHER)

                val intent = createConnectIntent()
                mBluetooth.connect(intent)
            }

        } catch (e: Exception) {
            Log.e("BluetoothHandler", "Error: $e")
        }
    }

    fun stop() {
        try {
            mBluetooth.stopService()
        } catch (e: Exception) {
            Log.e("BluetoothHandler", "Error: $e")
        }

    }


    fun startListeningForMsg(tvState: TextView) {
        mBluetooth.setOnDataReceivedListener { _, message ->

            when (message) {
                "CHECK-IN" -> {
                    tvState.setBackgroundColor(Color.GREEN)
                    tvState.text = message
                }
                "CHECK-OUT" -> {
                    tvState.setBackgroundColor(Color.RED)
                    tvState.text = message
                }
                else -> {
                    tvState.setBackgroundColor(Color.WHITE)
                    tvState.text = ""
                }
            }
        }
    }

}



}*/
package com.example.application

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var btName: TextView

    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var pairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1


    companion object {
        val EXTRA_ADDRESS: String = "Devices Adress"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)
        btName = findViewById(R.id.bluetooth)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Funkar inte", Toast.LENGTH_SHORT).show()
        }

        if (bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        pairedDevicesList()

    }

    private fun pairedDevicesList() {
        pairedDevices = bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()

        if(pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in pairedDevices) {
                list.add(device)
                Log.d("Test", ""+device)
            }
        } else {
            Toast.makeText(this, "hittade inga", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetoothAdapter!!.isEnabled) {
                    Toast.makeText(this, "enable bluetooth", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "disable bluetooth", Toast.LENGTH_SHORT).show()
                }
            } else if(resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "bluetooth enable have been canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

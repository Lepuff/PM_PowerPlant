package com.example.console


import android.app.ProgressDialog


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*




class ControlActivity: AppCompatActivity() {



    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        lateinit var mHandler : Handler

    }

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.control_layout)
            m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)

            ConnectToDevice(this).execute()










            control_led_on.setOnClickListener { sendCommand("0") }
            control_led_off.setOnClickListener { sendCommand("1") }
            control_led_disconnect.setOnClickListener { disconnect() }

            read_bt.setOnClickListener{


                    run()

            }
    }


    private fun run() {

            val BUFFER_SIZE = 1024
            var buffer = ByteArray(BUFFER_SIZE)
            var bytes = 0
            bytes = m_bluetoothSocket?.inputStream?.read(buffer, 0, 8)!!

            val string = String(buffer)

            Log.i("bytes", bytes.toString())
            Log.i("testtest", string)
            var id: String = ""


            if (string[0] == '1') {
                for (x in 0..7) {
                    id += string[x + 1]
                }

                sendCommand("0")
            }

            Log.i("ID", "USER ID :" + id)
    }









    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }



    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }


    }



}
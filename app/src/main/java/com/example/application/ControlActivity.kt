package com.example.application

import android.app.ProgressDialog


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_control.*
import java.io.IOException
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

    private lateinit var hejButton: Button
    private lateinit var onButton: Button
    private lateinit var offButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)!!

        ConnectToDevice(this).execute()

        hejButton = findViewById(R.id.hej_button)
        onButton = findViewById(R.id.control_led_on)
        offButton = findViewById(R.id.control_led_off)

        hejButton.setOnClickListener {
            Log.d("Test", "HEJ BUTTON")
            Toast.makeText(this, "HEJ BUTTON", Toast.LENGTH_SHORT).show()
            clockInUser()
        }

        onButton.setOnClickListener {


            Log.d("Test", "HEJ BUTTON ON")
            //sendCommand("0")


        }
        offButton.setOnClickListener {
            //sendCommand("1")
            Log.d("Test", "HEJ BUTTON OFF")

        }
        control_led_disconnect.setOnClickListener { disconnect() }

        read_bt.setOnClickListener{
            run()

        }
    }


    private fun clockInUser() {

        val userRef: DocumentReference = FirebaseFirestore.getInstance().collection("User").document("0ePZuN5WcxQ3hZXt43JI0rvbzL63")

        Log.d("Test", "Clock in user")
        Log.d("Test", userRef.toString())

        userRef.get()
            .addOnSuccessListener {documentSnapshot ->
                if(documentSnapshot.exists()) {
                    val username = documentSnapshot.get("username")
                    text_test.text = username.toString()

                    Log.d("Test", username.toString())
                }
            }

        userRef.update(mapOf(
            "clock_In" to true,
            "username" to "Philip Deep"
        ))
            .addOnCompleteListener {task ->
                Log.d("Test", "Clock in update")
                if(task.isComplete) {
                    Toast.makeText(this, "Firebase uppdaterad", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Log.d("Test", "Clock in fail")
                Toast.makeText(this, "funkar inte", Toast.LENGTH_SHORT).show()
            }


    }

    private fun clockOutUser() {

    }


    private fun run() {

        val BUFFER_SIZE = 1024
        var buffer = ByteArray(BUFFER_SIZE)
        var bytes = 0
        bytes = m_bluetoothSocket?.inputStream?.read(buffer, 0, 8)!!

        val string = String(buffer)

        Log.i("Test", bytes.toString())
        Log.i("Test", string)
        var id: String = ""


        if (string[0] == '1') {
            for (x in 0..7) {
                id += string[x + 1]
            }

            sendCommand("0")
        }

        Log.i("Test", "USER ID :" + id)

    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            Log.d("Test", m_bluetoothSocket.toString())
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

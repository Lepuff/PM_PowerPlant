package com.example.application

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.application.Data.Common_2
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {


    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        var m_address: String = "98:D3:81:FD:46:DA"
        var btMessenger : BluetoothMessageThread? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        ConnectToDevice(this).execute()

        startListeningButton.setOnClickListener {
            btMessenger = BluetoothMessageThread(m_bluetoothSocket!!)
            btMessenger!!.start()
            startListeningButton.visibility = View.GONE
        }


        nuclear_technician_button.setOnClickListener {
            startActivity(Intent(this@MainActivity, NuclearTechnicianActivity::class.java))
        }

        power_plant_button.setOnClickListener {
            startActivity(Intent(this@MainActivity, PowerPlantManagerActivity::class.java))
        }

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


    private fun checkInUser(tmpMsg: String) {

        val searchUserRef: CollectionReference = FirebaseFirestore.getInstance().collection("User")

        searchUserRef.get()
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {

                    val querySnapshot: QuerySnapshot = task.result!!

                    if (querySnapshot.isEmpty) {

                    } else {
                        for (document in task.result!!) {

                            if(document.getString("tag_Id") == tmpMsg) {

                                val userRef: DocumentReference = FirebaseFirestore.getInstance().collection("User").document(document.getString("user_Id").toString())

                                userRef.get()
                                    .addOnSuccessListener { documentSnapshot ->

                                        Common_2.currentUserId = documentSnapshot.getString("user_Id")
                                        Common_2.currentUsername = documentSnapshot.getString("username")

                                        if(documentSnapshot.getBoolean("clock_In")!!) {
                                            userRef.update(mapOf(
                                                "clock_In" to false

                                            ))
                                        }
                                        else {
                                            userRef.update(mapOf(
                                                "clock_In" to true

                                            ))
                                        }
                                    }.addOnCompleteListener {
                                        // UPDATE UI
                                       updateUI()
                                    }


                            }
                        }
                    }
                }
            }

    }

    private fun updateUI() {
        nuclear_technician_button.visibility = View.VISIBLE
        power_plant_button.visibility = View.VISIBLE
    }



    inner class BluetoothMessageThread(bluetoothSocket: BluetoothSocket) : Thread(){

        private val mmInStream: InputStream = m_bluetoothSocket?.inputStream!!
        private val mmOutStream: OutputStream = m_bluetoothSocket?.outputStream!!
        private var mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream
        private val TAG = "BluetoothThread"
        override fun run() {
            var numBytes: Int
            var tmp_msg = ""

            while (true) {


                try {
                    // Read from the InputStream
                    numBytes = mmInStream.read(mmBuffer)
                    val readMessage = String(mmBuffer, 0, numBytes)
                    if (readMessage.contains(".")) {
                        tmp_msg += readMessage
                        val string = tmp_msg
                        // Call function to handle login to DB

                        checkInUser(tmp_msg)


                        Log.i("bytes", numBytes.toString())
                        Log.i("buffer", string)

                    } else {
                        tmp_msg += readMessage
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "disconnected", e);

                }
            }
        }


        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
            }
        }



        fun cancel() {
            try {
                m_bluetoothSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            m_bluetoothSocket!!.outputStream.write(input.toByteArray())
        }
    }
    /*
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
    }*/


}

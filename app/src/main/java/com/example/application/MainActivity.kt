package com.example.application

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.application.Data.Common
import com.example.application.Model.TimeStamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var simpleDateFormat: SimpleDateFormat

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


        Handler().postDelayed({
            btMessenger = BluetoothMessageThread(m_bluetoothSocket!!)
            btMessenger!!.start()
        }, 3000)

        ConnectToDevice(this).execute()

        simpleDateFormat = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault())
        localBroadcastManager = LocalBroadcastManager.getInstance(this)

        nuclear_technician_button.setOnClickListener {
            startActivity(Intent(this@MainActivity, NuclearTechnicianActivity::class.java))
        }

    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context = c

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


    private fun checkInUser(id: String) {

        val searchUserRef: CollectionReference = FirebaseFirestore.getInstance().collection("User")

        searchUserRef.get()
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {

                    val querySnapshot: QuerySnapshot = task.result!!

                    if (querySnapshot.isEmpty) {
                        Toast.makeText(this, getString(R.string.no_users), Toast.LENGTH_SHORT).show()
                    } else {
                        for (document in task.result!!) {

                            if(document.getString("tag_Id") == id) {

                                val userRef: DocumentReference = FirebaseFirestore.getInstance().collection("User").document(document.id)

                                userRef.get()
                                    .addOnCompleteListener { task ->
                                        if(task.isSuccessful) {
                                            val documentSnapshot: DocumentSnapshot = task.result!!

                                            Common.currentUserId = documentSnapshot.getString("user_Id")
                                            Common.currentUsername = documentSnapshot.getString("username")
                                            Common.currentRole = documentSnapshot.getBoolean("manager")
                                            Common.room = documentSnapshot.getLong("room")!!.toInt()
                                            Common.hazmatSuitOn = documentSnapshot.getBoolean("hazmatSuit")

                                            if(documentSnapshot.getBoolean("clock_In")!!) {
                                                userRef.update(mapOf(
                                                    "clock_In" to false
                                                ))
                                                Common.ifCheckIn = false
                                            }
                                            else {
                                                userRef.update(mapOf(
                                                    "clock_In" to true
                                                ))
                                                Common.ifCheckIn = true
                                            }
                                            checkWork()
                                        }
                                    }
                            }
                        }
                    }
                }
            }

    }

    private fun checkWork() {

        if(Common.ifCheckIn!!) {
            Common.currentCheckInTime = Timestamp.now().toDate()
        }

        val workRef: DocumentReference = FirebaseFirestore.getInstance()
            .collection("Work").document(Common.currentUserId!!).collection("Date").document(Common.currentCheckInTime!!.toString())

        if(Common.ifCheckIn!!) {
            val timeStamp = TimeStamp(Timestamp(Common.currentCheckInTime), Timestamp(Common.currentCheckInTime), null, null)
            workRef.set(timeStamp)
                .addOnSuccessListener {
                    updateUI()
                    sendCommand("0")
                }
                .addOnCanceledListener {
                    Log.d("Test", "Timestamp funkar ej")
                }
        } else {
            workRef.update(mapOf(
                "clock_Out" to Timestamp.now().toDate(),
                "hours" to 4,
                "radiation_Exposed" to 2
            )).addOnSuccessListener {
                updateUI()
                sendCommand("1")
                val intent = Intent(Common.KEY_DESTROY)
                localBroadcastManager.sendBroadcast(intent)
            }.addOnCanceledListener {
                Log.d("Test", "check out funkar ej")
            }

        }

    }

    private fun updateUI() {
        if(Common.ifCheckIn!!) {
            checkbox.text = getString(R.string.check_out)
            checkbox.setTextColor(Color.WHITE)
            checkbox.setBackgroundColor(Color.RED)
            startActivity(Intent(this@MainActivity, NuclearTechnicianActivity::class.java))
            nuclear_technician_button.visibility = View.VISIBLE
        }
        else {
            checkbox.text = getString(R.string.check_in)
            checkbox.setTextColor(Color.BLACK)
            nuclear_technician_button.visibility = View.GONE
            checkbox.setBackgroundColor(Color.WHITE)

            resetCommonData()
        }
    }

    private fun resetCommonData() {
        Common.currentRole = null
        Common.currentUserId = null
        Common.currentUsername = null
        Common.ifCheckIn = false
        Common.room = null
        Common.hazmatSuitOn = null
        Common.currentCheckInTime = null
        Common.humanExposure = 0.0
        Common.safteyLimit = 500000.0
        Common.isRunning = false
    }

    inner class BluetoothMessageThread(bluetoothSocket: BluetoothSocket) : Thread(){

        private val MESSAGE_ID = '1'
        private val MESSAGE_RADIATION = '2'

        private val mmInStream: InputStream = m_bluetoothSocket?.inputStream!!
        private val mmOutStream: OutputStream = m_bluetoothSocket?.outputStream!!
        private var mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream
        private val TAG = "BluetoothThread"
        override fun run() {
            var numBytes: Int
            var message = ""

            while (true) {

                try {
                    // Read from the InputStream
                    numBytes = mmInStream.read(mmBuffer)
                    val readMessage = String(mmBuffer, 0, numBytes)
                    if (readMessage.contains(".")) {
                        message += readMessage

                        message = message.trim('.')

                        when(message[0]){
                            MESSAGE_ID -> {
                                Log.i(TAG, "Message type : ID")
                                val id = message.substring(1,message.length)

                                Log.i(TAG, "ID: "+id)
                                checkInUser(id)



                            }
                            MESSAGE_RADIATION -> {
                                Log.i(TAG, "Message type : Radiation")
                                var radation = message.substring(1,message.length)
                                Log.i(TAG, "Radiation: "+radation)
                                Common.reactorOutput = radation.toDouble()
                            }
                        }

                        Log.d("num of bytes:", numBytes.toString())
                        Log.d("buffer:", mmBuffer.toString())

                        message = ""
                    } else {
                        message += readMessage
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

    fun sendCommand(input: String) {
        val TAG = "BT_sendCommand"
        try {
            if (m_bluetoothSocket != null) {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            }
        }catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
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


}

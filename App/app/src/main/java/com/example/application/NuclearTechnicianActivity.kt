package com.example.application

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import kotlinx.android.synthetic.main.activity_nuclear_technician.*
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.RemoteViews
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.bluetooth.BluetoothDevice
import android.os.AsyncTask
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.TimeUnit

import android.app.ProgressDialog
import android.view.View


class NuclearTechnicianActivity : AppCompatActivity() {




    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "com.example.application"
    private val description = "Test notification"


    var db = FirebaseFirestore.getInstance()
    //private var contentView = RemoteViews(packageName,R.layout.activity_notification)
    var safteyLimit = 500000.0

    private var rcBreakRoom = 0.1
    private var rcControlRoom = 0.5
    private var rcReactorRoom = 1.6

    var hazmatSuitOn = ""
    val pcHazmatSuit = 5.0
    val pcClothes = 1.0

    var room = ""
    var rC = 0.0
    var pC = pcClothes

    val reactorOutput = 30.0
    var humanExposure = 0.0
    var timeLeft = 0.0
    var millisInFuture: Long = 0
    val countDownInterval: Long = 1000
    var timeRemaining = ""

    var untilFinished: Long = 0

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        var BT_messenger : BluetoothMessageThread? = null
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuclear_technician)
        m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)
        ConnectToDevice(this).execute()




        startListeningButton.setOnClickListener {
            BT_messenger = BluetoothMessageThread(m_bluetoothSocket!!)
            BT_messenger!!.start()
            startListeningButton.visibility = View.GONE
        }





        fun getRoom() {
            val ref = db.collection("/User").document("/0ePZuN5WcxQ3hZXt43JI0rvbzL63")
            ref.get()
                .addOnSuccessListener { task ->
                    if (task.exists()) {
                        room = task.getString("room").toString()
                        Log.d("Test", room)
                    }

                }
                .addOnFailureListener { exception ->
                    Log.d("Test", "get failed with ", exception)
                }

        }

        fun isHazmatSuitOn() {
            val ref = db.collection("/User").document("/0ePZuN5WcxQ3hZXt43JI0rvbzL63")
            ref.get()
                .addOnSuccessListener { task ->
                    if (task.exists()) {
                        hazmatSuitOn = task.getString("hazmatSuit").toString()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Test", "get failed with ", exception)
                }

        }

        fun startTimer(){
            timeLeft = (safteyLimit / (reactorOutput * rC) / pcClothes) * 1000
            millisInFuture = timeLeft.toLong()
            timer(millisInFuture, countDownInterval).start()
            Log.d("Test",timeRemaining)
        }

        btn_ts.setOnClickListener {
            getRoom()
            isHazmatSuitOn()
            if(hazmatSuitOn == "1"){
                pC = pcHazmatSuit
            }
            if(room == "1"){
                rC = rcBreakRoom
                startTimer()
            }
            if(room == "2"){
                rC = rcControlRoom
                startTimer()
            }
            if(room == "3"){
                rC = rcReactorRoom
                startTimer()
            }
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    }



    private fun timer(millisInFuture:Long,countDownInterval:Long):CountDownTimer{
        return object: CountDownTimer(millisInFuture,countDownInterval){

            override fun onTick(millisUntilFinished: Long){

                //Break Room
                if(room == "1"){
                    timeLeft = (safteyLimit / (reactorOutput * rC) / pcClothes) *1000
                    untilFinished = timeLeft.toLong()
                }

                //Control Room
                else if(room == "2"){
                    timeLeft = (safteyLimit / (reactorOutput * rC) / pcClothes)*1000
                    untilFinished = timeLeft.toLong()
                }

                //Reactor Room
                else if(room == "3"){
                    timeLeft = (safteyLimit / (reactorOutput * rC) / pcClothes) *1000
                    untilFinished = timeLeft.toLong()
                }
                else{
                    //outside powerplant
                }

                if (timeRemaining == "01 day: 22 hour: 17 min: 40 sec"){
                    notification()
                }
                if (timeRemaining == "01 day: 22 hour: 17 min: 20 sec"){
                    notification()
                }
                if (timeRemaining == "00 day: 01 hour: 00 min: 00 sec"){
                    notification()
                }
                if (timeRemaining == "00 day: 00 hour: 30 min: 00 sec"){
                    notification()
                }
                if (timeRemaining == "00 day: 00 hour: 10 min: 00 sec"){
                    notification()
                }
                if (timeRemaining == "00 day: 00 hour: 05 min: 00 sec"){
                    notification()
                }
                if (timeRemaining == "00 day: 00 hour: 01 min: 00 sec"){
                    notification()
                }

                timeRemaining = timeString(untilFinished)
                txt_radiation_time.text = timeRemaining
                humanExposure += (reactorOutput * rC) / pcClothes
                safteyLimit -= (reactorOutput * rC) / pcClothes
                txt_unit.text = humanExposure.toString()
                txt_safety_limit.text = safteyLimit.toString()
                txt_room_number.text = room
            }

            override fun onFinish() {
                val builder = AlertDialog.Builder(this@NuclearTechnicianActivity)
                builder.setTitle("WARNING")
                builder.setMessage("GET OUT NOW")
                builder.setNeutralButton("Ok", {dialog, which -> })
                val dialog: AlertDialog = builder.create()
                dialog.show()

            }
        }

    }

    // Method to get days hours minutes seconds from milliseconds
    private fun timeString(millisUntilFinished:Long):String{

        var millisUntilFinished:Long = millisUntilFinished

        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

        // Format the string
        return String.format(
            Locale.getDefault(),
            "%02d day: %02d hour: %02d min: %02d sec",
            days, hours, minutes,seconds
        )
    }



    private fun notification(){

            val intent = Intent(this,NuclearTechnicianActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

            val contentView = RemoteViews(packageName,R.layout.activity_notification)

            contentView.setTextViewText(R.id.tv_title,"Alert")
            contentView.setTextViewText(R.id.tv_content,"You have " + timeRemaining + "  left")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(this,channelId)
                    .setContent(contentView)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.ic_launcher_foreground))
                    .setContentIntent(pendingIntent)
            }else{

                builder = Notification.Builder(this)
                    .setContent(contentView)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.ic_launcher_foreground))
                    .setContentIntent(pendingIntent)
            }
            notificationManager.notify(1234,builder.build())

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
    public inner class BluetoothMessageThread(bluetoothSocket: BluetoothSocket) : Thread(){

        val MESSAGE_ID = '1'
        val MESSAGE_RADIATION = '2'

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
                                var id = message.substring(1,message.length)

                                Log.i(TAG, "ID: "+id)
                                //TODO handle the id log in/out event

                            }
                            MESSAGE_RADIATION -> {
                                //TODO handle the radiation change
                                Log.i(TAG, "Message type : Radiation")
                                var radation = message.substring(1,message.length)
                                Log.i(TAG, "Radiation: "+radation)
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



    private fun sendCommand(input: String) {
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

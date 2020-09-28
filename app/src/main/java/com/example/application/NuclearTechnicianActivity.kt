package com.example.application

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import kotlinx.android.synthetic.main.activity_main.*

import java.util.*
import java.util.concurrent.TimeUnit



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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuclear_technician)





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


}

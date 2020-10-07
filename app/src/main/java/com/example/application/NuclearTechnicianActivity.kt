package com.example.application
import android.app.*
import android.content.BroadcastReceiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import kotlinx.android.synthetic.main.activity_nuclear_technician.*

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import java.util.*
import java.util.concurrent.TimeUnit
import com.example.application.Data.Common
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.String





class NuclearTechnicianActivity : AppCompatActivity() {

    private lateinit var notificationManager : NotificationManager
    private lateinit var notificationChannel : NotificationChannel
    private lateinit var builder : Notification.Builder
    private val channelId = "com.example.application"
    private val description = "Test notification"
    private var mainActivity = MainActivity()
    private lateinit var localBroadcastManager: LocalBroadcastManager

    private val checkOutReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Common.countdown_timer!!.cancel()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuclear_technician)



        if(Common.hazmatSuitOn == true){
            Common.pC = Common.pcHazmatSuit
        }

        when (Common.room){
            1 -> { Common.rC = Common.rcBreakRoom
                startTimer()}
            2 -> {Common.rC = Common.rcControlRoom
                startTimer()}
            3 -> {Common.rC = Common.rcReactorRoom
                startTimer()}
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(checkOutReceiver, IntentFilter(Common.KEY_DESTROY))
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        timeStamp_button.setOnClickListener {
            if(Common.currentRole!!) {
                startActivity(Intent(this@NuclearTechnicianActivity, PowerPlantManagerActivity::class.java))
            }else{
                startActivity(Intent(this@NuclearTechnicianActivity, TimeStampActivity::class.java))
            }
        }

        setUI()
    }

    private fun setUI() {

        if(Common.currentRole!!) {
            mainText.text = "Power plant Manager"
            timeStamp_button.text = "Nuclear User"
        }
        else {
            mainText.text = "Nuclear Technician"
            timeStamp_button.text = "TimeStamps"
        }

    }

    fun timer(millisInFuture:Long,countDownInterval:Long) {
        Common.countdown_timer = object : CountDownTimer(millisInFuture,countDownInterval) {
            override fun onFinish() {
                val builder = AlertDialog.Builder(this@NuclearTechnicianActivity)
                builder.setTitle("WARNING")
                builder.setMessage("GET OUT NOW")
                builder.setNeutralButton("Ok", {dialog, which -> })
                val dialog: AlertDialog = builder.create()
                dialog.show()
                mainActivity.sendCommand("2")
            }

            override fun onTick(p0: Long) {
                whichRoom()
                notificationInterval()

                Common.timeRemaining = timeString(Common.untilFinished)
                txt_radiation_time.text = Common.timeRemaining
                Common.humanExposure += (Common.reactorOutput * Common.rC) / Common.pcClothes
                Common.safteyLimit -= (Common.reactorOutput * Common.rC) / Common.pcClothes

                txt_unit.text = String.format("%.1f", Common.humanExposure)
                txt_safety_limit.text = String.format("%.1f", Common.safteyLimit)
                txt_room_number.text = Common.room.toString()
            }
        }


        Common.countdown_timer!!.start()

        Common.isRunning = true
    }

    private fun startTimer(){
        Common.timeLeft = (Common.safteyLimit / (Common.reactorOutput * Common.rC) / Common.pcClothes) * 1000
        Common.millisInFuture = Common.timeLeft.toLong()
        timer(Common.millisInFuture, Common.countDownInterval)
        Log.d("Test",Common.timeRemaining)
    }


    private fun whichRoom(){
        when (Common.room) {
            1 -> {Common.timeLeft = (Common.safteyLimit / (Common.reactorOutput * Common.rC) / Common.pcClothes) *1000
                Common.untilFinished = Common.timeLeft.toLong()}
            2 -> {Common.timeLeft = (Common.safteyLimit / (Common.reactorOutput * Common.rC) / Common.pcClothes)*1000
                Common.untilFinished = Common.timeLeft.toLong()}
            3 -> {Common.timeLeft = (Common.safteyLimit / (Common.reactorOutput * Common.rC) / Common.pcClothes) *1000
                Common.untilFinished = Common.timeLeft.toLong()}
        }
    }

    private fun notificationInterval(){

        when (Common.timeRemaining) {
            "01 day: 22 hour: 17 min: 40 sec" -> notification()
            "01 day: 22 hour: 17 min: 25 sec" -> notification()
            "00 day: 01 hour: 00 min: 00 sec" -> notification()
            "00 day: 00 hour: 30 min: 00 sec" -> notification()
            "00 day: 00 hour: 10 min: 00 sec" -> notification()
            "00 day: 00 hour: 05 min: 00 sec" -> notification()
            "00 day: 00 hour: 01 min: 00 sec" -> notification()
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

        contentView.setTextViewText(R.id.tv_title,"Warning")
        contentView.setTextViewText(R.id.tv_content,"You have " + Common.timeRemaining + "  left")

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

    override fun onDestroy() {
        Common.countdown_timer!!.cancel()
        super.onDestroy()
    }

}



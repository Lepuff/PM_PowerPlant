package com.example.application

import kotlinx.android.synthetic.main.activity_nuclear_technician.*
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



import android.graphics.Color
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot

import java.util.*
import java.util.concurrent.TimeUnit



class NuclearTechnicianActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()

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
                builder.setMessage("GET OUT AND CHECK OUT NOW")
                //setBackgroundColor(Color.RED)
                builder.setNeutralButton("Ok", {dialog, which -> })
                val dialog: AlertDialog = builder.create()
                dialog.show()

                // button_start.isEnabled = true

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


}

package com.example.application.Data

import android.os.CountDownTimer

class Common {


    companion object {

        lateinit var countdown_timer: CountDownTimer
        var currentUserId: String? = null
        var currentUsername: String? = null
        var currentRole: Boolean? = null
        var ifCheckIn: Boolean? = null
        var room: Int? = null
        var hazmatSuitOn: Boolean? = null

        var safteyLimit: Double = 500000.0
        val rcBreakRoom: Double = 0.1
        val rcControlRoom: Double = 0.5
        val rcReactorRoom: Double = 1.6

        val pcHazmatSuit: Double = 5.0
        val pcClothes: Double = 1.0

        var rC: Double = 0.0
        var pC = pcClothes

        var reactorOutput: Double = 30.0
        var humanExposure: Double = 0.0
        var timeLeft: Double = 0.0
        var millisInFuture: Long = 0
        val countDownInterval: Long = 1000
        var timeRemaining: String = ""

        var isRunning: Boolean = false;
        var untilFinished: Long = 0

    }


}
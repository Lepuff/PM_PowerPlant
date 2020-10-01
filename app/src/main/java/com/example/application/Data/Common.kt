
package com.example.application.Common

class Common {

    companion object{

        var safteyLimit: Double = 500000.0
        val rcBreakRoom: Double = 0.1
        val rcControlRoom: Double = 0.5
        val rcReactorRoom: Double = 1.6

        var hazmatSuitOn: Boolean? = null
        val pcHazmatSuit: Double = 5.0
        val pcClothes: Double = 1.0

        var room: Int? = null
        var rC: Double = 0.0
        var pC = pcClothes

        var reactorOutput: Double = 30.0
        var humanExposure: Double = 0.0
        var timeLeft: Double = 0.0
        var millisInFuture: Long = 0
        val countDownInterval: Long = 1000
        var timeRemaining: String = ""

        var untilFinished: Long = 0
    }

}
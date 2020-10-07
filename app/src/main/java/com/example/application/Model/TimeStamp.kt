package com.example.application.Model


import com.google.firebase.Timestamp
import java.util.*

@Suppress("SelfReferenceConstructorParameter")
class TimeStamp(val clock_In: Timestamp?, val clock_Out: Timestamp?, val hours: Double?, val radiation_Exposed: Double? ) {
}

class TimeStamps(val clock_In: String?, val clock_Out: String?, val hours: Double?, val radiation_Exposed: Double? ) {
}